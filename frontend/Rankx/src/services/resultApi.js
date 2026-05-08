import axiosInstance from "./api";

export const evaluateAttempt = (attemptId) =>
  axiosInstance.post(`/results/evaluate/${attemptId}`);

export const getMyResults = (params = {}) =>
  axiosInstance.get("/results/me", { params });

export const getResult = (attemptId) =>
  axiosInstance.get(`/results/${attemptId}`);

export const getResultReview = (attemptId) =>
  axiosInstance.get(`/results/${attemptId}/review`);
