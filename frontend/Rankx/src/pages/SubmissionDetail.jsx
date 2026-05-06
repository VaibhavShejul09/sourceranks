import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getSubmissionDetail } from "../services/submissionApi";

const formatTimestamp = (value) => {
  if (!value) {
    return "Recently";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "Recently";
  }

  return date.toLocaleString();
};

export default function SubmissionDetail() {
  const navigate = useNavigate();
  const { submissionId } = useParams();
  const [submission, setSubmission] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const loadSubmission = async () => {
      try {
        const data = await getSubmissionDetail(submissionId);
        setSubmission(data);
      } catch (err) {
        if (err.response?.status === 401) {
          localStorage.removeItem("token");
          navigate("/login");
          return;
        }

        setError("We could not load this submission.");
      } finally {
        setLoading(false);
      }
    };

    loadSubmission();
  }, [navigate, submissionId]);

  return (
    <div className="min-h-screen bg-slate-950 px-6 py-8 text-slate-100 md:px-10">
      <div className="mx-auto max-w-5xl space-y-6">
        <div className="flex items-center justify-between gap-4">
          <div>
            <p className="text-sm uppercase tracking-[0.25em] text-emerald-400">
              Submission Detail
            </p>
            <h1 className="mt-3 text-4xl font-bold">
              Submission #{submissionId}
            </h1>
          </div>
          <button
            onClick={() => navigate("/submissions")}
            className="rounded-2xl border border-slate-700 px-4 py-2 text-sm text-slate-200 hover:bg-slate-800"
          >
            Back to History
          </button>
        </div>

        {loading ? (
          <div className="rounded-3xl border border-slate-800 bg-slate-900 p-8 text-slate-300">
            Loading submission details...
          </div>
        ) : error ? (
          <div className="rounded-3xl border border-amber-500/40 bg-amber-500/10 p-8 text-amber-200">
            {error}
          </div>
        ) : (
          <>
            <section className="grid grid-cols-1 gap-4 md:grid-cols-4">
              <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
                <p className="text-sm text-slate-400">Problem</p>
                <p className="mt-2 text-2xl font-semibold">#{submission.problemId}</p>
              </div>
              <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
                <p className="text-sm text-slate-400">Language</p>
                <p className="mt-2 text-2xl font-semibold">{submission.languageKey}</p>
              </div>
              <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
                <p className="text-sm text-slate-400">Runtime</p>
                <p className="mt-2 text-2xl font-semibold">
                  {submission.runtimeMs != null ? `${submission.runtimeMs} ms` : "Pending"}
                </p>
              </div>
              <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
                <p className="text-sm text-slate-400">Memory</p>
                <p className="mt-2 text-2xl font-semibold">
                  {submission.memoryKb != null ? `${submission.memoryKb} KB` : "Pending"}
                </p>
              </div>
            </section>

            <section className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
              <div className="flex items-center justify-between gap-4">
                <div>
                  <p className="text-sm text-slate-400">Verdict</p>
                  <p className="mt-2 text-2xl font-semibold">{submission.status}</p>
                </div>
                <p className="text-sm text-slate-400">
                  Submitted {formatTimestamp(submission.createdAt)}
                </p>
              </div>
            </section>

            <section className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
              <p className="text-sm uppercase tracking-[0.2em] text-slate-400">
                Source Code
              </p>
              <pre className="mt-4 overflow-x-auto rounded-2xl bg-slate-950 p-5 text-sm text-slate-200">
                <code>{submission.sourceCode}</code>
              </pre>
            </section>
          </>
        )}
      </div>
    </div>
  );
}
