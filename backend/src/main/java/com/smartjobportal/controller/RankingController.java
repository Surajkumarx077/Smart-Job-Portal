package com.smartjobportal.controller;

import com.smartjobportal.dto.ranking.RankedCandidateResponse;
import com.smartjobportal.security.UserPrincipal;
import com.smartjobportal.service.CandidateRankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final CandidateRankingService rankingService;

    public RankingController(CandidateRankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<RankedCandidateResponse>> getRankedCandidates(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(rankingService.getRankedCandidates(jobId, principal.getId()));
    }
}
