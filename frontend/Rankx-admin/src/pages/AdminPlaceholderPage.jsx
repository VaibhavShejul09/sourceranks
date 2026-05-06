export default function AdminPlaceholderPage({
  eyebrow,
  title,
  description,
  metrics = [],
  tableTitle,
  tableColumns = [],
  tableRows = [],
  actions = [],
}) {
  return (
    <div className="admin-container space-y-6">
      <header className="page-header">
        <p className="eyebrow">{eyebrow}</p>
        <h1 className="mt-4 text-3xl font-semibold tracking-tight text-white sm:text-4xl">
          {title}
        </h1>
        <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-300 sm:text-base">
          {description}
        </p>
      </header>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {metrics.map((metric) => (
          <div key={metric.label} className="stat-card">
            <p className="text-sm text-slate-400">{metric.label}</p>
            <p className="mt-3 text-3xl font-semibold text-white">{metric.value}</p>
            <p className="mt-2 text-sm text-slate-500">{metric.detail}</p>
          </div>
        ))}
      </section>

      <section className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        <div className="surface-card">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <h2 className="section-title">{tableTitle}</h2>
              <p className="section-copy mt-1 text-sm">
                Placeholder management UI so the area feels intentional rather than broken.
              </p>
            </div>

            <div className="flex flex-wrap gap-2">
              <input className="input-base max-w-xs" placeholder="Filter records..." />
              <button className="btn-primary">Create new</button>
            </div>
          </div>

          <div className="mt-6 table-shell overflow-x-auto">
            <table className="min-w-full">
              <thead className="table-head">
                <tr className="text-left text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
                  {tableColumns.map((column) => (
                    <th key={column} className="px-5 py-4">
                      {column}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {tableRows.map((row, rowIndex) => (
                  <tr key={rowIndex} className="border-t border-white/5 text-sm text-slate-200">
                    {row.map((cell, cellIndex) => (
                      <td key={cellIndex} className="px-5 py-4">
                        {cell}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="surface-card">
          <h2 className="section-title">Recommended actions</h2>
          <div className="mt-6 space-y-4">
            {actions.map((action) => (
              <div key={action.title} className="surface-card-soft">
                <p className="text-sm font-semibold text-white">{action.title}</p>
                <p className="mt-2 text-sm leading-6 text-slate-400">{action.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
