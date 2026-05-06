import api, { extractApiData } from "./api";

export const getMyProfile = async () => {
  const response = await api.get("/users/me");
  return extractApiData(response);
};

export const getMyPreferences = async () => {
  const response = await api.get("/users/me/preferences");
  return extractApiData(response);
};

export const updateMyPreferences = async (payload) => {
  const response = await api.put("/users/me/preferences", payload);
  return extractApiData(response);
};

export const getMyDashboardSummary = async () => {
  const response = await api.get("/users/me/dashboard-summary");
  return extractApiData(response);
};
