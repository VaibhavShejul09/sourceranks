import { useCallback, useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import ProgressSummaryWidget from "../components/ProgressSummaryWidget";
import StudyPlanProgressCard from "../components/StudyPlanProgressCard";
import { getMyProgressSummary, getMyStudyPlans, getStudyPlanProgress } from "../services/userApi";
import { logoutUser } from "../services/authService";
import { subscribeToProgressUpdates } from "../utils/progressSync";
import { trackProductEvent } from "../utils/eventTracker";

const progressStateStyles = {
  COMPLETED: "border-emerald-500/30 bg-emerald-500/10",
  NEXT: "border-cyan-400/30 bg-cyan-400/10",
  LOCKED: "border-slate-800 bg-slate-950/60",
};

export default function MyProgress() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const selectedPlanId = searchParams.get("plan");
  const [summary, setSummary] = useState(null);
  const [plans, setPlans] = useState([]);
  const [selectedPlanProgress, setSelectedPlanProgress] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadProgress = useCallback(async () => {
    try {
      const [summaryData, planData] = await Promise.all([
        getMyProgressSummary(),
        getMyStudyPlans(),
      ]);

      setSummary(summaryData);
      setPlans(Array.isArray(planData) ? planData : []);
      trackProductEvent(
        {
          eventName: "PROGRESS_DASHBOARD_VIEWED",
          eventCategory: "PROGRESS",
          source: "WEB",
          track: "BOTH",
          contentType: "STUDY_PLAN",
          contentId: planData?.[0]?.studyPlanId ? `study-plan-${planData[0].studyPlanId}` : undefined,
          contentTitle: planData?.[0]?.title || "My Progress",
          numericValue: summaryData?.streakCount || 0,
        },
        { dedupeKey: "progress-dashboard" }
      );

      const planIdToLoad = selectedPlanId || planData?.[0]?.studyPlanId;
      if (planIdToLoad) {
        const progress = await getStudyPlanProgress(planIdToLoad);
        setSelectedPlanProgress(progress);
      } else {
        setSelectedPlanProgress(null);
      }
      setError("");
    } catch (err) {
      if (err.response?.status === 401) {
        logoutUser();
        navigate("/login", { replace: true });
        return;
      }
      setError("We could not load your progress right now.");
    } finally {
      setLoading(false);
    }
  }, [navigate, selectedPlanId]);

  useEffect(() => {
    loadProgress();
    return subscribeToProgressUpdates(() => {
      loadProgress();
    });
  }, [loadProgress]);

  if (loading) {
    return (
      <div className="app-container">
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-8 text-slate-300">
          Loading progress...
        </div>
      </div>
    );
  }

  return (
    <div className="app-container space-y-8">
      <header className="rounded-3xl border border-slate-800 bg-slate-900 p-8">
        <p className="text-sm uppercase tracking-[0.24em] text-cyan-400">Progress Tracking</p>
        <h1 className="mt-3 text-4xl font-bold text-white">My study progress</h1>
        <p className="mt-3 max-w-2xl text-slate-400">
          Track enrolled study plans, see what comes next, and keep momentum visible.
        </p>
      </header>

      {error ? (
        <div className="rounded-3xl border border-amber-500/40 bg-amber-500/10 p-8 text-amber-200">
          {error}
        </div>
      ) : null}

      <ProgressSummaryWidget summary={summary} />

      {plans.length === 0 ? (
        <div className="rounded-3xl border border-dashed border-slate-700 bg-slate-900 p-8 text-slate-400">
          You have not enrolled in a study plan yet. Browse plans to start guided progress.
        </div>
      ) : (
        <div className="grid gap-6 xl:grid-cols-[0.9fr_1.1fr]">
          <div className="space-y-4">
            {plans.map((plan) => (
              <StudyPlanProgressCard key={plan.studyPlanId} plan={plan} />
            ))}
          </div>

          <section className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
            {selectedPlanProgress ? (
              <>
                <div className="flex items-center justify-between gap-4">
                  <div>
                    <p className="text-sm uppercase tracking-[0.2em] text-slate-500">Current plan detail</p>
                    <h2 className="mt-2 text-2xl font-semibold text-white">{selectedPlanProgress.title}</h2>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-slate-400">Completion</p>
                    <p className="mt-2 text-2xl font-semibold text-cyan-300">
                      {Number(selectedPlanProgress.completionPercentage || 0).toFixed(0)}%
                    </p>
                  </div>
                </div>

                <p className="mt-4 text-sm text-slate-400">
                  Completed {selectedPlanProgress.completedItems} of {selectedPlanProgress.totalItems} items
                </p>
                <p className="mt-2 text-sm text-slate-300">
                  Next item: {selectedPlanProgress.nextItemTitle || "Plan completed"}
                </p>

                <div className="mt-6 space-y-3">
                  {selectedPlanProgress.items.map((item) => (
                    <div
                      key={item.itemId}
                      className={`rounded-2xl border px-4 py-4 ${
                        progressStateStyles[item.progressState] || progressStateStyles.LOCKED
                      }`}
                    >
                      <div className="flex items-center justify-between gap-4">
                        <div>
                          <p className="text-sm text-slate-400">Step {item.sequenceNumber}</p>
                          <p className="mt-1 font-medium text-white">{item.title}</p>
                        </div>
                        <span className="rounded-full bg-slate-800 px-3 py-1 text-xs font-semibold text-slate-200">
                          {item.progressState === "COMPLETED"
                            ? "Completed"
                            : item.progressState === "NEXT"
                              ? "Next"
                              : "Locked"}
                        </span>
                      </div>
                      <p className="mt-2 text-sm text-slate-400">Reference: {item.referenceKey}</p>
                    </div>
                  ))}
                </div>
              </>
            ) : (
              <p className="text-slate-400">Select a plan to inspect its progress.</p>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
