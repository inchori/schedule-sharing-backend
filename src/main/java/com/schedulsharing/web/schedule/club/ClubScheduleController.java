package com.schedulsharing.web.schedule.club;

import com.schedulsharing.service.schedule.ClubScheduleService;
import com.schedulsharing.web.schedule.club.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubSchedule")
public class ClubScheduleController {
    private final ClubScheduleService clubScheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClubScheduleCreateResponse createClubSchedule(@RequestBody @Valid ClubScheduleCreateRequest createRequest, Authentication authentication) {
        return clubScheduleService.create(createRequest, authentication.getName());
    }

    @GetMapping("/{id}")
    public ClubScheduleResponse getClubSchedule(@PathVariable("id") Long id) {
        return clubScheduleService.getClubSchedule(id);
    }

    @GetMapping("/list/{clubId}")
    public List<ClubScheduleResponse> getClubScheduleList(@PathVariable("clubId") Long clubId, @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return clubScheduleService.getClubScheduleList(clubId, yearMonth);
    }

    @PutMapping("/{id}")
    public ClubScheduleUpdateResponse updateClubSchedule(@PathVariable("id") Long id, @RequestBody @Valid ClubScheduleUpdateRequest clubScheduleUpdateRequest, Authentication authentication) {
        return clubScheduleService.update(id, clubScheduleUpdateRequest, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ClubScheduleDeleteResponse deleteClubSchedule(@PathVariable("id") Long id, Authentication authentication) {
        return clubScheduleService.delete(id, authentication.getName());
    }
}
