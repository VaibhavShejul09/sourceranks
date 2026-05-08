import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import StudyPlanCard from "../components/StudyPlanCard";
import { getStudyPlans } from "../services/userApi";
import { logoutUser } from "../services/authService";

export default function StudyPlans() {
  const navigate = useNavigate();
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadPlans = async () => {
      try {
        const data = await getStudyPlans();
        setPlans(Array.isArray(data) ? data : []);
      } catch (err) {
        if (err.response?.status === 401) {
          logoutUser();
          navigate("/login", { replace: true });
          return;
        }
        setError("We could not load study plans right now.");
      } finally {
        setLoading(false);
      }
    };

    loadPlans();
  }, [navigate]);

  return (
    <div className="app-container space-y-8">
      <header className="rounded-3xl border border-slate-800 bg-slate-900 p-8">
        <p className="text-sm uppercase tracking-[0.24em] text-cyan-400">Study Plans</p>
        <h1 className="mt-3 text-4xl font-bold text-white">Guided learning paths</h1>
        <p className="mt-3 max-w-2xl text-slate-400">
          Enroll in a structured plan and let RankX guide your next best practice action.
        </p>
      </header>

      {loading ? (
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-8 text-slate-300">
          Loading study plans...
        </div>
      ) : error ? (
        <div className="rounded-3xl border border-amber-500/40 bg-amber-500/10 p-8 text-amber-200">
          {error}
        </div>
      ) : plans.length === 0 ? (
        <div className="rounded-3xl border border-dashed border-slate-700 bg-slate-900 p-8 text-slate-400">
          No study plans are available yet.
        </div>
      ) : (
        <div className="grid gap-6 xl:grid-cols-2">
          {plans.map((plan) => (
            <StudyPlanCard key={plan.id} plan={plan} />
          ))}
        </div>
      )}
    </div>
  );
}
