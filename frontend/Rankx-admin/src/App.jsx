import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./dashboard/Dashboard";
import AdminShell from "./components/AdminShell";
import ManageQuizzes from "./modules/quizzes/pages/ManageQuizzes";
import CreateQuiz from "./modules/quizzes/pages/CreateQuiz";
import EditQuiz from "./modules/quizzes/pages/EditQuiz";
import QuizPreview from "./modules/quizzes/pages/QuizPreview";
import ManageQuestions from "./modules/questions/pages/ManageQuestions";
import CreateQuestion from "./modules/questions/pages/CreateQuestion";
import EditQuestion from "./modules/questions/pages/EditQuestion";
import AdminUsers from "./pages/AdminUsers";
import AdminPlans from "./pages/AdminPlans";
import AdminPayments from "./pages/AdminPayments";
import AdminReports from "./pages/AdminReports";
import AdminSupport from "./pages/AdminSupport";
import AdminSettings from "./pages/AdminSettings";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Auth Pages */}
        <Route path="/login" element={<Login />} />

        <Route element={<AdminShell />}>
          <Route path="/" element={<Navigate to="/admin/dashboard" replace />} />
          <Route path="/admin/dashboard" element={<Dashboard />} />

          {/* Quiz Pages */}
          <Route path="/quizzes" element={<ManageQuizzes />} />
          <Route path="/quizzes/create" element={<CreateQuiz />} />
          <Route path="/quizzes/:id/preview" element={<QuizPreview />} />
          <Route path="/quizzes/:id/edit" element={<EditQuiz />} />

          {/* Questions */}
          <Route path="/quizzes/:quizId/questions" element={<ManageQuestions />} />
          <Route path="/quizzes/:quizId/questions/create" element={<CreateQuestion />} />
          <Route path="/quizzes/:quizId/questions/:questionId/edit" element={<EditQuestion />} />

          {/* Placeholder management routes */}
          <Route path="/admin/users" element={<AdminUsers />} />
          <Route path="/admin/plans" element={<AdminPlans />} />
          <Route path="/admin/payments" element={<AdminPayments />} />
          <Route path="/admin/reports" element={<AdminReports />} />
          <Route path="/admin/support" element={<AdminSupport />} />
          <Route path="/admin/settings" element={<AdminSettings />} />
        </Route>

        {/* Default route */}
        <Route path="*" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
