import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProblemList from "./pages/ProblemList";
import ProblemDetail from "./pages/ProblemDetail";
import Login from "./pages/Login";
import Register from "./pages/Register";
import LandingPage from "./pages/LandingPage";
import Home from "./pages/Home";
import SubmissionHistory from "./pages/SubmissionHistory";
import SubmissionDetail from "./pages/SubmissionDetail";
import QuizList from "./pages/quiz/QuizList";
import QuizDetails from "./pages/quiz/QuizDetails";
import QuizAttempt from "./pages/quiz/QuizAttempt";
import QuizResult from "./pages/quiz/QuizResult";
import QuizHistory from "./pages/quiz/QuizHistory";
import QuizReview from "./pages/quiz/QuizReview";


function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Auth Pages */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/problems" element={<ProblemList />} />
        <Route path="/problems/:id" element={<ProblemDetail />} />
        <Route path="/submissions" element={<SubmissionHistory />} />
        <Route path="/submissions/:submissionId" element={<SubmissionDetail />} />
        <Route path="/landing" element={<LandingPage />} />
        <Route path="/home" element={<Home />} />
        <Route path="/" element={<Home />} />

        {/* Quiz Pages */}
        <Route path="/quiz" element={<QuizList />} />
        <Route path="/quiz/history" element={<QuizHistory />} />
        <Route path="/quiz/:quizId" element={<QuizDetails />} />
        <Route path="/quiz/:quizId/attempt" element={<QuizAttempt />} />
        <Route path="/quiz/result" element={<QuizResult />} />
        <Route path="/quiz/review/:attemptId" element={<QuizReview />} />

        {/* Default route */}
        <Route path="*" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}


export default App;
