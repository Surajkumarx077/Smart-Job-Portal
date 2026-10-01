CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    resume_url VARCHAR(500),
    created_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    company VARCHAR(100),
    location VARCHAR(100),
    job_type VARCHAR(50),
    recruiter_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_jobs_recruiter FOREIGN KEY (recruiter_id) REFERENCES users(id)
);

CREATE TABLE job_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    cover_letter TEXT,
    status VARCHAR(30) NOT NULL,
    shortlisted_at TIMESTAMP(6),
    interview_scheduled_at TIMESTAMP(6),
    online_meeting_link VARCHAR(500),
    interview_notes TEXT,
    CONSTRAINT uq_job_applicant UNIQUE (job_id, applicant_id),
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id),
    CONSTRAINT fk_applications_applicant FOREIGN KEY (applicant_id) REFERENCES users(id)
);

CREATE TABLE parsed_resumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    raw_text LONGTEXT,
    skills TEXT,
    experience_summary TEXT,
    education TEXT,
    CONSTRAINT fk_parsed_resumes_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_jobs_recruiter ON jobs(recruiter_id);
CREATE INDEX idx_jobs_active_created ON jobs(active, created_at);
CREATE INDEX idx_applications_applicant ON job_applications(applicant_id);