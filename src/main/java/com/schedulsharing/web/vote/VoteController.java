package com.schedulsharing.web.vote;

import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateRequest;
import com.schedulsharing.service.vote.VoteService;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateResponse;
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
    public SuggestionVoteUpdateResponse updateVote(@PathVariable("voteId") Long id, @RequestBody SuggestionVoteUpdateRequest updateRequest, Authentication authentication) {
        return voteService.updateVote(id, updateRequest, authentication.getName());
    }
}
