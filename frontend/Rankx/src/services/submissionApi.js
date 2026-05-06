import api, { extractApiData } from "./api";

export const getMyRecentSubmissions = async () => {
  const response = await api.get("/submissions/me");
  return extractApiData(response);
};

export const getSubmissionDetail = async (submissionId) => {
  const response = await api.get(`/submissions/${submissionId}`);
  return extractApiData(response);
};
