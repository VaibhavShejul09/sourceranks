export default function AdminSettings() {
  return (
    <div className="admin-container space-y-6">
      <header className="page-header">
        <p className="eyebrow">Settings</p>
        <h1 className="mt-4 text-3xl font-semibold tracking-tight text-white sm:text-4xl">
          Platform settings
        </h1>
        <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-300 sm:text-base">
          A complete admin experience needs a clear place for operational preferences,
          policy controls, and workspace configuration.
        </p>
      </header>

      <div className="grid gap-6 xl:grid-cols-2">
        <section className="surface-card">
          <h2 className="section-title">General configuration</h2>
          <div className="mt-6 space-y-4">
            <div>
              <label htmlFor="platform-name" className="field-label">
                Platform name
              </label>
              <input id="platform-name" className="input-base" defaultValue="RankX" />
            </div>
            <div>
              <label htmlFor="support-email" className="field-label">
                Support email
              </label>
              <input
                id="support-email"
                className="input-base"
                defaultValue="support@rankx.example"
              />
            </div>
            <button className="btn-primary">Save settings</button>
          </div>
        </section>

        <section className="surface-card">
          <h2 className="section-title">Operational controls</h2>
          <div className="mt-6 space-y-4">
            {[
              "Require approval for admin role changes",
              "Notify support on failed enterprise payments",
              "Enable weekly executive summary exports",
            ].map((item, index) => (
              <label
                key={item}
                className="flex items-center justify-between gap-4 rounded-2xl border border-white/8 bg-white/[0.03] px-4 py-3"
              >
                <span className="text-sm text-slate-200">{item}</span>
                <input
                  type="checkbox"
                  defaultChecked={index < 2}
                  className="h-4 w-4 rounded border-white/15 bg-slate-900 text-sky-400 focus:ring-sky-400"
                />
              </label>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
