export default function ProgressSummaryWidget({ summary }) {
  return (
    <section className="grid gap-4 md:grid-cols-3">
      <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
        <p className="text-sm text-slate-400">Current Plan</p>
        <p className="mt-2 text-2xl font-semibold text-white">
          {summary?.currentPlan?.title || "No active plan"}
        </p>
      </div>
      <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
        <p className="text-sm text-slate-400">Next Item</p>
        <p className="mt-2 text-2xl font-semibold text-white">
          {summary?.currentPlan?.nextItemTitle || "Choose a plan"}
        </p>
      </div>
      <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
        <p className="text-sm text-slate-400">Streak Count</p>
        <p className="mt-2 text-2xl font-semibold text-white">
          {summary?.streakCount ?? 0}
        </p>
      </div>
    </section>
  );
}
