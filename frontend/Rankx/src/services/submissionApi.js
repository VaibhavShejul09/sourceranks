import api, { extractApiData } from "./api";

export const getMyRecentSubmissions = async (filters = {}) => {
  const response = await api.get("/submissions/me", { params: filters });
  return extractApiData(response);
};

export const getSubmissionDetail = async (submissionId) => {
  const response = await api.get(`/submissions/${submissionId}`);
  return extractApiData(response);
};

export const getProblemAttemptSummary = async (problemId) => {
  const response = await api.get(`/submissions/me/problem-summary/${problemId}`);
  return extractApiData(response);
};
