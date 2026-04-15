import { createBrowserRouter } from 'react-router-dom';
import AppShell from '../components/layout/AppShell';
import LoginPage from '../features/auth/LoginPage';
import RegisterPage from '../features/auth/RegisterPage';
import PublicJobsPage from '../features/jobs/PublicJobsPage';
import JobDetailsPage from '../features/jobs/JobDetailsPage';
import ResumeUploadPage from '../features/resumes/ResumeUploadPage';
import MyApplicationsPage from '../features/applications/MyApplicationsPage';
import MyJobsPage from '../features/jobs/recruiter/MyJobsPage';
import JobFormPage from '../features/jobs/recruiter/JobFormPage';
import JobApplicationsPage from '../features/applications/recruiter/JobApplicationsPage';
import RankedCandidatesPage from '../features/ranking/RankedCandidatesPage';
import AdminDashboardPage from '../features/admin/AdminDashboardPage';
import UsersPage from '../features/admin/UsersPage';
import JobsPage from '../features/admin/JobsPage';
import ApplicationsPage from '../features/admin/ApplicationsPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppShell />,
    children: [
      { path: '/', element: <PublicJobsPage /> },
      { path: '/jobs/:id', element: <JobDetailsPage /> },
      { path: '/login', element: <LoginPage /> },
      { path: '/register', element: <RegisterPage /> },
      
      // Student
      { path: '/student/resume', element: <ResumeUploadPage /> },
      { path: '/student/applications', element: <MyApplicationsPage /> },

      // Recruiter
      { path: '/recruiter/jobs', element: <MyJobsPage /> },
      { path: '/recruiter/jobs/new', element: <JobFormPage /> },
      { path: '/recruiter/jobs/:id/edit', element: <JobFormPage /> },
      { path: '/recruiter/jobs/:id/applications', element: <JobApplicationsPage /> },
      { path: '/recruiter/jobs/:id/ranking', element: <RankedCandidatesPage /> },

      // Admin
      { path: '/admin/dashboard', element: <AdminDashboardPage /> },
      { path: '/admin/users', element: <UsersPage /> },
      { path: '/admin/jobs', element: <JobsPage /> },
      { path: '/admin/applications', element: <ApplicationsPage /> },
    ]
  }
]);
