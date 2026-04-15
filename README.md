# Smart Job Portal - Run Guide

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.0, Spring Security (JWT), Spring Data JPA, MySQL/H2.
- **Frontend**: React.js, Vite, React Router, Axios, Lucide React, Vanilla CSS.
- **AI Service (Optional)**: Python 3.10, FastAPI/Uvicorn.

## Prerequisites

- Java 17+
- MySQL 8+ (for MySQL mode)
- Python 3.10+ (only if you want to run AI service)

## Run Backend (with MySQL)

1. Create database in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS smart_job_portal;
```

2. Open `backend/src/main/resources/application.properties` and confirm:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_job_portal?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

3. Start backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

4. Backend URL:

```text
http://localhost:8080
```

## Run Backend (without MySQL - H2 dev mode)

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

- Backend URL: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

## Run Optional AI Service

```powershell
cd ai-service
pip install -r requirements.txt
uvicorn main:app --reload --port 5000
```

- AI URL: `http://localhost:5000`
- Health check: `http://localhost:5000/health`

If AI service is running, set this in `backend/src/main/resources/application.properties`:

```properties
ai.screening.url=http://localhost:5000
```

## Verify Build

From `backend`:

```powershell
.\mvnw.cmd clean -DskipTests compile
```

## Run Frontend

The frontend is a modern **React SPA** powered by **Vite**.

From project root:

```powershell
cd frontend
npm install
npm run dev
```

- Frontend URL: `http://localhost:5173`
- API calls to `/api` are automatically proxied to the backend at `http://localhost:8081` via `vite.config.js`.

## Run Full Project (Recommended)

Open 2-3 terminals and run:

1. Backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

2. Frontend:

```powershell
cd frontend
npm run dev
```

3. (Optional) AI service:

```powershell
cd ai-service
uvicorn main:app --reload --port 5000
python -m uvicorn main:app --reload --port 5000
```

## Create First Admin (Recommended)

Public registration now allows only `STUDENT` and `RECRUITER` after the first admin policy change.

To bootstrap the very first admin safely, set env vars before starting backend:

```powershell
$env:APP_BOOTSTRAP_ADMIN_EMAIL="admin@smartjobportal.com"
$env:APP_BOOTSTRAP_ADMIN_PASSWORD="ChangeMe123"
$env:APP_BOOTSTRAP_ADMIN_FULL_NAME="System Admin"
cd backend
.\mvnw.cmd spring-boot:run
```

Notes:
- Bootstrap runs only when no `ADMIN` exists.
- Once the admin is created, additional admin self-registration is blocked.
- You can clear these env vars after first startup.

## Frontend Planning Reference

Use this detailed blueprint to implement frontend modules, role-based features, and API integration:


