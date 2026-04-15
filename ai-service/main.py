"""
AI Resume Screening Service - NLP-based job-resume matching
Extracts skills and returns match score (0-100%)
"""
import re
from typing import List

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI(title="AI Resume Screening Service")

# Common skills for extraction
KNOWN_SKILLS = {
    "java", "python", "javascript", "typescript", "react", "angular", "vue", "node", "nodejs",
    "spring", "spring boot", "django", "flask", "fastapi", "express", "hibernate",
    "sql", "mysql", "postgresql", "mongodb", "redis", "aws", "docker", "kubernetes",
    "git", "rest", "graphql", "microservices", "agile", "scrum",
    "machine learning", "deep learning", "nlp", "data science", "tensorflow", "pytorch",
    "html", "css", "bootstrap", "tailwind", "jquery", "redux", "next.js",
    "c", "c++", "c#", "go", "golang", "rust", "kotlin", "swift",
    "jira", "jenkins", "ci/cd", "terraform", "ansible", "linux", "unix",
}

STOP_WORDS = {
    "the", "and", "for", "with", "from", "have", "has", "had", "this", "that",
    "these", "those", "will", "would", "could", "should", "experience", "education",
    "work", "project", "team", "year", "years", "need", "required", "apply",
}


class ScoreRequest(BaseModel):
    job_description: str
    resume_text: str


class ScoreResponse(BaseModel):
    score: float
    matched_skills: List[str]
    extracted_skills: List[str]


def extract_skills(text: str) -> List[str]:
    """Extract skills from resume text."""
    if not text:
        return []
    text_lower = text.lower()
    found = set()
    for skill in KNOWN_SKILLS:
        if skill in text_lower:
            found.add(skill)
    # Also extract word tokens (2-30 chars) that might be skills
    words = re.findall(r"\b[a-zA-Z][a-zA-Z0-9+#.]*\b", text)
    for w in words:
        wl = w.lower()
        if 2 <= len(wl) <= 30 and wl not in STOP_WORDS:
            found.add(wl)
    return sorted(list(found))[:50]


def compute_tfidf_score(job_text: str, resume_text: str) -> float:
    """Compute cosine similarity between job and resume using TF-IDF."""
    if not job_text.strip() or not resume_text.strip():
        return 0.0
    vectorizer = TfidfVectorizer(
        stop_words="english",
        ngram_range=(1, 2),
        min_df=1,
        max_features=5000,
    )
    try:
        tfidf_matrix = vectorizer.fit_transform([job_text, resume_text])
        similarity = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])[0][0]
        return min(100.0, max(0.0, similarity * 100))
    except Exception:
        return 0.0


@app.post("/score", response_model=ScoreResponse)
def score_resume(request: ScoreRequest):
    """Match job description with resume and return score + skills."""
    job_text = request.job_description or ""
    resume_text = request.resume_text or ""

    score = compute_tfidf_score(job_text, resume_text)
    extracted_skills = extract_skills(resume_text)

    # Matched skills = job keywords that appear in resume
    job_words = set(
        w.lower()
        for w in re.findall(r"\b[a-zA-Z]{2,}\b", job_text)
        if w.lower() not in STOP_WORDS
    )
    matched = [s for s in extracted_skills if s in job_words or any(s in w for w in job_words)]
    matched = list(set(matched))[:20]

    return ScoreResponse(
        score=round(score, 2),
        matched_skills=matched,
        extracted_skills=extracted_skills,
    )


@app.get("/health")
def health():
    return {"status": "ok"}
