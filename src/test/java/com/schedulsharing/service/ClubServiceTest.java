package com.schedulsharing.service;

import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.web.club.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ClubServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ClubService clubService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
    }

    @DisplayName("정상적으로 클럽 생성하기")
    @Test
    public void 클럽_정상적인_생성() {
        Member savedMember = createMember();
        String clubName = "testClubName";
        String categories = "밥";
        ClubCreateResponse result = createClub(savedMember, clubName, categories);
        assertEquals(result.getClubName(), clubName);
        assertEquals(result.getCategories(), categories);
        assertEquals(result.getLeaderId(), savedMember.getId());
    }


    @DisplayName("클럽 조회하기")
    @Test
    public void 클럽조회() {
        Member savedMember = createMember();
        String clubName = "testClubName";
        String categories = "밥";
        ClubCreateResponse clubCreateResponse = createClub(savedMember, clubName, categories);
        Long clubId = clubCreateResponse.getClubId();
        ClubResponse clubResponse = clubService.getClub(clubId, savedMember.getEmail()).getContent();
        assertEquals(clubResponse.getClubId(), clubId);
        assertEquals(clubResponse.getClubName(), clubName);
        assertEquals(clubResponse.getCategories(), categories);
        assertEquals(clubResponse.getLeaderId(), savedMember.getId());
    }

    @DisplayName("클럽 수정하기")
    @Test
    public void 클럽수정() {
        Member savedMember = createMember();
        String clubName = "testClubName";
        String categories = "밥";
        ClubCreateResponse clubCreateResponse = createClub(savedMember, clubName, categories);
        Long clubId = clubCreateResponse.getClubId();
        String updateClubName = "수정된 클럽이름";
        String updateClubCategories = "수정된 클럽카테고리";
        ClubUpdateRequest clubUpdateRequest = ClubUpdateRequest.builder()
                .clubName(updateClubName)
                .categories(updateClubCategories)
                .build();
        ClubUpdateResponse clubUpdateResponse = clubService.update(clubId, clubUpdateRequest, savedMember.getEmail()).getContent();

        assertEquals(clubUpdateResponse.getClubId(), clubId);
        assertEquals(clubUpdateResponse.getClubName(), updateClubName);
        assertEquals(clubUpdateResponse.getCategories(), updateClubCategories);
        assertEquals(clubUpdateResponse.getLeaderId(), savedMember.getId());
    }

    @DisplayName("클럽 삭제하기")
    @Test
    public void 클럽삭제() {
        Member savedMember = createMember();

        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName("testClubName")
                .categories("밥")
                .build();
        ClubCreateResponse result = clubService.createClub(clubCreateRequest, savedMember.getEmail()).getContent();

        clubService.delete(result.getClubId(), savedMember.getEmail());

        assertEquals(clubRepository.findById(result.getClubId()).isEmpty(), true);
    }

    private Member createMember() {
        Member member = Member.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        return memberRepository.save(member);
    }

    private ClubCreateResponse createClub(Member savedMember, String clubName, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        return clubService.createClub(clubCreateRequest, savedMember.getEmail()).getContent();
    }
}
