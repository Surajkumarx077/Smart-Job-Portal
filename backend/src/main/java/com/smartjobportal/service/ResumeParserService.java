package com.smartjobportal.service;

import com.smartjobportal.entity.ParsedResume;
import com.smartjobportal.entity.User;
import com.smartjobportal.repository.ParsedResumeRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeParserService {

    private static final Set<String> KNOWN_SKILLS = Set.of(
            "java", "python", "javascript", "typescript", "react", "angular", "vue", "node", "nodejs",
            "spring", "spring boot", "django", "flask", "fastapi", "express", "hibernate",
            "sql", "mysql", "postgresql", "mongodb", "redis", "aws", "docker", "kubernetes",
            "git", "rest", "rest api", "graphql", "microservices", "agile", "scrum",
            "machine learning", "deep learning", "nlp", "data science", "tensorflow", "pytorch",
            "html", "css", "bootstrap", "tailwind", "jquery", "redux", "next.js",
            "c", "c++", "c#", "go", "golang", "rust", "kotlin", "swift",
            "jira", "jenkins", "ci/cd", "terraform", "ansible", "linux", "unix",
            "communication", "leadership", "teamwork", "problem solving", "analytical"
    );

    private static final Pattern SKILL_PATTERN = Pattern.compile(
            "\\b([A-Za-z][a-z]*(?:\\s*[+.#]?\\s*[A-Za-z0-9]+)*)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private final ParsedResumeRepository parsedResumeRepository;

    public ResumeParserService(ParsedResumeRepository parsedResumeRepository) {
        this.parsedResumeRepository = parsedResumeRepository;
    }

    public ParsedResume parseAndSave(byte[] pdfBytes, User user) throws IOException {
        String rawText = extractTextFromPdf(pdfBytes);
        ParsedResume parsed = new ParsedResume();
        parsed.setUser(user);
        parsed.setRawText(rawText);
        parsed.setSkills(extractSkills(rawText));
        parsed.setExperienceSummary(extractExperienceSection(rawText));
        parsed.setEducation(extractEducationSection(rawText));

        parsedResumeRepository.findByUser(user).ifPresent(parsedResumeRepository::delete);
        return parsedResumeRepository.save(parsed);
    }

    public String extractTextFromPdf(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractSkills(String text) {
        String lower = text.toLowerCase();
        Set<String> found = new HashSet<>();

        for (String skill : KNOWN_SKILLS) {
            if (lower.contains(skill)) {
                found.add(skill);
            }
        }

        Matcher matcher = SKILL_PATTERN.matcher(text);
        while (matcher.find()) {
            String word = matcher.group(1).toLowerCase().trim();
            if (word.length() >= 2 && word.length() <= 30 && !isStopWord(word)) {
                found.add(word);
            }
        }

        return found.stream().limit(50).sorted().collect(Collectors.joining(", "));
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("the", "and", "for", "with", "from", "have", "has", "had",
                "this", "that", "these", "those", "will", "would", "could", "should",
                "experience", "education", "work", "project", "team", "year", "years");
        return stopWords.contains(word);
    }

    private String extractExperienceSection(String text) {
        return extractSection(text, "(?i)(experience|work experience|professional experience|employment)\\s*");
    }

    private String extractEducationSection(String text) {
        return extractSection(text, "(?i)(education|academic|qualification)\\s*");
    }

    private String extractSection(String text, String sectionPattern) {
        Pattern pattern = Pattern.compile(sectionPattern + "([\\s\\S]*?)(?=(?i)(experience|education|skills|projects|summary|objective|$))");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String section = matcher.group(1).trim();
            return section.length() > 500 ? section.substring(0, 500) + "..." : section;
        }
        return "";
    }

    public Optional<ParsedResume> getParsedResume(Long userId) {
        return parsedResumeRepository.findByUserId(userId);
    }

    public String getSearchableText(Long userId) {
        return parsedResumeRepository.findByUserId(userId)
                .map(pr -> (pr.getRawText() != null ? pr.getRawText() : "") + " " +
                        (pr.getSkills() != null ? pr.getSkills() : "") + " " +
                        (pr.getExperienceSummary() != null ? pr.getExperienceSummary() : "") + " " +
                        (pr.getEducation() != null ? pr.getEducation() : ""))
                .orElse("");
    }
}
