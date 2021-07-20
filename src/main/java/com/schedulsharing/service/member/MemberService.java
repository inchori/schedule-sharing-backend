package com.schedulsharing.service.member;


import com.schedulsharing.domain.club.Club;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.excpetion.PermissionException;
import com.schedulsharing.service.member.exception.EmailExistedException;
import com.schedulsharing.service.member.exception.MemberNotFoundException;
import com.schedulsharing.web.member.dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public SignUpResponseDto signup(SignUpRequestDto signUpRequestDto) {
        if (memberRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
            throw new EmailExistedException();
        }

        Member memberEntity = signUpRequestDto.toEntity(passwordEncoder);
        Member savedMember = memberRepository.save(memberEntity);

        return modelMapper.map(savedMember, SignUpResponseDto.class);
    }

    @Transactional(readOnly = true)
    public List<GetClubsResponse> getClubs(String email) {
        List<Club> clubs = clubRepository.findByMemberEmail(email);
        List<GetClubsResponse> getClubsResponse = new ArrayList<>();
        for (Club club : clubs) {
            getClubsResponse.add(modelMapper.map(club, GetClubsResponse.class));
        }
        return getClubsResponse;
    }

    @Transactional(readOnly = true)
    public EmailCheckResponseDto emailCheck(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            return new EmailCheckResponseDto(true, "이메일이 중복되었습니다.");
        }
        return new EmailCheckResponseDto(false, "사용가능한 이메일입니다.");
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        return modelMapper.map(member, MemberResponse.class);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return modelMapper.map(member, MemberResponse.class);
    }

    public MemberUpdateResponse updateMember(Long id, MemberUpdateRequest memberUpdateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        if (!member.getId().equals(id)) {
            throw new PermissionException();
        }
        member.update(memberUpdateRequest, passwordEncoder);
        return modelMapper.map(member, MemberUpdateResponse.class);
    }

    public MemberDeleteResponse deleteMember(Long id, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        if (!member.getId().equals(id)) {
            throw new PermissionException();
        }
        memberRepository.deleteById(id);
        return MemberDeleteResponse.builder()
                .success(true)
                .message("성공적으로 탈퇴하셨습니다.")
                .build();
    }
}
