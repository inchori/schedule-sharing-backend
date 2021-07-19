package com.schedulsharing.web.schedule.club;

import com.schedulsharing.web.dto.resource.SuggestionResource;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateResponse;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionUpdateRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionVoteRequest;
import com.schedulsharing.service.suggestion.ScheduleSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/suggestion")
@RequiredArgsConstructor
public class ScheduleSuggestionController {
    private final ScheduleSuggestionService scheduleSuggestionService;

    @PostMapping
    public ResponseEntity createSuggestion(@RequestBody @Valid SuggestionCreateRequest suggestionCreateRequest, Authentication authentication) {
        EntityModel<SuggestionCreateResponse> entityModel = scheduleSuggestionService.create(suggestionCreateRequest, authentication.getName());

        return ResponseEntity.created(SuggestionResource.getCreatedUri(entityModel.getContent().getId())).body(entityModel);
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity voteSuggestion(@PathVariable("id") Long suggestionId,
                                         @RequestBody SuggestionVoteRequest suggestionVoteRequest,
                                         Authentication authentication) {

        return ResponseEntity.ok(scheduleSuggestionService.vote(suggestionId, suggestionVoteRequest, authentication.getName()));
    }


    @GetMapping("/{id}")
    public ResponseEntity getSuggestion(@PathVariable("id") Long id, Authentication authentication) {

        return ResponseEntity.ok(scheduleSuggestionService.getSuggestion(id, authentication.getName()));
    }

    @GetMapping("/confirmList/{clubId}")
    public ResponseEntity getSuggestionListConfirm(@PathVariable("clubId") Long clubId,
                                                   @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
                                                   Authentication authentication) {

        return ResponseEntity.ok(scheduleSuggestionService.getSuggestionListConfirm(clubId, yearMonth, authentication.getName()));
    }

    @GetMapping("/list/{clubId}")
    public ResponseEntity getSuggestionList(@PathVariable("clubId") Long clubId,
                                            @RequestParam("now") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate now,
                                            Authentication authentication) {

        return ResponseEntity.ok(scheduleSuggestionService.getSuggestionList(clubId, now, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateSuggestion(@PathVariable("id") Long id,
                                           @RequestBody @Valid SuggestionUpdateRequest suggestionUpdateRequest,
                                           Authentication authentication) {

        return ResponseEntity.ok(scheduleSuggestionService.update(id, suggestionUpdateRequest, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteSuggestion(@PathVariable("id") Long id, Authentication authentication) {

        return ResponseEntity.ok(scheduleSuggestionService.delete(id, authentication.getName()));
    }
}
