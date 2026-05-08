import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AnalyticsOverviewCard from "../components/AnalyticsOverviewCard";
import RecommendationCardsSection from "../components/RecommendationCardsSection";
import { getMyAnalytics } from "../services/userApi";
import { logoutUser } from "../services/authService";

export default function Analytics() {
  const navigate = useNavigate();
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const loadAnalytics = async () => {
      try {
        const data = await getMyAnalytics();
        setAnalytics(data);
        setError("");
      } catch (err) {
        if (err.response?.status === 401) {
          logoutUser();
          navigate("/login", { replace: true });
          return;
        }
        setError("We could not load your analytics right now.");
      } finally {
        setLoading(false);
      }
    };

    loadAnalytics();
  }, [navigate]);

  if (loading) {
    return <div className="min-h-screen bg-slate-950 px-6 py-8 text-slate-300">Loading analytics...</div>;
  }

  if (error) {
    return <div className="min-h-screen bg-slate-950 px-6 py-8 text-amber-200">{error}</div>;
  }

  const coding = analytics?.codingPerformance;
  const quiz = analytics?.quizPerformance;
  const activity = analytics?.activitySummary;

  return (
    <div className="space-y-8">
      <header className="rounded-3xl border border-slate-800 bg-slate-900 p-8">
        <p className="text-sm uppercase tracking-[0.25em] text-cyan-400">Analytics</p>
        <h1 className="mt-3 text-4xl font-bold text-white">Performance insights</h1>
        <p className="mt-3 max-w-2xl text-slate-300">
          See how your coding and quiz performance is trending, where you are strongest, and where to focus next.
        </p>
      </header>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <AnalyticsOverviewCard title="Coding acceptance" value={`${Number(coding?.acceptanceRate || 0).toFixed(0)}%`} subtitle={`${coding?.acceptedSubmissions || 0} accepted`} tone="emerald" />
        <AnalyticsOverviewCard title="Average quiz score" value={`${Number(quiz?.averagePercentage || 0).toFixed(0)}%`} subtitle={`${quiz?.totalAttempts || 0} quiz attempts`} tone="cyan" />
        <AnalyticsOverviewCard title="Activity streak" value={activity?.streakCount ?? 0} subtitle="Current streak count" tone="amber" />
        <AnalyticsOverviewCard title="Enrolled plans" value={activity?.enrolledStudyPlans ?? 0} subtitle="Guided paths in progress" tone="violet" />
      </section>

      <RecommendationCardsSection recommendations={analytics?.recommendations || []} />

      <section className="grid gap-6 lg:grid-cols-2">
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
          <h2 className="text-xl font-semibold text-white">Coding strengths and gaps</h2>
          <div className="mt-5 grid gap-4 md:grid-cols-2">
            <div>
              <p className="text-sm uppercase tracking-[0.18em] text-emerald-400">Strong topics</p>
              <div className="mt-3 space-y-3">
                {(coding?.strongTopics || []).length === 0 ? (
                  <p className="text-sm text-slate-400">No strong coding topics yet.</p>
                ) : (
                  coding.strongTopics.map((topic) => (
                    <div key={topic.topic} className="rounded-2xl border border-emerald-400/20 bg-emerald-400/5 px-4 py-3">
                      <p className="font-medium text-emerald-200">{topic.topic}</p>
                      <p className="mt-1 text-sm text-slate-300">{Number(topic.successRate).toFixed(0)}% success across {topic.attempts} attempts</p>
                    </div>
                  ))
                )}
              </div>
            </div>
            <div>
              <p className="text-sm uppercase tracking-[0.18em] text-rose-400">Weak topics</p>
              <div className="mt-3 space-y-3">
                {(coding?.weakTopics || []).length === 0 ? (
                  <p className="text-sm text-slate-400">No weak coding topics detected.</p>
                ) : (
                  coding.weakTopics.map((topic) => (
                    <div key={topic.topic} className="rounded-2xl border border-rose-400/20 bg-rose-400/5 px-4 py-3">
                      <p className="font-medium text-rose-200">{topic.topic}</p>
                      <p className="mt-1 text-sm text-slate-300">{Number(topic.successRate).toFixed(0)}% success across {topic.attempts} attempts</p>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        </div>

        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
          <h2 className="text-xl font-semibold text-white">Quiz strengths and gaps</h2>
          <div className="mt-5 grid gap-4 md:grid-cols-2">
            <div>
              <p className="text-sm uppercase tracking-[0.18em] text-emerald-400">Strong topics</p>
              <div className="mt-3 space-y-3">
                {(quiz?.strongTopics || []).length === 0 ? (
                  <p className="text-sm text-slate-400">No strong quiz topics yet.</p>
                ) : (
                  quiz.strongTopics.map((topic) => (
                    <div key={topic.topic} className="rounded-2xl border border-emerald-400/20 bg-emerald-400/5 px-4 py-3">
                      <p className="font-medium text-emerald-200">{topic.topic}</p>
                      <p className="mt-1 text-sm text-slate-300">{Number(topic.successRate).toFixed(0)}% success across {topic.attempts} attempts</p>
                    </div>
                  ))
                )}
              </div>
            </div>
            <div>
              <p className="text-sm uppercase tracking-[0.18em] text-rose-400">Weak topics</p>
              <div className="mt-3 space-y-3">
                {(quiz?.weakTopics || []).length === 0 ? (
                  <p className="text-sm text-slate-400">No weak quiz topics detected.</p>
                ) : (
                  quiz.weakTopics.map((topic) => (
                    <div key={topic.topic} className="rounded-2xl border border-rose-400/20 bg-rose-400/5 px-4 py-3">
                      <p className="font-medium text-rose-200">{topic.topic}</p>
                      <p className="mt-1 text-sm text-slate-300">{Number(topic.successRate).toFixed(0)}% success across {topic.attempts} attempts</p>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
