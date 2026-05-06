import AdminPlaceholderPage from "./AdminPlaceholderPage";

export default function AdminReports() {
  return (
    <AdminPlaceholderPage
      eyebrow="Reports & Analytics"
      title="Analytics and reporting"
      description="A real SaaS admin product needs a visible analytics destination; this page keeps that area accessible and structured until more data sources are added."
      metrics={[
        { label: "Weekly active users", value: "862", detail: "Up 12% week over week" },
        { label: "Quiz completions", value: "1,942", detail: "Completed this month" },
        { label: "Avg. session length", value: "28 min", detail: "Across the user platform" },
        { label: "Support CSAT", value: "94%", detail: "Across recent ticket closures" },
      ]}
      tableTitle="Top operating signals"
      tableColumns={["Report", "Owner", "Updated", "Status"]}
      tableRows={[
        ["Usage trends", "Growth", "Today", "Live"],
        ["Revenue summary", "Finance", "Yesterday", "Live"],
        ["Content health", "Operations", "Today", "Live"],
      ]}
      actions={[
        { title: "Executive reporting", description: "Expose high-signal metrics for growth, revenue, and product adoption." },
        { title: "Operational alerts", description: "Surface unusual usage, billing anomalies, or content issues before they escalate." },
        { title: "Export pipeline", description: "Keep room for downloadable CSV/PDF exports when reporting services are ready." },
      ]}
    />
  );
}
