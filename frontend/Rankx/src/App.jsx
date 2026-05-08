import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import ProblemList from "./pages/ProblemList";
import ProblemDetail from "./pages/ProblemDetail";
import Login from "./pages/Login";
import Register from "./pages/Register";
import LandingPage from "./pages/LandingPage";
import Home from "./pages/Home";
import Analytics from "./pages/Analytics";
import Onboarding from "./pages/Onboarding";
import StudyPlans from "./pages/StudyPlans";
import StudyPlanDetail from "./pages/StudyPlanDetail";
import MyProgress from "./pages/MyProgress";
import SubmissionHistory from "./pages/SubmissionHistory";
import SubmissionDetail from "./pages/SubmissionDetail";
import QuizList from "./pages/quiz/QuizList";
import QuizDetails from "./pages/quiz/QuizDetails";
import QuizAttempt from "./pages/quiz/QuizAttempt";
import QuizResult from "./pages/quiz/QuizResult";
import QuizHistory from "./pages/quiz/QuizHistory";
import QuizReview from "./pages/quiz/QuizReview";
import UserShell from "./components/UserShell";
import Account from "./pages/Account";
import Settings from "./pages/Settings";
import Billing from "./pages/Billing";
import Support from "./pages/Support";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Auth Pages */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/landing" element={<LandingPage />} />

        <Route element={<UserShell />}>
          <Route path="/home" element={<Home />} />
          <Route path="/analytics" element={<Analytics />} />
          <Route path="/onboarding" element={<Onboarding />} />
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="/study-plans" element={<StudyPlans />} />
          <Route path="/study-plans/:id" element={<StudyPlanDetail />} />
          <Route path="/my-progress" element={<MyProgress />} />
          <Route path="/problems" element={<ProblemList />} />
          <Route path="/submissions" element={<SubmissionHistory />} />
          <Route path="/submissions/:submissionId" element={<SubmissionDetail />} />
          <Route path="/quiz" element={<QuizList />} />
          <Route path="/quiz/history" element={<QuizHistory />} />
          <Route path="/quiz/:quizId" element={<QuizDetails />} />
          <Route path="/quiz/result" element={<QuizResult />} />
          <Route path="/quiz/review/:attemptId" element={<QuizReview />} />
          <Route path="/account" element={<Account />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="/billing" element={<Billing />} />
          <Route path="/support" element={<Support />} />
        </Route>

        <Route path="/problems/:id" element={<ProblemDetail />} />

        {/* Quiz Pages */}
        <Route path="/quiz/:quizId/attempt" element={<QuizAttempt />} />

        {/* Default route */}
        <Route path="*" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}


export default App;
