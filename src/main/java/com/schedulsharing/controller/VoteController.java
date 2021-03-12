package com.schedulsharing.controller;

import com.schedulsharing.dto.voteCheck.SuggestionVoteUpdateRequest;
import com.schedulsharing.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PutMapping("/{voteId}")
    public ResponseEntity updateVote(@PathVariable("voteId") Long id, @RequestBody SuggestionVoteUpdateRequest updateRequest, Authentication authentication) {
        return ResponseEntity.ok(voteService.updateVote(id, updateRequest, authentication.getName()));
    }
}
