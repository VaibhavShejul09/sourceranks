const EVENT_NAME = "rankx:progress-updated";

export const emitProgressUpdated = (detail = {}) => {
  if (typeof window === "undefined") {
    return;
  }

  window.dispatchEvent(new CustomEvent(EVENT_NAME, { detail }));
};

export const subscribeToProgressUpdates = (callback) => {
  if (typeof window === "undefined") {
    return () => {};
  }

  const handler = (event) => callback(event.detail);
  window.addEventListener(EVENT_NAME, handler);
  return () => window.removeEventListener(EVENT_NAME, handler);
};
