import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { getMyProfile } from "../services/userApi";
import { getMyResults } from "../services/resultApi";
import { getMyRecentSubmissions } from "../services/submissionApi";

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
  const [profile, setProfile] = useState(null);
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
        const [profileData, resultsResponse, submissionData] = await Promise.all([
          getMyProfile(),
          getMyResults(),
          getMyRecentSubmissions(),
        ]);

        setProfile(profileData);
        setResults(Array.isArray(resultsResponse.data) ? resultsResponse.data : []);
        setSubmissions(Array.isArray(submissionData) ? submissionData : []);
      } catch (err) {
        if (err.response?.status === 401) {
          localStorage.removeItem("token");
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

  return (
    <div className="min-h-screen bg-slate-950 px-6 py-8 text-slate-100 md:px-10">
      <div className="mx-auto max-w-6xl space-y-8">
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
          {error ? (
            <p className="mt-4 rounded-2xl border border-amber-500/40 bg-amber-500/10 px-4 py-3 text-amber-200">
              {error}
            </p>
          ) : null}
        </header>

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
    </div>
  );
}
