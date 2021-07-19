package com.schedulsharing.web.schedule.club;

import com.schedulsharing.web.schedule.club.dto.ClubScheduleCreateRequest;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleCreateResponse;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleUpdateRequest;
import com.schedulsharing.web.dto.resource.ClubScheduleResource;
import com.schedulsharing.service.schedule.ClubScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubSchedule")
public class ClubScheduleController {
    private final ClubScheduleService clubScheduleService;

    @PostMapping
    public ResponseEntity createClubSchedule(@RequestBody @Valid ClubScheduleCreateRequest createRequest,
                                             Authentication authentication, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        EntityModel<ClubScheduleCreateResponse> entityModel = clubScheduleService.create(createRequest, authentication.getName());

        return ResponseEntity.created(ClubScheduleResource.getCreatedUri(entityModel.getContent().getId())).body(entityModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity getClubSchedule(@PathVariable("id") Long id, Authentication authentication) {

        return ResponseEntity.ok(clubScheduleService.getClubSchedule(id, authentication.getName()));
    }

    @GetMapping("/list/{clubId}")
    public ResponseEntity getClubScheduleList(@PathVariable("clubId") Long clubId,
                                              @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
                                              Authentication authentication) {
        return ResponseEntity.ok(clubScheduleService.getClubScheduleList(clubId, yearMonth, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateClubSchedule(@PathVariable("id") Long id,
                                             @RequestBody @Valid ClubScheduleUpdateRequest clubScheduleUpdateRequest,
                                             Authentication authentication) {

        return ResponseEntity.ok(clubScheduleService.update(id, clubScheduleUpdateRequest, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteClubSchedule(@PathVariable("id") Long id, Authentication authentication) {

        return ResponseEntity.ok(clubScheduleService.delete(id, authentication.getName()));
    }
}
