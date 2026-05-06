import { useEffect, useState } from "react";
import { useNavigate, useOutletContext } from "react-router-dom";
import { motion } from "framer-motion";
import DashboardChecklist from "../components/DashboardChecklist";
import RecommendedActionCard from "../components/RecommendedActionCard";
import { getMyDashboardSummary, getMyProfile } from "../services/userApi";
import { getMyResults } from "../services/resultApi";
import { getMyRecentSubmissions } from "../services/submissionApi";
import { logoutUser } from "../services/authService";

const practiceCards = [
  {
    title: "Quiz Practice",
    description:
      "Review your quiz performance and continue improving by topic.",
    route: "/quiz",
    gradient: "from-blue-600 to-indigo-600",
  },
  {
    title: "Coding Practice",
    description:
      "Return to coding challenges and monitor your recent submissions.",
    route: "/problems",
    gradient: "from-emerald-600 to-green-600",
  },
];

const formatTimestamp = (value) => {
  if (!value) {
    return "Recently";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "Recently";
  }

  return date.toLocaleString();
};

export default function Home() {
  const navigate = useNavigate();
  const shellContext = useOutletContext();
  const [profile, setProfile] = useState(null);
  const [summary, setSummary] = useState(null);
  const [results, setResults] = useState([]);
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const loadDashboard = async () => {
      try {
        const [profileData, summaryData, resultsResponse, submissionData] = await Promise.all([
          getMyProfile(),
          getMyDashboardSummary(),
          getMyResults(),
          getMyRecentSubmissions(),
        ]);

        if (!summaryData?.onboardingCompleted) {
          navigate("/onboarding", { replace: true });
          return;
        }

        setProfile(profileData);
        setSummary(summaryData);
        setResults(Array.isArray(resultsResponse.data) ? resultsResponse.data : []);
        setSubmissions(Array.isArray(submissionData) ? submissionData : []);
      } catch (err) {
        if (err.response?.status === 401) {
          logoutUser();
          navigate("/login");
          return;
        }

        setError("We could not load your dashboard right now.");
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, [navigate]);

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center">
        <p className="text-slate-300">Loading your dashboard...</p>
      </div>
    );
  }

  const checklistOverrides = {
    "complete-profile": Boolean(summary?.onboardingCompleted),
    "solve-first-problem": submissions.length > 0,
    "attempt-first-quiz": results.length > 0,
    "review-first-result": results.length > 0,
    "join-study-path": Boolean(summary?.preferredTrack),
  };

  return (
    <div className="app-container space-y-8">
      <div className="grid gap-8 xl:grid-cols-[1.15fr_0.85fr]">
        <header className="rounded-3xl border border-slate-800 bg-gradient-to-br from-slate-900 via-slate-900 to-slate-800 p-8 shadow-2xl">
          <p className="text-sm uppercase tracking-[0.25em] text-cyan-400">
            RankX Dashboard
          </p>
          <h1 className="mt-4 text-4xl font-bold">
            Welcome back{profile?.displayName ? `, ${profile.displayName}` : ""}
          </h1>
          <p className="mt-3 max-w-2xl text-slate-300">
            Track coding and quiz progress from one place and jump back into
            practice with a clear view of your recent work.
          </p>
          <div className="mt-6 flex flex-wrap gap-3 text-sm text-slate-300">
            {summary?.goal ? (
              <span className="rounded-full border border-cyan-400/20 bg-cyan-400/10 px-3 py-1">
                Goal: {summary.goal}
              </span>
            ) : null}
            {summary?.preferredTrack ? (
              <span className="rounded-full border border-emerald-400/20 bg-emerald-400/10 px-3 py-1">
                Track: {summary.preferredTrack}
              </span>
            ) : null}
            {summary?.skillLevel ? (
              <span className="rounded-full border border-amber-400/20 bg-amber-400/10 px-3 py-1">
                Level: {summary.skillLevel}
              </span>
            ) : null}
          </div>
          {error ? (
            <p className="mt-4 rounded-2xl border border-amber-500/40 bg-amber-500/10 px-4 py-3 text-amber-200">
              {error}
            </p>
          ) : null}
        </header>

        <aside className="space-y-4">
          <div className="surface-card">
            <div className="flex items-center gap-4">
              <div className="flex h-14 w-14 items-center justify-center rounded-full bg-teal-400/12 text-lg font-semibold text-teal-200">
                {(profile?.displayName || profile?.username || "User")
                  .slice(0, 2)
                  .toUpperCase()}
              </div>
              <div>
                <p className="text-lg font-semibold text-white">
                  {profile?.displayName || profile?.username || "RankX User"}
                </p>
                <p className="text-sm text-slate-400">
                  {shellContext?.profile?.email || "Workspace member"}
                </p>
              </div>
            </div>
            <div className="mt-6 grid gap-3">
              <button onClick={() => navigate("/account")} className="btn-secondary w-full justify-start">
                My account
              </button>
              <button onClick={() => navigate("/settings")} className="btn-secondary w-full justify-start">
                Settings
              </button>
              <button onClick={() => navigate("/billing")} className="btn-secondary w-full justify-start">
                Billing
              </button>
              <button onClick={() => navigate("/support")} className="btn-ghost w-full justify-start">
                Help & support
              </button>
            </div>
          </div>

          <div className="surface-card">
            <h2 className="section-title">Workspace status</h2>
            <div className="mt-5 space-y-3">
              {[
                "Onboarding preferences now personalize your first recommended action.",
                "Dashboard, profile, settings, billing, and support stay reachable from the main shell.",
                "Core quiz and coding flows remain intact while activation becomes more product-guided.",
              ].map((item) => (
                <div key={item} className="surface-card-soft">
                  <p className="text-sm leading-6 text-slate-300">{item}</p>
                </div>
              ))}
            </div>
          </div>
        </aside>
      </div>

      <RecommendedActionCard action={summary?.recommendedFirstAction} />

      <section className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
            <p className="text-sm text-slate-400">Role</p>
            <p className="mt-2 text-2xl font-semibold">
              {(profile?.role || "ROLE_USER").replace("ROLE_", "")}
            </p>
        </div>
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
            <p className="text-sm text-slate-400">Quiz Results</p>
            <p className="mt-2 text-2xl font-semibold">{results.length}</p>
        </div>
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
            <p className="text-sm text-slate-400">Recent Submissions</p>
            <p className="mt-2 text-2xl font-semibold">{submissions.length}</p>
        </div>
      </section>

      <DashboardChecklist
        items={summary?.checklist || []}
        overrides={checklistOverrides}
      />

      <section className="grid grid-cols-1 gap-6 md:grid-cols-2">
        {practiceCards.map((card) => (
          <motion.button
            key={card.title}
            whileHover={{ y: -4 }}
            whileTap={{ scale: 0.99 }}
            onClick={() => navigate(card.route)}
            className={`rounded-3xl bg-gradient-to-r ${card.gradient} p-8 text-left shadow-xl`}
          >
            <h2 className="text-3xl font-semibold">{card.title}</h2>
            <p className="mt-3 max-w-md text-white/85">{card.description}</p>
          </motion.button>
        ))}
      </section>

      <section className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold">Recent Quiz Results</h2>
            <button
              onClick={() => navigate("/quiz/history")}
              className="text-sm text-cyan-400 hover:text-cyan-300"
            >
              View all
            </button>
          </div>
          <div className="mt-5 space-y-3">
            {results.length === 0 ? (
              <p className="rounded-2xl border border-dashed border-slate-700 px-4 py-6 text-slate-400">
                No quiz history yet. Start a quiz to populate this dashboard.
              </p>
            ) : (
              results.slice(0, 5).map((result) => (
                <div
                  key={result.attemptId}
                  onClick={() => navigate(`/quiz/review/${result.attemptId}`)}
                  className="rounded-2xl border border-slate-800 bg-slate-950/60 px-4 py-4"
                >
                  <div className="flex items-center justify-between gap-4">
                    <div>
                      <p className="text-sm text-slate-400">Quiz</p>
                      <p className="font-medium">#{result.quizId}</p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-slate-400">Score</p>
                      <p className="font-semibold">
                        {result.score}/{result.totalQuestions}
                      </p>
                    </div>
                  </div>
                  <p className="mt-3 text-sm text-slate-300">
                    {result.percentage}% correct
                  </p>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold">Recent Coding Submissions</h2>
            <button
              onClick={() => navigate("/submissions")}
              className="text-sm text-emerald-400 hover:text-emerald-300"
            >
              View all
            </button>
          </div>
          <div className="mt-5 space-y-3">
            {submissions.length === 0 ? (
              <p className="rounded-2xl border border-dashed border-slate-700 px-4 py-6 text-slate-400">
                No submissions yet. Solve a coding problem to start building
                history.
              </p>
            ) : (
              submissions.map((submission) => (
                <div
                  key={submission.id}
                  className="rounded-2xl border border-slate-800 bg-slate-950/60 px-4 py-4"
                >
                  <div className="flex items-center justify-between gap-4">
                    <div>
                      <p className="text-sm text-slate-400">Problem</p>
                      <p className="font-medium">#{submission.problemId}</p>
                    </div>
                    <span className="rounded-full bg-slate-800 px-3 py-1 text-xs font-semibold text-slate-200">
                      {submission.status}
                    </span>
                  </div>
                  <div className="mt-3 flex flex-wrap gap-4 text-sm text-slate-300">
                    <span>{submission.languageKey}</span>
                    <span>
                      {submission.runtimeMs != null
                        ? `${submission.runtimeMs} ms`
                        : "Runtime pending"}
                    </span>
                    <span>
                      {submission.memoryKb != null
                        ? `${submission.memoryKb} KB`
                        : "Memory pending"}
                    </span>
                  </div>
                  <p className="mt-2 text-xs text-slate-500">
                    {formatTimestamp(submission.createdAt)}
                  </p>
                </div>
              ))
            )}
          </div>
        </div>
      </section>
      </div>
  );
}
