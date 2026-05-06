import AdminPlaceholderPage from "./AdminPlaceholderPage";

export default function AdminPayments() {
  return (
    <AdminPlaceholderPage
      eyebrow="Payments & Billing"
      title="Track payments and invoices"
      description="This billing console placeholder prevents the admin platform from feeling incomplete while keeping the route structure ready for future payment integrations."
      metrics={[
        { label: "MRR", value: "$18.4k", detail: "Current monthly recurring revenue" },
        { label: "Failed payments", value: "7", detail: "Need follow-up this week" },
        { label: "Refund requests", value: "3", detail: "Open review queue" },
        { label: "Invoices issued", value: "289", detail: "Generated this month" },
      ]}
      tableTitle="Recent billing activity"
      tableColumns={["Invoice", "Customer", "Amount", "Status"]}
      tableRows={[
        ["INV-4021", "NorthStar Labs", "$499", "Paid"],
        ["INV-4016", "Atlas Learning", "$29", "Pending"],
        ["INV-4010", "Asteria Tech", "$199", "Failed"],
      ]}
      actions={[
        { title: "Collections workflow", description: "Create retries and outreach processes for failed or overdue payments." },
        { title: "Refund handling", description: "Keep manual review actions visible once payment operations are implemented." },
        { title: "Finance reporting", description: "Connect this area to revenue exports and accounting workflows later." },
      ]}
    />
  );
}
