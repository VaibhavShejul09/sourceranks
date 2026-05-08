export default function AnalyticsOverviewCard({ title, value, subtitle, tone = "cyan" }) {
  const toneClasses = {
    cyan: "text-cyan-300 border-cyan-400/20 bg-cyan-400/5",
    emerald: "text-emerald-300 border-emerald-400/20 bg-emerald-400/5",
    amber: "text-amber-300 border-amber-400/20 bg-amber-400/5",
    violet: "text-violet-300 border-violet-400/20 bg-violet-400/5",
  };

  return (
    <div className={`rounded-3xl border p-6 ${toneClasses[tone] || toneClasses.cyan}`}>
      <p className="text-sm uppercase tracking-[0.22em] text-slate-400">{title}</p>
      <p className="mt-3 text-3xl font-semibold">{value}</p>
      {subtitle ? <p className="mt-2 text-sm text-slate-300">{subtitle}</p> : null}
    </div>
  );
}
