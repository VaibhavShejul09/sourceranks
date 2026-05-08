import { useNavigate } from "react-router-dom";

export default function RecommendationCardsSection({ recommendations = [] }) {
  const navigate = useNavigate();

  if (!recommendations.length) {
    return null;
  }

  return (
    <section className="space-y-4">
      <div>
        <p className="text-sm uppercase tracking-[0.25em] text-amber-400">
          Recommendations
        </p>
        <h2 className="mt-2 text-2xl font-semibold text-white">
          Suggested next actions
        </h2>
      </div>
      <div className="grid gap-4 lg:grid-cols-3">
        {recommendations.map((recommendation) => (
          <button
            key={`${recommendation.title}-${recommendation.route}`}
            type="button"
            onClick={() => navigate(recommendation.route)}
            className="rounded-3xl border border-slate-800 bg-slate-900 p-6 text-left transition hover:border-slate-700 hover:bg-slate-900/90"
          >
            <div className="flex items-center justify-between gap-4">
              <p className="text-lg font-semibold text-white">{recommendation.title}</p>
              <span className="rounded-full bg-amber-400/10 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-amber-300">
                {recommendation.priority}
              </span>
            </div>
            <p className="mt-3 text-sm leading-6 text-slate-300">
              {recommendation.description}
            </p>
            <p className="mt-4 text-xs uppercase tracking-[0.16em] text-slate-500">
              {recommendation.reason}
            </p>
          </button>
        ))}
      </div>
    </section>
  );
}
