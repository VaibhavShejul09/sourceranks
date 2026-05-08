import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { getResult } from "../../services/resultApi";
import { emitProgressUpdated } from "../../utils/progressSync";

const QuizResult = () => {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const attemptId = params.get("attemptId");

  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!attemptId) {
      setError("Invalid attempt");
      setLoading(false);
      return;
    }

    getResult(attemptId)
      .then((res) => {
        setResult(res.data);
        emitProgressUpdated({
          source: "quiz-result",
          attemptId,
          quizId: res.data?.quizId,
        });
        setLoading(false);
      })
      .catch(() => {
        setError("Failed to load result");
        setLoading(false);
      });
  }, [attemptId]);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#020617] flex items-center justify-center">
        <p className="text-white text-xl animate-pulse">
          Calculating your result...
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-[#020617] flex items-center justify-center">
        <p className="text-red-400 text-xl">{error}</p>
      </div>
    );
  }

  const isPassed = result.percentage >= 40;

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#020617] to-[#020617] text-white flex justify-center items-center px-4">
      <div className="w-full max-w-md bg-[#0f172a] rounded-3xl p-10 shadow-2xl text-center">
        <div className="mb-4 text-6xl">{isPassed ? "Success" : "Retry"}</div>

        <h1 className="text-3xl font-bold mb-2">
          {isPassed ? "Congratulations!" : "Better Luck Next Time"}
        </h1>

        <p className="mb-8 text-slate-400">Here's how you performed in this quiz</p>

        <div className="bg-[#020617] rounded-2xl py-6 mb-6">
          <p className="text-slate-400 text-sm mb-1">Your Score</p>
          <p className="text-5xl font-extrabold text-indigo-400">
            {result.score}
            <span className="text-2xl text-slate-400">
              {" "} / {result.totalQuestions}
            </span>
          </p>
        </div>

        <div className="mb-8">
          <p className="text-lg text-slate-300">Percentage</p>
          <p
            className={`text-2xl font-semibold mt-1 ${
              isPassed ? "text-green-400" : "text-red-400"
            }`}
          >
            {result.percentage.toFixed(2)}%
          </p>
        </div>

        <div className="flex flex-col gap-4">
          <button
            onClick={() => navigate("/home")}
            className="w-full py-3 rounded-xl bg-indigo-600 hover:bg-indigo-700 transition text-lg font-medium"
          >
            Go to Dashboard
          </button>

          <button
            onClick={() => navigate("/quiz")}
            className="w-full py-3 rounded-xl border border-slate-600 hover:bg-slate-800 transition text-lg"
          >
            Attempt Another Quiz
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuizResult;
