# AI Resume Screening Service

NLP-based job-resume matching using TF-IDF and cosine similarity.

## Setup

```bash
cd ai-service
pip install -r requirements.txt
```

## Run

```bash
uvicorn main:app --reload --port 5000
```

API: http://localhost:5000

## Endpoints

- `POST /score` - Match job description with resume
  - Body: `{ "job_description": "...", "resume_text": "..." }`
  - Response: `{ "score": 78.5, "matched_skills": [...], "extracted_skills": [...] }`
- `GET /health` - Health check
