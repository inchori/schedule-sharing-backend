package com.schedulsharing.web.club;

import com.schedulsharing.web.club.dto.*;
import com.schedulsharing.service.club.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/club")
public class ClubController {
    private final ClubService clubService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClubCreateResponse createClub(@RequestBody @Valid ClubCreateRequest clubCreateRequest, Authentication authentication) {
        return clubService.createClub(clubCreateRequest, authentication.getName());
    }

    @GetMapping("/{clubId}")
    public ClubGetResponse getClub(@PathVariable("clubId") Long clubId, Authentication authentication) {
        return clubService.getClub(clubId, authentication.getName());
    }

    @PutMapping("/{clubId}")
    public ClubUpdateResponse updateClub(@PathVariable("clubId") Long clubId, @RequestBody @Valid ClubUpdateRequest clubUpdateRequest, Authentication authentication) {
        return clubService.update(clubId,clubUpdateRequest, authentication.getName());
    }

    @DeleteMapping("/{clubId}")
    public ClubDeleteResponse deleteClub(@PathVariable("clubId") Long clubId, Authentication authentication) {
        return clubService.delete(clubId, authentication.getName());
    }

    @PostMapping("/{clubId}/invite")
    public ClubInviteResponse inviteClub(@RequestBody @Valid ClubInviteRequest clubInviteRequest, @PathVariable("clubId") Long clubId, Authentication authentication) {

        return clubService.invite(clubInviteRequest, clubId, authentication.getName());
    }
}
