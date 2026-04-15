# Smart Job Portal Frontend Implementation Guide

This guide gives a practical structure and API map so frontend development can start immediately.

## 1) Frontend Architecture

Suggested React + Vite structure:

frontend/
  src/
    app/
      router.jsx
      providers.jsx
    api/
      client.js
      auth.api.js
      jobs.api.js
      applications.api.js
      resumes.api.js
      ranking.api.js
      admin.api.js
    features/
      auth/
        LoginPage.jsx
        RegisterPage.jsx
        auth.store.js
      jobs/
        PublicJobsPage.jsx
        JobDetailsPage.jsx
        recruiter/
          MyJobsPage.jsx
          JobFormPage.jsx
      applications/
        candidate/
          MyApplicationsPage.jsx
          ApplyDrawer.jsx
        recruiter/
          JobApplicationsPage.jsx
      resumes/
        ResumeUploadPage.jsx
        ParsedResumeCard.jsx
      ranking/
        RankedCandidatesPage.jsx
      admin/
        AdminDashboardPage.jsx
        UsersPage.jsx
        JobsPage.jsx
        ApplicationsPage.jsx
    components/
      layout/
        AppShell.jsx
        TopNav.jsx
      common/
        Loader.jsx
        EmptyState.jsx
        ErrorAlert.jsx
        ConfirmDialog.jsx
      guards/
        RequireAuth.jsx
        RequireRole.jsx
    hooks/
      useAuth.js
      usePagination.js
    utils/
      token.js
      date.js
      role.js
    styles/
      theme.css

## 2) Roles and Core Features

### Public (no login)
- View active jobs list
- View job details
- Register account
- Login

### Student
- All Public features
- Upload resume
- View own resume URL
- View parsed resume fields (skills, experience, education)
- Apply to job with optional cover letter
- View own applications

### Recruiter
- All Public features
- Create job
- Edit own job
- Delete own job
- View own posted jobs
- View applications for a specific job
- View ranked candidates for a job

### Admin
- Dashboard stats
- Paginated users list
- Full jobs list
- Full applications list

## 3) API Base and Auth Rules

- Backend base URL from backend configuration: http://localhost:8081
- Frontend should call API with /api prefix
- Send JWT token in Authorization header:
  Authorization: Bearer <token>

Token source:
- POST /api/auth/login or POST /api/auth/register response returns token

## 4) Endpoint Catalog for UI Integration

## Health and root

1. GET /
- Auth: No
- Use: quick backend status check
- Response:
{
  "message": "Smart Job Portal backend is running",
  "auth": "/api/auth/login",
  "jobs": "/api/jobs/public"
}

## Auth

1. POST /api/auth/register
- Auth: No
- Request body:
{
  "email": "student@example.com",
  "password": "secret123",
  "fullName": "Alex Student",
  "role": "STUDENT"
}
- Valid role values: STUDENT, RECRUITER, ADMIN
- Response:
{
  "token": "<jwt>",
  "email": "student@example.com",
  "fullName": "Alex Student",
  "role": "STUDENT",
  "userId": 12
}

2. POST /api/auth/login
- Auth: No
- Request body:
{
  "email": "student@example.com",
  "password": "secret123"
}
- Response shape: same as register

## Jobs

1. GET /api/jobs/public
- Auth: No
- Use: landing page jobs list
- Response: JobResponse[]

2. GET /api/jobs/{id}
- Auth: No
- Use: job details page
- Response: JobResponse

3. POST /api/jobs
- Auth: Yes
- Intended role: RECRUITER
- Request body:
{
  "title": "Java Developer",
  "description": "Build backend services",
  "company": "Acme",
  "location": "Bangalore",
  "jobType": "FULL_TIME"
}
- Response: JobResponse

4. GET /api/jobs/my
- Auth: Yes
- Intended role: RECRUITER
- Response: JobResponse[]

5. PUT /api/jobs/{id}
- Auth: Yes
- Intended role: RECRUITER
- Request body: same as create
- Response: JobResponse

6. DELETE /api/jobs/{id}
- Auth: Yes
- Intended role: RECRUITER
- Response: 204 No Content

JobResponse fields:
- id: number
- title: string
- description: string
- company: string
- location: string
- jobType: string
- recruiterId: number
- recruiterName: string
- createdAt: ISO datetime string
- active: boolean

## Applications

1. POST /api/applications
- Auth: Yes
- Intended role: STUDENT
- Request body:
{
  "jobId": 5,
  "coverLetter": "I am a strong fit for this role"
}
- Response: ApplicationResponse

2. GET /api/applications/my
- Auth: Yes
- Intended role: STUDENT
- Response: ApplicationResponse[]

3. GET /api/applications/job/{jobId}
- Auth: Yes
- Intended role: RECRUITER
- Response: ApplicationResponse[]

ApplicationResponse fields:
- id: number
- jobId: number
- jobTitle: string
- applicantId: number
- applicantName: string
- applicantEmail: string
- resumeUrl: string
- coverLetter: string
- createdAt: ISO datetime string

## Resume

1. POST /api/resumes/upload
- Auth: Yes
- Intended role: STUDENT
- Content-Type: multipart/form-data
- Form field: file
- Response:
{
  "resumeUrl": "http://.../uploads/resumes/...pdf"
}

2. GET /api/resumes/me
- Auth: Yes
- Intended role: STUDENT
- Response:
{
  "resumeUrl": "http://.../uploads/resumes/...pdf"
}

3. GET /api/resumes/parsed
- Auth: Yes
- Intended role: STUDENT
- Response 200:
{
  "skills": "Java, Spring Boot, React",
  "experienceSummary": "2 years in backend",
  "education": "B.Tech CSE"
}
- Response 404 when no parsed resume exists

## Ranking (Recruiter)

1. GET /api/ranking/job/{jobId}
- Auth: Yes
- Intended role: RECRUITER
- Response: RankedCandidateResponse[]

RankedCandidateResponse fields:
- applicationId: number
- applicantId: number
- applicantName: string
- applicantEmail: string
- resumeUrl: string
- coverLetter: string
- appliedAt: ISO datetime string
- matchScore: number
- matchedSkills: string[]

## Admin

All admin endpoints require ADMIN role.

1. GET /api/admin/stats
- Response:
{
  "totalUsers": 120,
  "totalJobs": 45,
  "totalApplications": 320,
  "usersByRole": {
    "STUDENT": 90,
    "RECRUITER": 25,
    "ADMIN": 5
  }
}

2. GET /api/admin/users?page=0&size=20
- Response: Spring Page<AdminUserResponse>
- Important Page fields for frontend:
  - content: AdminUserResponse[]
  - totalElements
  - totalPages
  - number
  - size

3. GET /api/admin/jobs
- Response: JobResponse[]

4. GET /api/admin/applications
- Response: ApplicationResponse[]

AdminUserResponse fields:
- id: number
- email: string
- fullName: string
- role: STUDENT | RECRUITER | ADMIN
- hasResume: boolean
- createdAt: ISO datetime string

## 5) Expected Error Shape

Validation and runtime errors return JSON maps.

Examples:

1. Validation error:
{
  "email": "must be a well-formed email address",
  "password": "Password must be at least 6 characters"
}

2. Business/runtime error:
{
  "error": "Job not found"
}

3. Login failure:
{
  "error": "Invalid email or password"
}

Frontend recommendation:
- If response has error, show that as toast/banner
- Else if object has field-wise keys, map to form field errors

## 6) Route Map for Frontend Pages

Public routes:
- / -> Public jobs list
- /jobs/:id -> Job details
- /login -> Login
- /register -> Register

Student routes:
- /student/resume -> Upload + view resume + parsed data
- /student/applications -> My applications

Recruiter routes:
- /recruiter/jobs -> My jobs
- /recruiter/jobs/new -> Create job
- /recruiter/jobs/:id/edit -> Edit job
- /recruiter/jobs/:id/applications -> Applicants list
- /recruiter/jobs/:id/ranking -> Ranked candidates

Admin routes:
- /admin/dashboard -> Stats widgets
- /admin/users -> Users table with pagination
- /admin/jobs -> All jobs table
- /admin/applications -> All applications table

## 7) Frontend API Client Checklist

- Add interceptor to include JWT token
- Add interceptor for 401 to redirect to /login
- Centralize API calls in src/api modules
- Keep role in auth store and use route guards
- Use optimistic UI only for delete/update where rollback is simple

## 8) Quick Start Implementation Order

1. Build auth module and token persistence
2. Build public jobs pages
3. Build student resume + apply flows
4. Build recruiter CRUD + applicants + ranking
5. Build admin dashboard pages
6. Add loading skeletons and error boundaries
7. Add integration tests for critical flows
