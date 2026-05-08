import { useEffect, useMemo, useState } from "react";
import { NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import { getMyProfile } from "../services/userApi";
import { logoutUser } from "../services/authService";

const primaryNav = [
  { label: "Dashboard", to: "/home", badge: "DB" },
  { label: "Analytics", to: "/analytics", badge: "AN" },
  { label: "Study Plans", to: "/study-plans", badge: "SP" },
  { label: "My Progress", to: "/my-progress", badge: "PG" },
  { label: "Coding Practice", to: "/problems", badge: "CP" },
  { label: "Quiz Center", to: "/quiz", badge: "QZ" },
  { label: "Submissions", to: "/submissions", badge: "SB" },
];

const secondaryNav = [
  { label: "My Account", to: "/account", badge: "AC" },
  { label: "Settings", to: "/settings", badge: "ST" },
  { label: "Billing", to: "/billing", badge: "BL" },
  { label: "Help & Support", to: "/support", badge: "HP" },
];

const navLinkBase =
  "group flex items-center gap-3 rounded-2xl px-3 py-2.5 text-sm font-medium transition focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-teal-400/20";

const getNavLinkClass = ({ isActive }) =>
  `${navLinkBase} ${
    isActive
      ? "bg-teal-400/12 text-white ring-1 ring-teal-300/20"
      : "text-slate-400 hover:bg-white/5 hover:text-white"
  }`;

export default function UserShell() {
  const navigate = useNavigate();
  const location = useLocation();
  const [profile, setProfile] = useState(null);
  const [loadingProfile, setLoadingProfile] = useState(true);
  const [profileError, setProfileError] = useState("");
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [profileMenuOpen, setProfileMenuOpen] = useState(false);

  const initials = useMemo(() => {
    const source =
      profile?.displayName || profile?.username || profile?.email || "User";
    return source
      .split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase())
      .join("");
  }, [profile]);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login", { replace: true });
      return;
    }

    const loadProfile = async () => {
      try {
        const data = await getMyProfile();
        setProfile(data);
      } catch (err) {
        if (err.response?.status === 401) {
          logoutUser();
          navigate("/login", { replace: true });
          return;
        }

        setProfileError("Profile details are temporarily unavailable.");
      } finally {
        setLoadingProfile(false);
      }
    };

    loadProfile();
  }, [navigate]);

  useEffect(() => {
    setMobileNavOpen(false);
    setProfileMenuOpen(false);
  }, [location.pathname]);

  const handleLogout = () => {
    logoutUser();
    navigate("/login", { replace: true });
  };

  const renderNavGroup = (items) => (
    <div className="space-y-1">
      {items.map((item) => {
        return (
          <NavLink key={item.to} to={item.to} className={getNavLinkClass}>
            <span className="flex h-8 w-8 items-center justify-center rounded-full bg-white/[0.04] text-[11px] font-semibold text-slate-400 transition group-hover:text-slate-200">
              {item.badge}
            </span>
            <span>{item.label}</span>
          </NavLink>
        );
      })}
    </div>
  );

  return (
    <div className="min-h-screen bg-transparent">
      <div className="flex min-h-screen">
        <aside className="hidden w-72 flex-col border-r border-white/10 bg-slate-950/65 px-5 py-6 backdrop-blur-xl lg:flex">
          <div className="mb-8">
            <div className="badge-neutral">RankX</div>
            <h1 className="mt-4 text-2xl font-semibold tracking-tight text-white">
              User workspace
            </h1>
            <p className="mt-2 text-sm leading-6 text-slate-400">
              Practice, review progress, and manage your account from one place.
            </p>
          </div>

          <div className="space-y-8">
            <div>
              <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">
                Work
              </p>
              {renderNavGroup(primaryNav)}
            </div>

            <div>
              <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">
                Account
              </p>
              {renderNavGroup(secondaryNav)}
            </div>
          </div>

          <div className="mt-auto space-y-3 pt-6">
            <div className="surface-card-soft">
              <p className="text-xs uppercase tracking-[0.18em] text-slate-500">
                Signed in as
              </p>
              <p className="mt-2 text-sm font-medium text-white">
                {profile?.displayName || profile?.username || "RankX User"}
              </p>
              <p className="mt-1 text-xs text-slate-400">
                {profile?.email || (loadingProfile ? "Loading profile..." : "User account")}
              </p>
            </div>

            <button onClick={handleLogout} className="btn-secondary w-full justify-start">
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
                  aria-label="Open navigation menu"
                >
                  Menu
                </button>

                <div>
                  <p className="text-xs uppercase tracking-[0.22em] text-slate-500">
                    RankX Platform
                  </p>
                  <p className="text-sm font-medium text-white">
                    {profileError || "Focused practice and account control"}
                  </p>
                </div>
              </div>

              <div className="relative">
                <button
                  type="button"
                  onClick={() => setProfileMenuOpen((value) => !value)}
                  className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/[0.04] px-3 py-2 text-left transition hover:bg-white/[0.07] focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-teal-400/20"
                  aria-expanded={profileMenuOpen}
                  aria-haspopup="menu"
                >
                  <span className="flex h-10 w-10 items-center justify-center rounded-full bg-teal-400/12 text-sm font-semibold text-teal-200">
                    {initials || "U"}
                  </span>
                  <span className="hidden sm:block">
                    <span className="block text-sm font-medium text-white">
                      {profile?.displayName || profile?.username || "RankX User"}
                    </span>
                    <span className="block text-xs text-slate-400">
                      {profile?.role?.replace("ROLE_", "") || "Member"}
                    </span>
                  </span>
                  <span className="hidden text-xs uppercase tracking-[0.18em] text-slate-500 sm:block">
                    Menu
                  </span>
                </button>

                {profileMenuOpen ? (
                  <div
                    role="menu"
                    className="absolute right-0 mt-3 w-64 rounded-3xl border border-white/10 bg-slate-950/96 p-2 shadow-[0_24px_60px_rgba(2,8,23,0.42)] backdrop-blur-xl"
                  >
                    <NavLink to="/account" className={getNavLinkClass} role="menuitem">
                      <span>My account</span>
                    </NavLink>
                    <NavLink to="/settings" className={getNavLinkClass} role="menuitem">
                      <span>Settings</span>
                    </NavLink>
                    <NavLink to="/billing" className={getNavLinkClass} role="menuitem">
                      <span>Billing</span>
                    </NavLink>
                    <button
                      type="button"
                      onClick={handleLogout}
                      className={`${navLinkBase} w-full text-slate-400 hover:bg-white/5 hover:text-white`}
                      role="menuitem"
                    >
                      <span>Logout</span>
                    </button>
                  </div>
                ) : null}
              </div>
            </div>
          </header>

          <main className="flex-1 px-4 py-6 sm:px-6">
            <Outlet context={{ profile, loadingProfile, profileError, onLogout: handleLogout }} />
          </main>
        </div>
      </div>

      {mobileNavOpen ? (
        <div className="fixed inset-0 z-40 lg:hidden">
          <button
            type="button"
            className="absolute inset-0 bg-slate-950/70 backdrop-blur-sm"
            onClick={() => setMobileNavOpen(false)}
            aria-label="Close navigation menu"
          />
          <div className="absolute inset-y-0 left-0 flex w-full max-w-xs flex-col border-r border-white/10 bg-slate-950 p-5 shadow-2xl">
            <div className="mb-6 flex items-center justify-between">
              <div>
                <div className="badge-neutral">RankX</div>
                <p className="mt-3 text-sm text-slate-400">Navigation</p>
              </div>
              <button
                type="button"
                onClick={() => setMobileNavOpen(false)}
                className="btn-ghost"
                aria-label="Close navigation"
              >
                Close
              </button>
            </div>

            <div className="space-y-8 overflow-y-auto">
              <div>
                <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">
                  Work
                </p>
                {renderNavGroup(primaryNav)}
              </div>
              <div>
                <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">
                  Account
                </p>
                {renderNavGroup(secondaryNav)}
              </div>
            </div>

            <div className="mt-auto pt-6">
              <button onClick={handleLogout} className="btn-secondary w-full justify-start">
                Logout
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
