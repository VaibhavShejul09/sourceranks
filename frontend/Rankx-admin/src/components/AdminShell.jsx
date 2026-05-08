import { useEffect, useMemo, useState } from "react";
import { NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import {
  FaBars,
  FaChartBar,
  FaChartLine,
  FaChevronDown,
  FaClipboardList,
  FaCog,
  FaCreditCard,
  FaHeadset,
  FaLayerGroup,
  FaSignOutAlt,
  FaTimes,
  FaUsers,
} from "react-icons/fa";
import { logoutUser } from "../services/authService";

const navigationGroups = [
  {
    label: "Overview",
    items: [
      { label: "Dashboard overview", to: "/admin/dashboard", icon: FaChartBar },
      { label: "KPI dashboard", to: "/admin/analytics/kpis", icon: FaChartLine },
      { label: "Problem analytics", to: "/admin/analytics/problems", icon: FaChartLine },
      { label: "Quiz analytics", to: "/admin/analytics/quizzes", icon: FaChartLine },
      { label: "Question analytics", to: "/admin/analytics/questions", icon: FaChartLine },
      { label: "Quiz management", to: "/quizzes", icon: FaClipboardList },
      { label: "Create quiz", to: "/quizzes/create", icon: FaLayerGroup },
    ],
  },
  {
    label: "Operations",
    items: [
      { label: "Users management", to: "/admin/users", icon: FaUsers },
      { label: "Plans & subscriptions", to: "/admin/plans", icon: FaLayerGroup },
      { label: "Payments & billing", to: "/admin/payments", icon: FaCreditCard },
      { label: "Reports & analytics", to: "/admin/reports", icon: FaChartBar },
      { label: "Support & tickets", to: "/admin/support", icon: FaHeadset },
      { label: "Settings", to: "/admin/settings", icon: FaCog },
    ],
  },
];

const navLinkBase =
  "group flex items-center gap-3 rounded-2xl px-3 py-2.5 text-sm font-medium transition focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-sky-400/20";

const getNavLinkClass = ({ isActive }) =>
  `${navLinkBase} ${
    isActive
      ? "bg-sky-400/12 text-white ring-1 ring-sky-300/20"
      : "text-slate-400 hover:bg-white/5 hover:text-white"
  }`;

const readAdminName = () => {
  const role = localStorage.getItem("role");
  return role === "ROLE_ADMIN" ? "Platform Admin" : "Admin Operator";
};

export default function AdminShell() {
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [profileMenuOpen, setProfileMenuOpen] = useState(false);
  const [adminName] = useState(readAdminName);

  const initials = useMemo(
    () =>
      adminName
        .split(" ")
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0]?.toUpperCase())
        .join(""),
    [adminName]
  );

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (!token || (role && !["ROLE_ADMIN", "ADMIN"].includes(role))) {
      logoutUser();
      navigate("/login", { replace: true });
    }
  }, [navigate]);

  useEffect(() => {
    setMobileNavOpen(false);
    setProfileMenuOpen(false);
  }, [location.pathname]);

  const handleLogout = () => {
    logoutUser();
    navigate("/login", { replace: true });
  };

  const renderNavigation = () => (
    <div className="space-y-8">
      {navigationGroups.map((group) => (
        <div key={group.label}>
          <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">
            {group.label}
          </p>
          <div className="space-y-1">
            {group.items.map((item) => {
              const Icon = item.icon;

              return (
                <NavLink key={item.to} to={item.to} className={getNavLinkClass}>
                  <Icon className="text-sm text-slate-500 transition group-hover:text-slate-300" />
                  <span>{item.label}</span>
                </NavLink>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );

  return (
    <div className="min-h-screen bg-transparent">
      <div className="flex min-h-screen">
        <aside className="hidden w-80 flex-col border-r border-white/10 bg-slate-950/70 px-5 py-6 backdrop-blur-xl lg:flex">
          <div className="mb-8">
            <div className="badge-neutral">RankX Admin</div>
            <h1 className="mt-4 text-2xl font-semibold tracking-tight text-white">
              Management console
            </h1>
            <p className="mt-2 text-sm leading-6 text-slate-400">
              Operate content, billing, support, and platform management from one
              complete control surface.
            </p>
          </div>

          {renderNavigation()}

          <div className="mt-auto space-y-3 pt-6">
            <div className="surface-card-soft">
              <p className="text-xs uppercase tracking-[0.18em] text-slate-500">
                Signed in as
              </p>
              <p className="mt-2 text-sm font-medium text-white">{adminName}</p>
              <p className="mt-1 text-xs text-slate-400">Administrative access</p>
            </div>

            <button onClick={handleLogout} className="btn-secondary w-full justify-start">
              <FaSignOutAlt />
              Logout
            </button>
          </div>
        </aside>

        <div className="flex min-h-screen flex-1 flex-col">
          <header className="sticky top-0 z-30 border-b border-white/10 bg-slate-950/70 px-4 py-3 backdrop-blur-xl sm:px-6">
            <div className="flex items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <button
                  type="button"
                  onClick={() => setMobileNavOpen(true)}
                  className="btn-secondary px-3 lg:hidden"
                  aria-label="Open admin navigation"
                >
                  <FaBars />
                </button>

                <div>
                  <p className="text-xs uppercase tracking-[0.22em] text-slate-500">
                    Platform Control
                  </p>
                  <p className="text-sm font-medium text-white">
                    Administrative workflows and management actions
                  </p>
                </div>
              </div>

              <div className="relative">
                <button
                  type="button"
                  onClick={() => setProfileMenuOpen((value) => !value)}
                  className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/[0.04] px-3 py-2 text-left transition hover:bg-white/[0.07] focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-sky-400/20"
                  aria-expanded={profileMenuOpen}
                  aria-haspopup="menu"
                >
                  <span className="flex h-10 w-10 items-center justify-center rounded-full bg-sky-400/12 text-sm font-semibold text-sky-200">
                    {initials || "AD"}
                  </span>
                  <span className="hidden sm:block">
                    <span className="block text-sm font-medium text-white">{adminName}</span>
                    <span className="block text-xs text-slate-400">Admin workspace</span>
                  </span>
                  <FaChevronDown className="hidden text-slate-500 sm:block" />
                </button>

                {profileMenuOpen ? (
                  <div
                    role="menu"
                    className="absolute right-0 mt-3 w-64 rounded-3xl border border-white/10 bg-slate-950/96 p-2 shadow-[0_24px_60px_rgba(2,8,23,0.42)] backdrop-blur-xl"
                  >
                    <NavLink to="/admin/settings" className={getNavLinkClass} role="menuitem">
                      <FaCog className="text-sm text-slate-500" />
                      <span>Settings</span>
                    </NavLink>
                    <NavLink to="/admin/support" className={getNavLinkClass} role="menuitem">
                      <FaHeadset className="text-sm text-slate-500" />
                      <span>Support & tickets</span>
                    </NavLink>
                    <button
                      type="button"
                      onClick={handleLogout}
                      className={`${navLinkBase} w-full text-slate-400 hover:bg-white/5 hover:text-white`}
                      role="menuitem"
                    >
                      <FaSignOutAlt className="text-sm text-slate-500" />
                      <span>Logout</span>
                    </button>
                  </div>
                ) : null}
              </div>
            </div>
          </header>

          <main className="flex-1 px-4 py-6 sm:px-6">
            <Outlet context={{ onLogout: handleLogout, adminName }} />
          </main>
        </div>
      </div>

      {mobileNavOpen ? (
        <div className="fixed inset-0 z-40 lg:hidden">
          <button
            type="button"
            className="absolute inset-0 bg-slate-950/70 backdrop-blur-sm"
            onClick={() => setMobileNavOpen(false)}
            aria-label="Close admin navigation"
          />
          <div className="absolute inset-y-0 left-0 flex w-full max-w-xs flex-col border-r border-white/10 bg-slate-950 p-5 shadow-2xl">
            <div className="mb-6 flex items-center justify-between">
              <div>
                <div className="badge-neutral">RankX Admin</div>
                <p className="mt-3 text-sm text-slate-400">Navigation</p>
              </div>
              <button
                type="button"
                onClick={() => setMobileNavOpen(false)}
                className="btn-ghost"
                aria-label="Close admin navigation"
              >
                <FaTimes />
              </button>
            </div>

            <div className="overflow-y-auto">{renderNavigation()}</div>

            <div className="mt-auto pt-6">
              <button onClick={handleLogout} className="btn-secondary w-full justify-start">
                <FaSignOutAlt />
                Logout
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
