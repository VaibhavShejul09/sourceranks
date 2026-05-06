import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../services/api";
import ProblemWorkspace from "../components/ProblemWorkspace";

export default function ProblemDetail() {
  const { id } = useParams();
  const [problem, setProblem] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    setError("");

    api
      .get(`/problems/${id}`)
      .then((res) => setProblem(res.data))
      .catch((err) => {
        console.error(err);
        setError("We couldn't load this problem right now.");
      });
  }, [id]);

  if (error) {
    return (
      <div className="app-shell flex items-center justify-center">
        <div
          role="alert"
          className="surface-card w-full max-w-xl rounded-[28px] text-center"
        >
          <h1 className="text-2xl font-semibold text-white">Problem unavailable</h1>
          <p className="mt-3 text-sm text-slate-400">{error}</p>
        </div>
      </div>
    );
  }

  if (!problem) {
    return (
      <div className="app-shell flex items-center justify-center">
        <div className="surface-card w-full max-w-xl rounded-[28px] text-center">
          <div className="mx-auto h-12 w-12 animate-pulse rounded-full bg-teal-400/15" />
          <p className="mt-4 text-sm text-slate-300">Loading problem workspace...</p>
        </div>
      </div>
    );
  }

  return <ProblemWorkspace problem={problem} />;
}
