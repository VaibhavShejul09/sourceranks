const QuizFilters = ({ filters, onChange }) => {
  return (
    <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_220px]">
      <div>
        <label htmlFor="quiz-search" className="sr-only">
          Search quizzes
        </label>
        <input
          id="quiz-search"
          type="search"
          value={filters.search || ""}
          onChange={(e) => onChange({ ...filters, search: e.target.value })}
          placeholder="Search quizzes..."
          className="input-base"
        />
      </div>

      <div>
        <label htmlFor="quiz-status" className="sr-only">
          Filter by status
        </label>
        <select
          id="quiz-status"
          value={filters.status || ""}
          onChange={(e) => onChange({ ...filters, status: e.target.value })}
          className="input-base"
        >
          <option value="">All status</option>
          <option value="PUBLISHED">Published</option>
          <option value="DRAFT">Draft</option>
        </select>
      </div>
    </div>
  );
};

export default QuizFilters;
