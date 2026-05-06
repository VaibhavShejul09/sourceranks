import axios from "axios";

const api = axios.create({
  baseURL: "/api",
});

// 🔐 Attach JWT automatically
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const extractApiData = (response) => response.data;

export default api;
