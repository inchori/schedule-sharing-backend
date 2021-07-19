package com.schedulsharing.service.schedule;

import com.schedulsharing.domain.club.Club;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.schedule.ClubSchedule;
import com.schedulsharing.domain.schedule.repository.clubSchedule.ClubScheduleRepository;
import com.schedulsharing.excpetion.PermissionException;
import com.schedulsharing.service.club.exception.ClubNotFoundException;
import com.schedulsharing.service.member.exception.MemberNotFoundException;
import com.schedulsharing.service.schedule.exception.ClubScheduleNotFoundException;
import com.schedulsharing.web.dto.resource.ClubScheduleResource;
import com.schedulsharing.web.schedule.club.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClubScheduleService {
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final ClubScheduleRepository clubScheduleRepository;
    private final ModelMapper modelMapper;

    public EntityModel<ClubScheduleCreateResponse> create(ClubScheduleCreateRequest createRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Club club = clubRepository.findById(createRequest.getClubId()).orElseThrow(ClubNotFoundException::new);

        ClubSchedule clubSchedule = ClubSchedule.createClubSchedule(createRequest, member, club);
        ClubSchedule savedClubSchedule = clubScheduleRepository.save(clubSchedule);

        ClubScheduleCreateResponse createResponse = modelMapper.map(savedClubSchedule, ClubScheduleCreateResponse.class);

        return ClubScheduleResource.createClubScheduleLink(createResponse);
    }

    @Transactional(readOnly = true)
    public EntityModel<ClubScheduleResponse> getClubSchedule(Long id, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ClubSchedule clubSchedule = clubScheduleRepository.findById(id).orElseThrow(ClubScheduleNotFoundException::new);
        ClubScheduleResponse response = modelMapper.map(clubSchedule, ClubScheduleResponse.class);
        return ClubScheduleResource.getClubScheduleLink(response, member.getEmail());
    }

    @Transactional(readOnly = true)
    public CollectionModel<EntityModel<ClubScheduleResponse>> getClubScheduleList(Long clubId, YearMonth yearMonth, String email) {
        List<ClubSchedule> clubSchedules = clubScheduleRepository.findAllByClubId(clubId, yearMonth);
        List<ClubScheduleResponse> responseList = clubSchedules.stream()
                .map(clubSchedule -> modelMapper.map(clubSchedule, ClubScheduleResponse.class))
                .collect(Collectors.toList());

        return ClubScheduleResource.getClubScheduleListLink(responseList, clubId, email);
    }

    public EntityModel<ClubScheduleUpdateResponse> update(Long id, ClubScheduleUpdateRequest clubScheduleUpdateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ClubSchedule clubSchedule = clubScheduleRepository.findById(id).orElseThrow(ClubScheduleNotFoundException::new);
        if (!member.equals(clubSchedule.getMember())) {
            throw new PermissionException();
        }
        clubSchedule.update(clubScheduleUpdateRequest);
        ClubScheduleUpdateResponse response = modelMapper.map(clubSchedule, ClubScheduleUpdateResponse.class);
        return ClubScheduleResource.updateClubScheduleLink(response);
    }

    public EntityModel<ClubScheduleDeleteResponse> delete(Long id, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ClubSchedule clubSchedule = clubScheduleRepository.findById(id).orElseThrow(ClubScheduleNotFoundException::new);
        if (!member.equals(clubSchedule.getMember())) {
            throw new PermissionException();
        }
        clubScheduleRepository.deleteById(id);
        ClubScheduleDeleteResponse clubScheduleDeleteResponse = ClubScheduleDeleteResponse.builder()
                .message("클럽 스케줄을 삭제하였습니다.")
                .success(true)
                .build();

        return ClubScheduleResource.deleteClubScheduleLink(id, clubScheduleDeleteResponse);
    }
}
