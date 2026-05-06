import { useNavigate } from "react-router-dom";

export default function RecommendedActionCard({ action }) {
  const navigate = useNavigate();

  if (!action) {
    return (
      <section className="surface-card">
        <p className="text-sm uppercase tracking-[0.24em] text-amber-300/80">
          Recommended First Action
        </p>
        <p className="mt-4 text-sm text-slate-400">
          We are preparing your next best action. Complete onboarding to personalize this space.
        </p>
      </section>
    );
  }

  return (
    <section className="rounded-3xl border border-cyan-500/25 bg-gradient-to-br from-cyan-500/12 via-slate-900 to-slate-900 p-6 shadow-2xl">
      <p className="text-sm uppercase tracking-[0.24em] text-cyan-300">
        Recommended First Action
      </p>
      <h2 className="mt-4 text-2xl font-semibold text-white">{action.title}</h2>
      <p className="mt-3 max-w-xl text-sm leading-6 text-slate-300">{action.description}</p>
      <button
        type="button"
        onClick={() => navigate(action.route || "/home")}
        className="mt-6 rounded-2xl bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400"
      >
        Continue
      </button>
    </section>
  );
}
