package com.schedulsharing.service;

import com.schedulsharing.dto.ClubSchedule.*;
import com.schedulsharing.dto.resource.ClubScheduleResource;
import com.schedulsharing.entity.Club;
import com.schedulsharing.entity.member.Member;
import com.schedulsharing.entity.schedule.ClubSchedule;
import com.schedulsharing.excpetion.club.ClubNotFoundException;
import com.schedulsharing.excpetion.clubSchedule.ClubScheduleNotFoundException;
import com.schedulsharing.excpetion.common.InvalidGrantException;
import com.schedulsharing.repository.ClubRepository;
import com.schedulsharing.repository.ClubScheduleRepository;
import com.schedulsharing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        Member member = memberRepository.findByEmail(email).get();
        Club club = findById(createRequest.getClubId());

        ClubSchedule clubSchedule = ClubSchedule.createClubSchedule(createRequest, member, club);
        ClubSchedule savedClubSchedule = clubScheduleRepository.save(clubSchedule);

        ClubScheduleCreateResponse createResponse = modelMapper.map(savedClubSchedule, ClubScheduleCreateResponse.class);

        return ClubScheduleResource.createClubScheduleLink(createResponse);
    }

    @Transactional(readOnly = true)
    public EntityModel<ClubScheduleResponse> getClubSchedule(Long id, String email) {
        Member member = memberRepository.findByEmail(email).get();
        ClubSchedule clubSchedule = clubScheduleFindById(id);
        ClubScheduleResponse response = modelMapper.map(clubSchedule, ClubScheduleResponse.class);
        return ClubScheduleResource.getClubScheduleLink(response, member.getEmail());
    }

    public EntityModel<ClubScheduleUpdateResponse> update(Long id, ClubScheduleUpdateRequest clubScheduleUpdateRequest, String email) {
        Member member = memberRepository.findByEmail(email).get();
        ClubSchedule clubSchedule = clubScheduleFindById(id);
        if (!member.equals(clubSchedule.getMember())) {
            throw new InvalidGrantException("수정할 권한이 없습니다.");
        }
        clubSchedule.update(clubScheduleUpdateRequest);
        ClubScheduleUpdateResponse response = modelMapper.map(clubSchedule, ClubScheduleUpdateResponse.class);
        return ClubScheduleResource.updateClubScheduleLink(response);
    }

    private ClubSchedule clubScheduleFindById(Long id) {
        Optional<ClubSchedule> optionalClubSchedule = clubScheduleRepository.findById(id);
        if (optionalClubSchedule.isEmpty()) {
            throw new ClubScheduleNotFoundException("클럽 스케줄이 존재하지 않습니다.");
        }
        return optionalClubSchedule.get();
    }

    private Club findById(Long clubId) {
        Optional<Club> optionalClub = clubRepository.findById(clubId);
        if (optionalClub.isEmpty()) {
            throw new ClubNotFoundException("클럽이 존재하지 않습니다.");
        }
        return optionalClub.get();
    }
}
