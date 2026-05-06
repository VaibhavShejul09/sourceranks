const BulkActionBar = ({ count, onPublish, onUnpublish, onDelete }) => {
  if (!count) return null;

  return (
    <div className="surface-card-soft flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <span className="text-sm text-slate-300">
        {count} quiz{count === 1 ? "" : "zes"} selected
      </span>

      <div className="flex flex-wrap gap-2">
        <button className="btn-success" onClick={onPublish}>
          Publish
        </button>
        <button className="btn-secondary" onClick={onUnpublish}>
          Unpublish
        </button>
        <button className="btn-danger" onClick={onDelete}>
          Delete
        </button>
      </div>
    </div>
  );
};

export default BulkActionBar;
