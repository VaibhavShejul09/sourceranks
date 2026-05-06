import api, { extractApiData } from "./api";

export const getMyProfile = async () => {
  const response = await api.get("/users/me");
  return extractApiData(response);
};
