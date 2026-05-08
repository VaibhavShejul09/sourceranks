import api from "../services/api";

const inFlightKeys = new Map();

export async function trackProductEvent(event, options = {}) {
  const token = localStorage.getItem("token");
  if (!token || !event?.eventName || !event?.eventCategory) {
    return null;
  }

  const payload = {
    ...event,
    occurredAt: event.occurredAt || new Date().toISOString(),
  };
  const dedupeKey = options.dedupeKey;

  if (dedupeKey && inFlightKeys.has(dedupeKey)) {
    return inFlightKeys.get(dedupeKey);
  }

  const request = api
    .post("/users/events", payload)
    .catch((error) => {
      if (typeof window !== "undefined" && window.location.hostname === "localhost") {
        console.debug("Product event tracking skipped", payload.eventName, error?.message);
      }
      return null;
    })
    .finally(() => {
      if (dedupeKey) {
        inFlightKeys.delete(dedupeKey);
      }
    });

  if (dedupeKey) {
    inFlightKeys.set(dedupeKey, request);
  }

  return request;
}
