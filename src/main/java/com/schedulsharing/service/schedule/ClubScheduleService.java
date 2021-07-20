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
import com.schedulsharing.web.schedule.club.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
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

    public ClubScheduleCreateResponse create(ClubScheduleCreateRequest createRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Club club = clubRepository.findById(createRequest.getClubId()).orElseThrow(ClubNotFoundException::new);

        ClubSchedule clubSchedule = ClubSchedule.createClubSchedule(createRequest, member, club);
        ClubSchedule savedClubSchedule = clubScheduleRepository.save(clubSchedule);

        return modelMapper.map(savedClubSchedule, ClubScheduleCreateResponse.class);
    }

    @Transactional(readOnly = true)
    public ClubScheduleResponse getClubSchedule(Long id) {
        ClubSchedule clubSchedule = clubScheduleRepository.findById(id).orElseThrow(ClubScheduleNotFoundException::new);

        return modelMapper.map(clubSchedule, ClubScheduleResponse.class);
    }

    @Transactional(readOnly = true)
    public List<ClubScheduleResponse> getClubScheduleList(Long clubId, YearMonth yearMonth) {
        List<ClubSchedule> clubSchedules = clubScheduleRepository.findAllByClubId(clubId, yearMonth);
        return clubSchedules.stream()
                .map(clubSchedule -> modelMapper.map(clubSchedule, ClubScheduleResponse.class))
                .collect(Collectors.toList());
    }

    public ClubScheduleUpdateResponse update(Long id, ClubScheduleUpdateRequest clubScheduleUpdateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ClubSchedule clubSchedule = clubScheduleRepository.findById(id).orElseThrow(ClubScheduleNotFoundException::new);
        if (!member.equals(clubSchedule.getMember())) {
            throw new PermissionException();
        }
        clubSchedule.update(clubScheduleUpdateRequest);

        return modelMapper.map(clubSchedule, ClubScheduleUpdateResponse.class);
    }

    public ClubScheduleDeleteResponse delete(Long id, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ClubSchedule clubSchedule = clubScheduleRepository.findById(id).orElseThrow(ClubScheduleNotFoundException::new);
        if (!member.equals(clubSchedule.getMember())) {
            throw new PermissionException();
        }
        clubScheduleRepository.deleteById(id);
        return ClubScheduleDeleteResponse.builder()
                .message("클럽 스케줄을 삭제하였습니다.")
                .success(true)
                .build();
    }
}
