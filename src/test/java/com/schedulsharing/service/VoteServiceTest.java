package com.schedulsharing.service;

import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.service.member.MemberService;
import com.schedulsharing.service.suggestion.ScheduleSuggestionService;
import com.schedulsharing.service.vote.VoteService;
import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateResponse;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionVoteRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionVoteResponse;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateRequest;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateResponse;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.vote.repository.VoteCheckRepository;
import com.schedulsharing.domain.schedule.repository.suggestion.ScheduleSuggestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class VoteServiceTest {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ScheduleSuggestionRepository scheduleSuggestionRepository;
    @Autowired
    private VoteCheckRepository voteCheckRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ClubService clubService;
    @Autowired
    private VoteService voteService;
    @Autowired
    private ScheduleSuggestionService scheduleSuggestionService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
        scheduleSuggestionRepository.deleteAll();
        voteCheckRepository.deleteAll();
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
    }

    @DisplayName("투표 수정 테스트")
    @Test
    public void 투표_수정_테스트() {
        Member member = memberRepository.findByEmail("test@example.com").get();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(2)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        SuggestionVoteResponse content = scheduleSuggestionService.vote(createResponse.getId(), suggestionVoteRequest, "test@example.com").getContent();


        SuggestionVoteUpdateRequest updateRequest = SuggestionVoteUpdateRequest.builder()
                .agree(false)
                .build();

        SuggestionVoteUpdateResponse updateResponse = voteService.updateVote(content.getId(), updateRequest, "test@example.com");
        assertFalse(updateResponse.isAgree());
    }

    private ClubCreateResponse createClub(Member savedMember, String clubName, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        return clubService.createClub(clubCreateRequest, savedMember.getEmail());
    }
}
