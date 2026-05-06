import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaSave, FaSpinner } from "react-icons/fa";
import { getRoleFromToken } from "../../../utils/jwtUtils";
import { createQuiz } from "../services/quizApi";

const DIFFICULTY_OPTIONS = ["EASY", "MEDIUM", "HARD"];

const CreateQuiz = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    title: "",
    description: "",
    hours: 0,
    minutes: 10,
    category: "",
    subCategory: "",
    difficulty: "EASY",
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) return navigate("/login");

    try {
      const role = getRoleFromToken(token);
      if (!["ROLE_ADMIN", "ADMIN"].includes(role)) {
        navigate("/login");
      }
    } catch {
      navigate("/login");
    }
  }, [navigate]);

  const getTotalMinutes = () => Number(form.hours) * 60 + Number(form.minutes);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const newErrors = {};

    if (!form.title.trim()) newErrors.title = "Title is required";
    if (!form.description.trim()) newErrors.description = "Description is required";
    if (!form.category.trim()) newErrors.category = "Category is required";
    if (!form.subCategory.trim()) newErrors.subCategory = "Subcategory is required";
    if (getTotalMinutes() < 1) newErrors.duration = "Duration must be at least 1 minute";

    return newErrors;
  };

  const handleCreate = async () => {
    const validationErrors = validate();
    if (Object.keys(validationErrors).length) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);
    try {
      const quiz = await createQuiz({
        title: form.title,
        description: form.description,
        durationMinutes: getTotalMinutes(),
        category: form.category,
        subCategory: form.subCategory,
        difficulty: form.difficulty,
        status: "DRAFT",
      });

      navigate(`/quizzes/${quiz.id}/questions`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-shell">
      <div className="admin-container">
        <div className="mx-auto max-w-4xl space-y-6">
          <header className="page-header">
            <p className="eyebrow">Quiz Builder</p>
            <h1 className="mt-4 text-3xl font-semibold tracking-tight text-white sm:text-4xl">
              Create a new quiz
            </h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-300 sm:text-base">
              Define the quiz basics first, then continue directly into question
              management without changing the existing workflow.
            </p>
          </header>

          <section className="surface-card space-y-6">
            <div className="grid gap-6 md:grid-cols-2">
              <div className="md:col-span-2">
                <label htmlFor="quiz-title" className="field-label">
                  Quiz title
                </label>
                <input
                  id="quiz-title"
                  name="title"
                  value={form.title}
                  onChange={handleChange}
                  className="input-base"
                />
                {errors.title ? <p className="mt-2 text-sm text-rose-300">{errors.title}</p> : null}
              </div>

              <div className="md:col-span-2">
                <label htmlFor="quiz-description" className="field-label">
                  Description
                </label>
                <textarea
                  id="quiz-description"
                  name="description"
                  value={form.description}
                  onChange={handleChange}
                  rows={5}
                  className="input-base min-h-36 resize-y"
                />
                {errors.description ? (
                  <p className="mt-2 text-sm text-rose-300">{errors.description}</p>
                ) : null}
              </div>

              <div>
                <label htmlFor="quiz-hours" className="field-label">
                  Duration hours
                </label>
                <input
                  id="quiz-hours"
                  type="number"
                  min={0}
                  name="hours"
                  value={form.hours}
                  onChange={handleChange}
                  className="input-base"
                  placeholder="0"
                />
              </div>

              <div>
                <label htmlFor="quiz-minutes" className="field-label">
                  Duration minutes
                </label>
                <input
                  id="quiz-minutes"
                  type="number"
                  min={0}
                  max={59}
                  name="minutes"
                  value={form.minutes}
                  onChange={handleChange}
                  className="input-base"
                  placeholder="10"
                />
              </div>

              {errors.duration ? (
                <p className="md:col-span-2 -mt-2 text-sm text-rose-300">{errors.duration}</p>
              ) : null}

              <div>
                <label htmlFor="quiz-category" className="field-label">
                  Category
                </label>
                <input
                  id="quiz-category"
                  name="category"
                  value={form.category}
                  onChange={handleChange}
                  className="input-base"
                />
                {errors.category ? (
                  <p className="mt-2 text-sm text-rose-300">{errors.category}</p>
                ) : null}
              </div>

              <div>
                <label htmlFor="quiz-subcategory" className="field-label">
                  Subcategory
                </label>
                <input
                  id="quiz-subcategory"
                  name="subCategory"
                  value={form.subCategory}
                  onChange={handleChange}
                  className="input-base"
                />
                {errors.subCategory ? (
                  <p className="mt-2 text-sm text-rose-300">{errors.subCategory}</p>
                ) : null}
              </div>

              <div className="md:col-span-2">
                <label htmlFor="quiz-difficulty" className="field-label">
                  Difficulty
                </label>
                <select
                  id="quiz-difficulty"
                  name="difficulty"
                  value={form.difficulty}
                  onChange={handleChange}
                  className="input-base"
                >
                  {DIFFICULTY_OPTIONS.map((difficulty) => (
                    <option key={difficulty} value={difficulty}>
                      {difficulty}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="flex flex-col gap-3 border-t border-white/10 pt-6 sm:flex-row sm:justify-end">
              <button onClick={() => navigate("/quizzes")} className="btn-secondary">
                Cancel
              </button>
              <button onClick={handleCreate} disabled={loading} className="btn-primary">
                {loading ? <FaSpinner className="animate-spin" /> : <FaSave />}
                {loading ? "Creating..." : "Create and add questions"}
              </button>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default CreateQuiz;
