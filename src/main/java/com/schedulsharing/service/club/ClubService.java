package com.schedulsharing.service.club;

import com.schedulsharing.domain.club.Club;
import com.schedulsharing.domain.club.MemberClub;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.excpetion.PermissionException;
import com.schedulsharing.service.club.exception.ClubNotFoundException;
import com.schedulsharing.service.member.exception.MemberNotFoundException;
import com.schedulsharing.web.club.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClubService {
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public ClubCreateResponse createClub(ClubCreateRequest clubCreateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        MemberClub memberClub = MemberClub.createMemberClub(member);

        Club club = Club.createClub(clubCreateRequest.getClubName(), member.getId(), clubCreateRequest.getCategories(), memberClub);

        Club savedClub = clubRepository.save(club);

        return modelMapper.map(savedClub, ClubCreateResponse.class);
    }

    @Transactional(readOnly = true)
    public ClubGetResponse getClub(Long clubId, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Club club = clubRepository.findById(clubId).orElseThrow(ClubNotFoundException::new);

        return modelMapper.map(club, ClubGetResponse.class);
    }

    public ClubInviteResponse invite(ClubInviteRequest clubInviteRequest, Long clubId, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Club club = clubRepository.findById(clubId).orElseThrow(ClubNotFoundException::new);

        if (!member.getId().equals(club.getLeaderId())) {
            throw new PermissionException();
        }
        List<Long> memberIds = clubInviteRequest.getMemberIds();
        List<Member> members = new ArrayList<>();
        for (Long memberId : memberIds) {
            members.add(memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new));
        }
        List<MemberClub> memberClubs = MemberClub.inviteMemberClub(members);
        Club.inviteClub(club, memberClubs);

        return new ClubInviteResponse(true, "초대를 완료하였습니다.");
    }


    public ClubUpdateResponse update(Long clubId, ClubUpdateRequest clubUpdateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Club club = clubRepository.findById(clubId).orElseThrow(ClubNotFoundException::new);
        if (!member.getId().equals(club.getLeaderId())) {
            throw new PermissionException();
        }
        club.update(clubUpdateRequest.getClubName(), clubUpdateRequest.getCategories());

        return modelMapper.map(club, ClubUpdateResponse.class);
    }

    public ClubDeleteResponse delete(Long clubId, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Club club = clubRepository.findById(clubId).orElseThrow(ClubNotFoundException::new);
        if (!member.getId().equals(club.getLeaderId())) {
            throw new PermissionException();
        }
        clubRepository.deleteById(clubId);
        return new ClubDeleteResponse(true, "모임을 삭제하였습니다");
    }
}
