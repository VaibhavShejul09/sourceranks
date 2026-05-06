import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Editor from "@monaco-editor/react";
import api from "../services/api";
import { AnimatePresence, motion } from "framer-motion";

const PRIMARY_TABS = ["question", "solution", "submissions", "notes"];
const DRAWER_TABS = ["testcase", "output", "custom"];

const difficultyBadgeClass = {
  EASY: "status-easy",
  MEDIUM: "status-medium",
  HARD: "status-hard",
};

export default function ProblemWorkspace({ problem }) {
  const navigate = useNavigate();
  const workspaceRef = useRef(null);
  const editorRef = useRef(null);

  const [activeTab, setActiveTab] = useState("question");
  const [languageKey, setLanguageKey] = useState("");
  const [editorLanguage, setEditorLanguage] = useState("python");
  const [code, setCode] = useState("");
  const [starterCodeMap, setStarterCodeMap] = useState({});
  const [output, setOutput] = useState("");
  const [isDark, setIsDark] = useState(true);
  const [showConsole, setShowConsole] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [testTab, setTestTab] = useState("testcase");
  const [sampleTestCases, setSampleTestCases] = useState([]);
  const [activeTestCase, setActiveTestCase] = useState(0);
  const [customInput, setCustomInput] = useState("");
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [problems, setProblems] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    api.get("/problems").then((res) => setProblems(res.data.content || []));
  }, []);

  const goPrev = () => {
    if (problem.id > 1) {
      navigate(`/problems/${problem.id - 1}`);
    }
  };

  const goNext = () => {
    navigate(`/problems/${problem.id + 1}`);
  };

  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      workspaceRef.current?.requestFullscreen();
      setIsFullscreen(true);
    } else {
      document.exitFullscreen();
      setIsFullscreen(false);
    }
  };

  useEffect(() => {
    const handler = () => {
      if (!document.fullscreenElement) {
        setIsFullscreen(false);
      }
    };

    document.addEventListener("fullscreenchange", handler);
    return () => document.removeEventListener("fullscreenchange", handler);
  }, []);

  useEffect(() => {
    if (!problem?.id) return;

    api
      .get(`/problems/${problem.id}/testcases/samples`)
      .then((res) => {
        setSampleTestCases(res.data || []);
        setActiveTestCase(0);
      })
      .catch((err) => console.error("Failed to load testcases", err));
  }, [problem?.id]);

  useEffect(() => {
    if (problem?.languages?.length > 0) {
      const map = {};
      problem.templates.forEach((template) => {
        map[template.languageKey] = template.starterCode;
      });

      const defaultLang = problem.languages[0];
      setStarterCodeMap(map);
      setLanguageKey(defaultLang.languageKey);
      setEditorLanguage(defaultLang.editorMode);
      setCode(map[defaultLang.languageKey] || "");
    }
  }, [problem]);

  const filteredProblems = useMemo(
    () =>
      problems.filter((item) =>
        item.title.toLowerCase().includes(search.toLowerCase())
      ),
    [problems, search]
  );

  const handleRun = async () => {
    try {
      setOutput("Running...");
      setShowConsole(true);
      setTestTab("output");

      const res = await api.post("/submissions/run", {
        problemId: problem.id,
        languageKey,
        sourceCode: code,
        customInput: testTab === "custom" ? customInput : "",
      });

      const formatted = res.data.results
        .map(
          (result, index) =>
            `Case ${index + 1}:\n` +
            `Input:\n${result.input}\n\n` +
            `Expected Output:\n${result.expectedOutput}\n\n` +
            `Your Output:\n${result.actualOutput}\n\n` +
            `Result: ${result.passed ? "Passed" : "Failed"}\n`
        )
        .join("\n-----------------\n");

      setOutput(formatted);
    } catch (err) {
      console.error(err);
      setOutput(err.response?.data?.message || "Error while running code");
    }
  };

  const handleSubmit = async () => {
    try {
      setOutput("Submitting...");
      setShowConsole(true);
      setTestTab("output");

      const res = await api.post("/submissions/submit", {
        problemId: problem.id,
        languageKey,
        sourceCode: code,
      });

      const { submissionId, verdict, results } = res.data;

      let outputText = `Submission ID: ${submissionId}\n\n`;
      results.forEach((testCase) => {
        outputText += `Hidden Test Case ${testCase.index}: ${
          testCase.passed ? "Passed" : "Failed"
        }\n`;
      });

      outputText += "\n====================\n";
      outputText += `FINAL VERDICT: ${
        verdict === "ACCEPTED" ? "ACCEPTED" : verdict
      }\n`;

      setOutput(outputText);
    } catch (err) {
      console.error(err);
      setOutput(err.response?.data?.message || "Error while submitting code");
    }
  };

  const renderQuestionPanel = () => (
    <>
      <div className="flex flex-wrap items-start gap-3">
        <div className="flex-1">
          <h2 className="text-2xl font-semibold tracking-tight text-white">
            {problem.title}
          </h2>
          <p className="mt-3 text-sm leading-6 text-slate-300">{problem.statement}</p>
        </div>
        <span
          className={`badge ${difficultyBadgeClass[problem.difficulty] || "badge-neutral"}`}
        >
          {problem.difficulty}
        </span>
      </div>

      <div className="mt-6 space-y-6">
        <div className="surface-card-soft">
          <h3 className="text-sm font-semibold uppercase tracking-[0.18em] text-slate-400">
            Constraints
          </h3>
          <p className="mt-3 whitespace-pre-wrap text-sm leading-6 text-slate-300">
            {problem.constraints || "No constraints provided."}
          </p>
        </div>
      </div>
    </>
  );

  return (
    <div
      ref={workspaceRef}
      className={`min-h-screen ${isDark ? "bg-[#08111f] text-slate-100" : "bg-slate-100 text-slate-900"}`}
    >
      {!isFullscreen && (
        <header className="border-b border-white/10 bg-slate-950/70 px-4 py-3 backdrop-blur-xl sm:px-6">
          <div className="mx-auto flex max-w-[1600px] flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div className="flex flex-wrap items-center gap-2">
              <button
                onClick={() => setSidebarOpen(true)}
                className="btn-secondary px-3"
                aria-label="Open problem navigator"
              >
                Problems
              </button>
              <button onClick={goPrev} className="btn-ghost px-3">
                Prev
              </button>
              <button onClick={goNext} className="btn-ghost px-3">
                Next
              </button>
              <div className="ml-0 lg:ml-3">
                <p className="text-xs uppercase tracking-[0.22em] text-slate-500">
                  Workspace
                </p>
                <p className="text-sm font-medium text-white sm:text-base">{problem.title}</p>
              </div>
            </div>

            <div className="flex flex-wrap items-center gap-2">
              <button onClick={() => setIsDark((value) => !value)} className="btn-secondary">
                {isDark ? "Light editor" : "Dark editor"}
              </button>
              <button onClick={toggleFullscreen} className="btn-secondary">
                Fullscreen
              </button>
            </div>
          </div>
        </header>
      )}

      <main className="mx-auto flex max-w-[1600px] flex-col gap-4 px-3 py-3 sm:px-4 lg:h-[calc(100vh-80px)] lg:flex-row lg:gap-0 lg:px-6 lg:py-5">
        <section className="surface-card flex min-h-[360px] flex-col lg:mr-4 lg:w-[44%] lg:min-h-0">
          <div className="flex flex-wrap gap-2 border-b border-white/10 pb-4">
            {PRIMARY_TABS.map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`rounded-full px-4 py-2 text-sm font-medium capitalize transition ${
                  activeTab === tab
                    ? "bg-teal-400/14 text-teal-200 ring-1 ring-teal-300/25"
                    : "text-slate-400 hover:bg-white/5 hover:text-white"
                }`}
              >
                {tab}
              </button>
            ))}
          </div>

          <div className="scrollbar-subtle mt-5 flex-1 overflow-y-auto pr-1">
            {activeTab === "question" ? (
              renderQuestionPanel()
            ) : (
              <div className="empty-state">
                <p className="text-base font-medium text-white">
                  {activeTab.charAt(0).toUpperCase() + activeTab.slice(1)} coming soon
                </p>
                <p className="mt-2 text-sm text-slate-400">
                  This tab is preserved in the flow and ready for future content.
                </p>
              </div>
            )}
          </div>
        </section>

        <section className="surface-card relative flex min-h-[520px] flex-1 flex-col p-0 lg:min-h-0">
          <div className="flex flex-col gap-3 border-b border-white/10 px-4 py-4 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex flex-wrap items-center gap-3">
              <label htmlFor="language-select" className="sr-only">
                Select language
              </label>
              <select
                id="language-select"
                value={languageKey}
                onChange={(e) => {
                  const key = e.target.value;
                  setLanguageKey(key);
                  const language = problem.languages.find((item) => item.languageKey === key);
                  setEditorLanguage(language.editorMode);
                  setCode(starterCodeMap[key] || "");
                }}
                className="input-base max-w-[220px] py-2.5"
              >
                {problem.languages.map((language) => (
                  <option key={language.languageKey} value={language.languageKey}>
                    {language.displayName}
                  </option>
                ))}
              </select>
              <span className="badge-neutral font-mono text-[11px]">{editorLanguage}</span>
            </div>

            <div className="flex flex-wrap items-center gap-2">
              <button
                onClick={() => setCode(starterCodeMap[languageKey] || "")}
                className="btn-secondary"
              >
                Reset code
              </button>
              <button onClick={toggleFullscreen} className="btn-ghost">
                {isFullscreen ? "Exit fullscreen" : "Expand"}
              </button>
            </div>
          </div>

          <div className="min-h-[320px] flex-1 overflow-hidden">
            <Editor
              height="100%"
              theme={isDark ? "vs-dark" : "light"}
              language={editorLanguage}
              value={code}
              onChange={setCode}
              onMount={(editor) => {
                editorRef.current = editor;
              }}
              options={{
                fontSize: 14,
                minimap: { enabled: false },
                padding: { top: 16 },
                scrollBeyondLastLine: false,
                smoothScrolling: true,
              }}
            />
          </div>

          <div
            className={`absolute inset-x-0 bottom-[72px] z-20 transition-transform duration-300 ${
              showConsole ? "translate-y-0" : "translate-y-full"
            }`}
            style={{ height: "min(42vh, 320px)" }}
          >
            <div className="flex h-full flex-col border-t border-white/10 bg-slate-950/96 backdrop-blur-xl">
              <div className="flex flex-wrap gap-2 border-b border-white/10 px-4 py-3">
                {DRAWER_TABS.map((tab) => (
                  <button
                    key={tab}
                    onClick={() => setTestTab(tab)}
                    className={`rounded-full px-4 py-2 text-sm font-medium capitalize transition ${
                      testTab === tab
                        ? "bg-teal-400/14 text-teal-200 ring-1 ring-teal-300/25"
                        : "text-slate-400 hover:bg-white/5 hover:text-white"
                    }`}
                  >
                    {tab === "custom" ? "Custom input" : tab}
                  </button>
                ))}
              </div>

              {testTab === "testcase" && sampleTestCases.length > 0 ? (
                <div className="flex flex-wrap gap-2 border-b border-white/10 px-4 py-3">
                  {sampleTestCases.map((_, idx) => (
                    <button
                      key={idx}
                      onClick={() => setActiveTestCase(idx)}
                      className={`rounded-full px-3 py-1.5 text-xs font-medium transition ${
                        activeTestCase === idx
                          ? "bg-white/10 text-white"
                          : "text-slate-400 hover:bg-white/5 hover:text-white"
                      }`}
                    >
                      Case {idx + 1}
                    </button>
                  ))}
                </div>
              ) : null}

              <div className="scrollbar-subtle flex-1 overflow-y-auto px-4 py-4 text-sm">
                {testTab === "testcase" && sampleTestCases[activeTestCase] ? (
                  <div className="space-y-4">
                    <div className="surface-card-soft">
                      <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Input</p>
                      <pre className="mt-3 whitespace-pre-wrap font-mono text-[13px] text-slate-200">
                        {sampleTestCases[activeTestCase].input}
                      </pre>
                    </div>
                    <div className="surface-card-soft">
                      <p className="text-xs uppercase tracking-[0.2em] text-slate-400">
                        Expected output
                      </p>
                      <pre className="mt-3 whitespace-pre-wrap font-mono text-[13px] text-slate-200">
                        {sampleTestCases[activeTestCase].expectedOutput}
                      </pre>
                    </div>
                  </div>
                ) : null}

                {testTab === "testcase" && sampleTestCases.length === 0 ? (
                  <div className="empty-state">
                    <p className="text-base font-medium text-white">No sample test cases</p>
                    <p className="mt-2 text-sm text-slate-400">
                      Try running the problem with custom input instead.
                    </p>
                  </div>
                ) : null}

                {testTab === "output" ? (
                  <div className="surface-card-soft h-full">
                    <pre className="whitespace-pre-wrap font-mono text-[13px] leading-6 text-emerald-300">
                      {output || "Run the code to inspect the output here."}
                    </pre>
                  </div>
                ) : null}

                {testTab === "custom" ? (
                  <div className="space-y-4">
                    <div>
                      <label htmlFor="custom-input" className="field-label">
                        Custom input
                      </label>
                      <textarea
                        id="custom-input"
                        value={customInput}
                        onChange={(e) => setCustomInput(e.target.value)}
                        placeholder={
                          sampleTestCases.length > 0
                            ? `Example:\n\n${sampleTestCases[0].input}`
                            : "Enter input exactly as stdin"
                        }
                        className="input-base min-h-36 resize-y"
                      />
                    </div>
                    <div className="flex justify-end">
                      <button onClick={handleRun} className="btn-primary">
                        Run custom input
                      </button>
                    </div>
                  </div>
                ) : null}
              </div>
            </div>
          </div>

          <div className="relative z-30 flex flex-col gap-3 border-t border-white/10 bg-slate-950/94 px-4 py-4 sm:flex-row sm:items-center sm:justify-between">
            <button onClick={() => setShowConsole((value) => !value)} className="btn-secondary">
              {showConsole ? "Hide results" : "Show test cases"}
            </button>

            <div className="flex flex-wrap gap-2">
              <button onClick={handleRun} className="btn-secondary">
                Run
              </button>
              <button onClick={handleSubmit} className="btn-primary">
                Submit
              </button>
            </div>
          </div>
        </section>
      </main>

      <AnimatePresence>
        {sidebarOpen ? (
          <>
            <motion.button
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setSidebarOpen(false)}
              className="fixed inset-0 z-40 bg-slate-950/70 backdrop-blur-sm"
              aria-label="Close problem navigator"
            />

            <motion.aside
              initial={{ x: "-100%" }}
              animate={{ x: 0 }}
              exit={{ x: "-100%" }}
              transition={{ duration: 0.22 }}
              className="fixed inset-y-0 left-0 z-50 flex w-full max-w-sm flex-col border-r border-white/10 bg-slate-950/96 shadow-2xl backdrop-blur-xl"
            >
              <div className="flex items-center justify-between border-b border-white/10 px-5 py-4">
                <div>
                  <p className="eyebrow">Navigator</p>
                  <h2 className="mt-2 text-lg font-semibold text-white">Problems</h2>
                </div>
                <button onClick={() => setSidebarOpen(false)} className="btn-ghost">
                  Close
                </button>
              </div>

              <div className="border-b border-white/10 px-5 py-4">
                <label htmlFor="problem-sidebar-search" className="sr-only">
                  Search problems
                </label>
                <input
                  id="problem-sidebar-search"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  placeholder="Search problems..."
                  className="input-base"
                />
              </div>

              <div className="scrollbar-subtle flex-1 overflow-y-auto p-3">
                {filteredProblems.map((item, idx) => (
                  <button
                    key={item.id}
                    onClick={() => {
                      navigate(`/problems/${item.id}`);
                      setSidebarOpen(false);
                    }}
                    className={`mb-2 flex w-full items-start gap-3 rounded-2xl border px-4 py-3 text-left transition ${
                      item.id === problem.id
                        ? "border-teal-300/25 bg-teal-400/10 text-white"
                        : "border-white/6 bg-white/[0.02] text-slate-300 hover:border-white/10 hover:bg-white/[0.04]"
                    }`}
                  >
                    <span className="badge-neutral mt-0.5 min-w-8 justify-center">{idx + 1}</span>
                    <span className="line-clamp-2">{item.title}</span>
                  </button>
                ))}
              </div>
            </motion.aside>
          </>
        ) : null}
      </AnimatePresence>
    </div>
  );
}
