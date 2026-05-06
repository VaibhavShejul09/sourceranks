import AdminPlaceholderPage from "./AdminPlaceholderPage";

export default function AdminUsers() {
  return (
    <AdminPlaceholderPage
      eyebrow="Users Management"
      title="Manage users and access"
      description="This placeholder keeps user administration visible in the product until a dedicated backend-connected users module is available."
      metrics={[
        { label: "Active users", value: "1,184", detail: "Signed in during the last 30 days" },
        { label: "Admins", value: "12", detail: "Platform operators with elevated access" },
        { label: "New signups", value: "64", detail: "Accounts created this week" },
        { label: "Pending reviews", value: "9", detail: "Accounts requiring manual review" },
      ]}
      tableTitle="User directory"
      tableColumns={["User", "Plan", "Status", "Last active"]}
      tableRows={[
        ["Aarav S.", "Pro", "Active", "2 hours ago"],
        ["Maya R.", "Starter", "Trial", "Today"],
        ["Priya K.", "Enterprise", "Active", "Yesterday"],
      ]}
      actions={[
        { title: "Role assignment", description: "Promote or restrict access using the eventual user administration APIs." },
        { title: "Lifecycle control", description: "Suspend, reactivate, or review unusual account activity from one place." },
        { title: "Onboarding visibility", description: "Track new-user activation and plan conversion without leaving the dashboard." },
      ]}
    />
  );
}
