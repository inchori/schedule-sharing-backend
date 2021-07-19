package com.schedulsharing.controller;

import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.web.member.dto.LoginRequestDto;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateResponse;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionVoteRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionVoteResponse;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateRequest;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.vote.repository.VoteCheckRepository;
import com.schedulsharing.domain.schedule.repository.suggestion.ScheduleSuggestionRepository;
import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.service.member.MemberService;
import com.schedulsharing.service.suggestion.ScheduleSuggestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class VoteControllerTest extends ApiDocumentationTest{
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

        String email2 = "test2@example.com";
        String password2 = "1234";
        String imagePath2 = "imagePath2";
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email(email2)
                .password(password2)
                .name("테스터2")
                .imagePath(imagePath2)
                .build();

        memberService.signup(signUpRequestDto2);
    }

    @DisplayName("투표 수정 테스트")
    @Test
    public void 투표_수정_테스트() throws Exception {
        SuggestionVoteResponse voteResponse = getVote();
        SuggestionVoteUpdateRequest voteUpdateRequest = SuggestionVoteUpdateRequest.builder()
                .agree(false)
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/vote/{voteId}", voteResponse.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("agree").exists())
                .andDo(document("vote-update",
                        pathParameters(
                                parameterWithName("voteId").description("수정할 투표의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("suggestion-vote").description("link to suggestion-vote"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("agree").description("투표의 찬반여부")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수정된 투표의 고유아이디"),
                                fieldWithPath("agree").description("수정된 투표 찬반"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.suggestion-vote.href").description("link to suggestion-vote"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    private ClubCreateResponse createClub(Member savedMember, String clubName, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        return clubService.createClub(clubCreateRequest, savedMember.getEmail());
    }

    private String getBearToken() throws Exception {
        return "Bearer  " + getToken();
    }

    private String getToken() throws Exception {
        String email = "test@example.com";
        String password = "1234";

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        ResultActions perform = mvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));
        String responseBody = perform.andReturn().getResponse().getContentAsString();
        JacksonJsonParser parser = new JacksonJsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    private SuggestionCreateResponse createSuggestion(String email) {
        Member member = memberRepository.findByEmail(email).get(); //setUp에서 생성한 멤버
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
        return scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
    }

    private SuggestionVoteResponse getVote() {
        SuggestionCreateResponse suggestionCreateResponse = createSuggestion("test@example.com");
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        return scheduleSuggestionService.vote(suggestionCreateResponse.getId(), suggestionVoteRequest, "test@example.com").getContent();
    }
}
