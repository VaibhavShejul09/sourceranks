import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import OnboardingCard from "../components/OnboardingCard";
import { getMyPreferences, updateMyPreferences } from "../services/userApi";
import { trackProductEvent } from "../utils/eventTracker";

const GOALS = ["Interview Prep", "College/Exam Practice", "Skill Improvement"];
const TRACKS = ["Coding", "Quiz", "Both"];
const LEVELS = ["Beginner", "Intermediate", "Advanced"];

const buttonClass = (selected) =>
  `rounded-2xl border px-4 py-3 text-left text-sm font-medium transition ${
    selected
      ? "border-cyan-400/50 bg-cyan-400/10 text-white"
      : "border-slate-800 bg-slate-950/70 text-slate-300 hover:border-slate-700 hover:bg-slate-900"
  }`;

export default function Onboarding() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    goal: "",
    preferredTrack: "",
    skillLevel: "",
  });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login", { replace: true });
      return;
    }

    const loadPreferences = async () => {
      try {
        const data = await getMyPreferences();

        if (data?.onboardingCompleted) {
          navigate("/home", { replace: true });
          return;
        }

        setForm({
          goal: data?.goal || "",
          preferredTrack: data?.preferredTrack || "",
          skillLevel: data?.skillLevel || "",
        });
      } catch (err) {
        if (err.response?.status === 401) {
          localStorage.removeItem("token");
          localStorage.removeItem("role");
          navigate("/login", { replace: true });
          return;
        }

        setError("We could not load onboarding right now.");
      } finally {
        setLoading(false);
      }
    };

    loadPreferences();
  }, [navigate]);

  const isComplete = useMemo(
    () => Boolean(form.goal && form.preferredTrack && form.skillLevel),
    [form]
  );

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!isComplete) {
      setError("Please complete all onboarding steps before continuing.");
      return;
    }

    try {
      setSubmitting(true);
      setError("");
      await updateMyPreferences(form);
      trackProductEvent({
        eventName: "ONBOARDING_COMPLETED",
        eventCategory: "ONBOARDING",
        source: "WEB",
        track: form.preferredTrack,
        topic: form.goal,
        outcome: form.skillLevel,
      });
      navigate("/home", { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || "We could not save your preferences.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 px-6 py-8 text-slate-100">
        <div className="mx-auto max-w-4xl rounded-3xl border border-slate-800 bg-slate-900 p-8 text-slate-300">
          Loading onboarding...
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 px-6 py-8 text-slate-100 md:px-10">
      <div className="mx-auto max-w-5xl space-y-6">
        <header className="rounded-3xl border border-slate-800 bg-gradient-to-br from-slate-900 via-slate-900 to-slate-800 p-8 shadow-2xl">
          <p className="text-sm uppercase tracking-[0.25em] text-cyan-400">RankX Activation</p>
          <h1 className="mt-4 text-4xl font-bold">Tell us how you want to grow</h1>
          <p className="mt-3 max-w-2xl text-slate-300">
            Complete onboarding once so RankX can shape your dashboard, next best action,
            and practice direction from day one.
          </p>
        </header>

        <form onSubmit={handleSubmit} className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
          <div className="space-y-6">
            <OnboardingCard
              title="What is your primary goal?"
              description="This helps RankX bias the dashboard and first action toward the type of practice you care about most."
            >
              <div className="grid gap-3 sm:grid-cols-2">
                {GOALS.map((goal) => (
                  <button
                    key={goal}
                    type="button"
                    className={buttonClass(form.goal === goal)}
                    onClick={() => setForm((current) => ({ ...current, goal }))}
                  >
                    {goal}
                  </button>
                ))}
              </div>
            </OnboardingCard>

            <OnboardingCard
              title="Which track do you want to focus on?"
              description="Choose a single lane or keep both coding and quiz practice active."
            >
              <div className="grid gap-3 sm:grid-cols-3">
                {TRACKS.map((track) => (
                  <button
                    key={track}
                    type="button"
                    className={buttonClass(form.preferredTrack === track)}
                    onClick={() => setForm((current) => ({ ...current, preferredTrack: track }))}
                  >
                    {track}
                  </button>
                ))}
              </div>
            </OnboardingCard>

            <OnboardingCard
              title="What is your current level?"
              description="We use this to set expectations and steer your first action toward the right complexity."
            >
              <div className="grid gap-3 sm:grid-cols-3">
                {LEVELS.map((skillLevel) => (
                  <button
                    key={skillLevel}
                    type="button"
                    className={buttonClass(form.skillLevel === skillLevel)}
                    onClick={() => setForm((current) => ({ ...current, skillLevel }))}
                  >
                    {skillLevel}
                  </button>
                ))}
              </div>
            </OnboardingCard>
          </div>

          <div className="space-y-6">
            <OnboardingCard
              title="Your activation summary"
              description="This preview shows what RankX will use to personalize your dashboard after you continue."
              footer={
                <button
                  type="submit"
                  disabled={!isComplete || submitting}
                  className="w-full rounded-2xl bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {submitting ? "Saving preferences..." : "Continue to personalized dashboard"}
                </button>
              }
            >
              <div className="space-y-4">
                <div className="rounded-2xl border border-slate-800 bg-slate-950/70 px-4 py-4">
                  <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Goal</p>
                  <p className="mt-2 text-sm text-white">{form.goal || "Choose your primary goal"}</p>
                </div>
                <div className="rounded-2xl border border-slate-800 bg-slate-950/70 px-4 py-4">
                  <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Preferred track</p>
                  <p className="mt-2 text-sm text-white">{form.preferredTrack || "Choose your learning track"}</p>
                </div>
                <div className="rounded-2xl border border-slate-800 bg-slate-950/70 px-4 py-4">
                  <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Current level</p>
                  <p className="mt-2 text-sm text-white">{form.skillLevel || "Choose your current level"}</p>
                </div>
              </div>
            </OnboardingCard>

            {error ? (
              <div className="rounded-3xl border border-amber-500/40 bg-amber-500/10 p-5 text-sm text-amber-200">
                {error}
              </div>
            ) : null}
          </div>
        </form>
      </div>
    </div>
  );
}
