import AdminPlaceholderPage from "./AdminPlaceholderPage";

export default function AdminSupport() {
  return (
    <AdminPlaceholderPage
      eyebrow="Support & Tickets"
      title="Support operations"
      description="Keep support reachable from the main admin navigation so the product feels operationally complete instead of design-only."
      metrics={[
        { label: "Open tickets", value: "26", detail: "Across all channels" },
        { label: "Urgent", value: "4", detail: "Require same-day response" },
        { label: "Avg. first reply", value: "42 min", detail: "Current support performance" },
        { label: "Resolved today", value: "17", detail: "Closed by support team" },
      ]}
      tableTitle="Incoming queue"
      tableColumns={["Ticket", "Topic", "Priority", "Assigned"]}
      tableRows={[
        ["SUP-209", "Billing confusion", "High", "Aditi"],
        ["SUP-208", "Login issue", "Urgent", "Karan"],
        ["SUP-207", "Quiz feedback", "Medium", "Unassigned"],
      ]}
      actions={[
        { title: "Escalation routing", description: "Direct urgent issues to the right team quickly from a visible support area." },
        { title: "Status visibility", description: "Track backlogs and resolution performance without leaving the admin shell." },
        { title: "Cross-team handoff", description: "Connect product, billing, and support workflows when those systems are integrated." },
      ]}
    />
  );
}
