import api from "./api";

export const getAdminKpis = async () => (await api.get("/users/admin/analytics/kpis")).data;

export const getProblemAnalytics = async () =>
  (await api.get("/users/admin/analytics/problems")).data;

export const getQuizAnalytics = async () =>
  (await api.get("/users/admin/analytics/quizzes")).data;

export const getQuestionAnalytics = async () =>
  (await api.get("/users/admin/analytics/questions")).data;
