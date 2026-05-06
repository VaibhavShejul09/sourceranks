import AdminPlaceholderPage from "./AdminPlaceholderPage";

export default function AdminPlans() {
  return (
    <AdminPlaceholderPage
      eyebrow="Plans & Subscriptions"
      title="Manage plans and packaging"
      description="A clear subscription management destination helps the admin app feel complete even before the full monetization console is implemented."
      metrics={[
        { label: "Starter plans", value: "412", detail: "Current users on entry tier" },
        { label: "Professional plans", value: "536", detail: "Paid active subscriptions" },
        { label: "Enterprise seats", value: "236", detail: "Seats across enterprise accounts" },
        { label: "Churn risk", value: "18", detail: "Accounts flagged for outreach" },
      ]}
      tableTitle="Plan catalog"
      tableColumns={["Plan", "Price", "Subscribers", "Status"]}
      tableRows={[
        ["Starter", "$0", "412", "Active"],
        ["Professional", "$29/mo", "536", "Active"],
        ["Enterprise", "Custom", "17 orgs", "Active"],
      ]}
      actions={[
        { title: "Packaging controls", description: "Adjust pricing tiers, trial messaging, and entitlements when billing APIs are connected." },
        { title: "Conversion analysis", description: "Pair this area with reports to understand upgrade behavior and retention." },
        { title: "Support alignment", description: "Keep plan-related support issues close to subscription configuration." },
      ]}
    />
  );
}
