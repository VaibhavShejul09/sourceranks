export default function OnboardingCard({ title, description, children, footer }) {
  return (
    <section className="rounded-3xl border border-slate-800 bg-slate-900 p-6 shadow-2xl">
      <div className="mb-6">
        <p className="text-sm uppercase tracking-[0.24em] text-cyan-400">
          Activation
        </p>
        <h2 className="mt-3 text-2xl font-semibold text-white">{title}</h2>
        <p className="mt-2 text-sm leading-6 text-slate-400">{description}</p>
      </div>

      <div className="space-y-5">{children}</div>

      {footer ? <div className="mt-6 border-t border-slate-800 pt-5">{footer}</div> : null}
    </section>
  );
}
