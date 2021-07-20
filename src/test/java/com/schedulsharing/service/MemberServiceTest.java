package com.schedulsharing.service;

import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.service.member.MemberService;
import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.web.member.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ClubService clubService;
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
    }

    @DisplayName("이메일로 멤버 검색하기")
    @Test
    public void 이메일로_멤버_검색() {
        String email = "test@example.com";
        String password = "1234";
        String imagePath = "imagePath";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password(password)
                .name("테스터")
                .imagePath(imagePath)
                .build();
        memberService.signup(signUpRequestDto);
        String email2 = "test2@example.com";
        final String password2 = "123456";
        final String name2 = "테스터2";
        final String imagePath2 = "imagePath";
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email(email2)
                .password(password2)
                .name(name2)
                .imagePath(imagePath2)
                .build();
        memberService.signup(signUpRequestDto2);

        MemberResponse result = memberService.getMemberByEmail(email2);
        assertEquals(result.getEmail(), email2);
        assertEquals(result.getName(), name2);
        assertEquals(result.getImagePath(), imagePath2);
    }

    @DisplayName("로그인한 유저의 클럽 가져오기")
    @Test
    public void 로그인유저_클럽_조회() {
        String email = "test@example.com";
        String password = "1234";
        String imagePath = "imagePath";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password(password)
                .name("테스터")
                .imagePath(imagePath)
                .build();
        memberService.signup(signUpRequestDto);
        String email2 = "test2@example.com";
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email(email2)
                .password("1234")
                .name("테스터2")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto2);

        createClub(email, "동네친구", "밥");
        createClub(email, "스터디 모임", "스터디");
        createClub(email2, "테스트2모임", "테스트2모임카테고리");

        Collection<GetClubsResponse> result = memberService.getClubs(email);
        assertEquals(result.size(), 2);
    }

    @DisplayName("멤버 id로 단건 조회")
    @Test
    public void 멤버_id로_단건_조회() {
        String email = "test@example.com";
        String password = "1234";
        String imagePath = "imagePath";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .name("테스터")
                .password(password)
                .imagePath(imagePath)
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto);

        MemberResponse memberResponse = memberService.getMemberById(signUpResponseDto.getId());

        assertEquals(memberResponse.getId(), signUpResponseDto.getId());
        assertEquals(memberResponse.getEmail(), signUpResponseDto.getEmail());
        assertEquals(memberResponse.getName(), signUpResponseDto.getName());
        assertEquals(memberResponse.getImagePath(), signUpResponseDto.getImagePath());
    }

    @DisplayName("멤버 수정 성공 테스트")
    @Test
    public void 멤버_수정_성공_테스트() {
        String email = "test@example.com";
        String password = "1234";
        String imagePath = "imagePath";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .name("테스터")
                .password(password)
                .imagePath(imagePath)
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto);

        String updateName = "수정된 멤버 이름";
        String updatePassword = "2345";
        String updateImagePath = "수정된 사진 경로";
        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .name(updateName)
                .password(updatePassword)
                .imagePath(updateImagePath)
                .build();

        MemberUpdateResponse updateResponse = memberService.updateMember(signUpResponseDto.getId(), updateRequest, signUpResponseDto.getEmail());

        assertEquals(updateResponse.getName(), updateName);
        assertEquals(updateResponse.getImagePath(), updateImagePath);
    }

    @DisplayName("멤버 탈퇴 및 삭제 성공")
    @Test
    public void 멤버_삭제_성공() {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("test")
                .password("1234")
                .imagePath("imagePath")
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto);

        memberService.deleteMember(signUpResponseDto.getId(), signUpResponseDto.getEmail());

        assertEquals(memberRepository.findById(signUpResponseDto.getId()).isEmpty(), true);
    }

    private void createClub(String email, String name, String categories) {

        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(name)
                .categories(categories)
                .build();
        clubService.createClub(clubCreateRequest, email);
    }
}