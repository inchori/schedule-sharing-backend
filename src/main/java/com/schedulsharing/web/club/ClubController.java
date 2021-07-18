package com.schedulsharing.web.club;

import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.web.club.dto.ClubInviteRequest;
import com.schedulsharing.web.club.dto.ClubUpdateRequest;
import com.schedulsharing.web.dto.resource.ClubResource;
import com.schedulsharing.service.club.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/club")
public class ClubController {
    private final ClubService clubService;

    @PostMapping
    public ResponseEntity createClub(@RequestBody @Valid ClubCreateRequest clubCreateRequest, Authentication authentication) {
        EntityModel<ClubCreateResponse> entityModel = clubService.createClub(clubCreateRequest, authentication.getName());

        return ResponseEntity.created(ClubResource.getCreatedUri(entityModel.getContent().getClubId())).body(entityModel);
    }

    @GetMapping("/{clubId}")
    public ResponseEntity getClub(@PathVariable("clubId") Long clubId, Authentication authentication) {

        return ResponseEntity.ok(clubService.getClub(clubId, authentication.getName()));
    }

    @PutMapping("/{clubId}")
    public ResponseEntity updateClub(@PathVariable("clubId") Long clubId,
                                     @RequestBody @Valid ClubUpdateRequest clubUpdateRequest,
                                     Authentication authentication) {
        return ResponseEntity.ok(clubService.update(clubId,clubUpdateRequest, authentication.getName()));
    }

    @DeleteMapping("/{clubId}")
    public ResponseEntity deleteClub(@PathVariable("clubId") Long clubId, Authentication authentication) {

        return ResponseEntity.ok(clubService.delete(clubId, authentication.getName()));
    }

    @PostMapping("/{clubId}/invite")
    public ResponseEntity inviteClub(@RequestBody @Valid ClubInviteRequest clubInviteRequest,
                                     @PathVariable("clubId") Long clubId,
                                     Authentication authentication) {

        return ResponseEntity.ok(clubService.invite(clubInviteRequest, clubId, authentication.getName()));
    }
}
