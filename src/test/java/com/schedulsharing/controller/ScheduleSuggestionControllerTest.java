package com.schedulsharing.controller;

import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.web.member.dto.LoginRequestDto;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateResponse;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionUpdateRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionVoteRequest;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.schedule.ScheduleSuggestion;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ScheduleSuggestionControllerTest extends ApiDocumentationTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubService clubService;
    @Autowired
    private ScheduleSuggestionRepository scheduleSuggestionRepository;
    @Autowired
    private ScheduleSuggestionService scheduleSuggestionService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
        scheduleSuggestionRepository.deleteAll();
        String email = "test@example.com";
        String password = "1234";
        String imagePath = "imagePath";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password(password)
                .name("테스터")
                .imagePath(imagePath)
                .build();

        memberService.signup(signUpRequestDto).getContent();
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("1234")
                .name("테스터2")
                .imagePath("imagePath2")
                .build();

        memberService.signup(signUpRequestDto2);
    }

    @DisplayName("스케줄 제안 생성 성공")
    @Test
    public void 스케줄제안_생성_성공() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        int minMember = 2;
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(minMember)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();

        mvc.perform(post("/api/suggestion")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suggestionCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("location").exists())
                .andExpect(jsonPath("minMember").value(2))
                .andExpect(jsonPath("scheduleStartDate").exists())
                .andExpect(jsonPath("scheduleEndDate").exists())
                .andExpect(jsonPath("voteStartDate").exists())
                .andExpect(jsonPath("voteStartDate").exists())
                .andDo(document("suggestion-create",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("suggestion-getOne").description("link to suggestion-getOne"),
                                linkWithRel("suggestion-update").description("link to suggestion-update"),
                                linkWithRel("suggestion-delete").description("link to suggestion-delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("생성할 제안의 제목"),
                                fieldWithPath("contents").description("생성할 제안의 내용"),
                                fieldWithPath("location").description("생성할 제안의 위치"),
                                fieldWithPath("minMember").description("생성할 제안의 최소인원"),
                                fieldWithPath("scheduleStartDate").description("생성할 제안의 스케줄 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("생성할 제안의 스케줄 끝나는 날짜"),
                                fieldWithPath("voteStartDate").description("생성할 제안의 투표 시작 날짜"),
                                fieldWithPath("voteEndDate").description("생성할 제안의 투표 끝나는 날짜"),
                                fieldWithPath("clubId").description("생성할 제안이 속해있는 클럽의 고유 아이디")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("생성된 클럽스케줄제안의 고유아이디"),
                                fieldWithPath("title").description("생성된 클럽스케줄제안의 제목"),
                                fieldWithPath("contents").description("생성된 클럽스케줄제안의 내용"),
                                fieldWithPath("location").description("생성된 클럽스케줄제안의 위치"),
                                fieldWithPath("minMember").description("생성된 클럽스케줄제안의 최소인원"),
                                fieldWithPath("scheduleStartDate").description("생성된 클럽스케줄제안의 스케줄 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("생성된 클럽스케줄제안의 스케줄 끝나는 날짜"),
                                fieldWithPath("voteStartDate").description("생성된 클럽스케줄제안의 투표 시작 날짜"),
                                fieldWithPath("voteEndDate").description("생성된 클럽스케줄제안의 투표 끝나는 날짜"),
                                fieldWithPath("_links.suggestion-getOne.href").description("link to suggestion-getOne"),
                                fieldWithPath("_links.suggestion-update.href").description("link to suggestion-update"),
                                fieldWithPath("_links.suggestion-delete.href").description("link to suggestion-delete"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("작성자인 경우 클럽스케줄제안 단건조회")
    @Test
    public void 클럽스케줄제안_단건조회_작성자() throws Exception {
        SuggestionCreateResponse createResponse = createSuggestion("test@example.com");
        mvc.perform(RestDocumentationRequestBuilders.get("/api/suggestion/{id}", createResponse.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("location").exists())
                .andExpect(jsonPath("minMember").exists())
                .andExpect(jsonPath("scheduleStartDate").exists())
                .andExpect(jsonPath("scheduleEndDate").exists())
                .andExpect(jsonPath("voteStartDate").exists())
                .andExpect(jsonPath("voteStartDate").exists())
                .andExpect(jsonPath("memberName").exists())
                .andExpect(jsonPath("memberEmail").exists())
                .andDo(document("suggestion-getOne",
                        pathParameters(
                                parameterWithName("id").description("조회할 클럽스케줄제안의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("suggestion-create").description("link to suggestion-create"),
                                linkWithRel("suggestion-update").description("link to suggestion-update 작성자의 경우에만 보입니다."),
                                linkWithRel("suggestion-delete").description("link to suggestion-delete 작성자의 경우에만 보입니다."),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("조회한 클럽스케줄제안의 고유아이디"),
                                fieldWithPath("title").description("조회한 클럽스케줄제안의 제목"),
                                fieldWithPath("contents").description("조회한 클럽스케줄제안의 내용"),
                                fieldWithPath("location").description("조회한 클럽스케줄제안의 위치"),
                                fieldWithPath("minMember").description("조회한 클럽스케줄제안의 최소인원"),
                                fieldWithPath("confirm").description("조회한 클럽스케줄제안의 confirm 여부"),
                                fieldWithPath("scheduleStartDate").description("조회한 클럽스케줄제안의 스케줄 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("조회한 클럽스케줄제안의 스케줄 끝나는 날짜"),
                                fieldWithPath("voteStartDate").description("조회한 클럽스케줄제안의 투표 시작 날짜"),
                                fieldWithPath("voteEndDate").description("조회한 클럽스케줄제안의 투표 끝나는 날짜"),
                                fieldWithPath("memberName").description("조회한 클럽스케줄제안을 작성한 멤버 이름"),
                                fieldWithPath("memberEmail").description("조회한 클럽스케줄제안을 작성한 멤버 이메일"),
                                fieldWithPath("agree.count").description("조회한 클럽스케줄제안의 찬성 수"),
                                fieldWithPath("agree.memberName").description("조회한 클럽스케줄제안의 반대한 사람 이름 리스트"),
                                fieldWithPath("disagree.count").description("조회한 클럽스케줄제안의 반대 수"),
                                fieldWithPath("disagree.memberName").description("조회한 클럽스케줄제안의 반대한 사람 이름 리스트"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.suggestion-create.href").description("link to suggestion-create"),
                                fieldWithPath("_links.suggestion-update.href").description("link to suggestion-update"),
                                fieldWithPath("_links.suggestion-delete.href").description("link to suggestion-delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("년,월에 해당하는 클럽스케줄제안 confirm된 리스트 조회하기")
    @Test
    public void 클럽스케줄제안_리스트조회_confirm_true() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        for (int i = 0; i < 3; i++) {
            //2021-2월 시작 2021-3월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-2~2021-3 테스트 제안 제목" + i)
                    .contents("2021-2~2021-3 테스트 제안 내용" + i)
                    .location("2021-2~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 2, 28, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 2, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 2, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 2, 8, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }
        for (int i = 0; i < 5; i++) {
            //2021-3월 시작 2021-3월 끝 5개 이 5개만 confirm
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-3~2021-3 테스트 제안 제목" + i)
                    .contents("2021-3~2021-3 테스트 제안 내용" + i)
                    .location("2021-3~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 27, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 28, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 3, 20, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 3, 24, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            SuggestionCreateResponse response = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
            ScheduleSuggestion scheduleSuggestion = scheduleSuggestionRepository.findById(response.getId()).get();
            scheduleSuggestion.updateConfirmTrue();
            scheduleSuggestionRepository.save(scheduleSuggestion);
        }

        for (int i = 0; i < 3; i++) {
            //2021-4월 시작 2021-4월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-4~2021-4 테스트 제안 제목" + i)
                    .contents("2021-4~2021-4 테스트 제안 내용" + i)
                    .location("2021-4~2021-4 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 4, 21, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 4, 23, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 4, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 4, 8, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }

        mvc.perform(RestDocumentationRequestBuilders.get("/api/suggestion/confirmList/{clubId}", clubCreateResponse.getClubId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .param("yearMonth", String.valueOf(YearMonth.of(2021, 3))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.suggestionList[0].id").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].title").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].contents").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].location").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].minMember").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].scheduleStartDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].scheduleEndDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].voteStartDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].voteEndDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].memberName").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].memberEmail").exists())
                .andDo(document("suggestion-confirmList",
                        pathParameters(
                                parameterWithName("clubId").description("클럽스케줄제안을 가져올 클럽 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestParameters(
                                parameterWithName("yearMonth").description("클럽스케줄리스트를 조회할 year,month")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.suggestionList[0].id").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 고유아이디"),
                                fieldWithPath("_embedded.suggestionList[0].title").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 이름"),
                                fieldWithPath("_embedded.suggestionList[0].contents").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 내용"),
                                fieldWithPath("_embedded.suggestionList[0].location").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 위치"),
                                fieldWithPath("_embedded.suggestionList[0].minMember").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 최소인원"),
                                fieldWithPath("_embedded.suggestionList[0].confirm").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안이 confirm 되었는지 여부"),
                                fieldWithPath("_embedded.suggestionList[0].scheduleStartDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 스케줄 시작날짜"),
                                fieldWithPath("_embedded.suggestionList[0].scheduleEndDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 스케줄 끝나는 날짜"),
                                fieldWithPath("_embedded.suggestionList[0].voteStartDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 투표 시작날짜"),
                                fieldWithPath("_embedded.suggestionList[0].voteEndDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 투표 끝나는 날짜"),
                                fieldWithPath("_embedded.suggestionList[0].memberName").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 작성한 멤버의 이름"),
                                fieldWithPath("_embedded.suggestionList[0].memberEmail").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 작성한 멤버의 이메일"),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-create.href").description("link to create"),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-getOne.href").description("link to getOne"),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-update.href").description("link to update 작성자에 경우에만 보입니다."),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-delete.href").description("link to delete 작성자에 경우에만 보입니다."),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("년,월에 해당하는 클럽스케줄제안 투표시간이 현재시간보다 이후인 리스트 조회하기")
    @Test
    public void 클럽스케줄제안_리스트조회_투표시간이_현재시간보다_이후인_제안() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");

        for (int i = 0; i < 2; i++) {
            //2021-3월 시작 2021-3월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-3~2021-3 테스트 제안 제목" + i)
                    .contents("2021-3~2021-3 테스트 제안 내용" + i)
                    .location("2021-3~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 27, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 28, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 3, 4, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            SuggestionCreateResponse response = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
            ScheduleSuggestion scheduleSuggestion = scheduleSuggestionRepository.findById(response.getId()).get();
            scheduleSuggestionRepository.save(scheduleSuggestion);
        }

        for (int i = 0; i < 5; i++) {
            //2021-3월 시작 2021-3월 끝 5개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-3~2021-3 테스트 제안 제목" + i)
                    .contents("2021-3~2021-3 테스트 제안 내용" + i)
                    .location("2021-3~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 27, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 28, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 3, 20, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 3, 24, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            SuggestionCreateResponse response = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
            ScheduleSuggestion scheduleSuggestion = scheduleSuggestionRepository.findById(response.getId()).get();
            scheduleSuggestionRepository.save(scheduleSuggestion);
        }

        for (int i = 0; i < 3; i++) {
            //2021-4월 시작 2021-4월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-4~2021-4 테스트 제안 제목" + i)
                    .contents("2021-4~2021-4 테스트 제안 내용" + i)
                    .location("2021-4~2021-4 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 4, 21, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 4, 23, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 4, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 4, 8, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }

        mvc.perform(RestDocumentationRequestBuilders.get("/api/suggestion/list/{clubId}", clubCreateResponse.getClubId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .param("now", String.valueOf(LocalDate.of(2021, 3, 8))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.suggestionList[0].id").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].title").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].contents").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].location").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].minMember").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].scheduleStartDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].scheduleEndDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].voteStartDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].voteEndDate").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].memberName").exists())
                .andExpect(jsonPath("_embedded.suggestionList[0].memberEmail").exists())
                .andDo(document("suggestion-list",
                        pathParameters(
                                parameterWithName("clubId").description("클럽스케줄제안을 가져올 클럽 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestParameters(
                                parameterWithName("now").description("오늘 날짜")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.suggestionList[0].id").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 고유아이디"),
                                fieldWithPath("_embedded.suggestionList[0].title").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 이름"),
                                fieldWithPath("_embedded.suggestionList[0].contents").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 내용"),
                                fieldWithPath("_embedded.suggestionList[0].location").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 위치"),
                                fieldWithPath("_embedded.suggestionList[0].minMember").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 최소인원"),
                                fieldWithPath("_embedded.suggestionList[0].confirm").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안이 confirm 되었는지 여부"),
                                fieldWithPath("_embedded.suggestionList[0].scheduleStartDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 스케줄 시작날짜"),
                                fieldWithPath("_embedded.suggestionList[0].scheduleEndDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 스케줄 끝나는 날짜"),
                                fieldWithPath("_embedded.suggestionList[0].voteStartDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 투표 시작날짜"),
                                fieldWithPath("_embedded.suggestionList[0].voteEndDate").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 투표 끝나는 날짜"),
                                fieldWithPath("_embedded.suggestionList[0].memberName").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 작성한 멤버의 이름"),
                                fieldWithPath("_embedded.suggestionList[0].memberEmail").description("조회한 클럽스케줄리스트제안중 첫번째 클럽스케줄제안을 작성한 멤버의 이메일"),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-create.href").description("link to create"),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-getOne.href").description("link to getOne"),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-update.href").description("link to update 작성자에 경우에만 보입니다."),
                                fieldWithPath("_embedded.suggestionList[0]._links.suggestion-delete.href").description("link to delete 작성자에 경우에만 보입니다."),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("클럽스케줄제안 수정성공")
    @Test
    public void 클럽스케줄제안_수정성공() throws Exception {
        SuggestionCreateResponse createResponse = createSuggestion("test@example.com");
        String updateTitle = "수정된 테스트 제안 제목";
        String updateContents = "수정된 테스트 제안 내용";
        String updateLocation = "수정된 제안 위치";
        int updateMinMember = 5;

        SuggestionUpdateRequest suggestionUpdateRequest = SuggestionUpdateRequest.builder()
                .title(updateTitle)
                .contents(updateContents)
                .location(updateLocation)
                .minMember(updateMinMember)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/suggestion/{id}", createResponse.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suggestionUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("location").exists())
                .andExpect(jsonPath("minMember").exists())
                .andExpect(jsonPath("scheduleStartDate").exists())
                .andExpect(jsonPath("scheduleEndDate").exists())
                .andExpect(jsonPath("voteStartDate").exists())
                .andExpect(jsonPath("voteEndDate").exists())
                .andDo(document("suggestion-update",
                        pathParameters(
                                parameterWithName("id").description("수정할 클럽스케줄제안의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("suggestion-create").description("link to suggestion-create"),
                                linkWithRel("suggestion-getOne").description("link to suggestion-getOne"),
                                linkWithRel("suggestion-delete").description("link to suggestion-delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("클럽스케줄제안의 제목 수정"),
                                fieldWithPath("contents").description("클럽스케줄제안의 내용 수정"),
                                fieldWithPath("location").description("클럽스케줄제안의 위치 수정"),
                                fieldWithPath("minMember").description("클럽스케줄제안의 최소인원 수정"),
                                fieldWithPath("scheduleStartDate").description("클럽스케줄제안의 스케줄 시작날짜 수정"),
                                fieldWithPath("scheduleEndDate").description("클럽스케줄제안의 스케줄 끝나는 날짜 수정"),
                                fieldWithPath("voteStartDate").description("클럽스케줄제안의 투표 시작날짜 수정"),
                                fieldWithPath("voteEndDate").description("클럽스케줄제안의 투표 끝나는 날짜 수정")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수정된 클럽스케줄제안의 고유아이디"),
                                fieldWithPath("title").description("수정된 클럽스케줄제안의 제목"),
                                fieldWithPath("contents").description("수정된 클럽스케줄제안의 내용"),
                                fieldWithPath("location").description("수정된 클럽스케줄제안의 위치"),
                                fieldWithPath("minMember").description("수정된 클럽스케줄제안의 최소인원"),
                                fieldWithPath("confirm").description("수정된 클럽스케줄제안의 confirm 여부"),
                                fieldWithPath("scheduleStartDate").description("수정된 클럽스케줄제안의 스케줄 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("수정된 클럽스케줄제안의 스케줄 끝나는 날짜"),
                                fieldWithPath("voteStartDate").description("수정된 클럽스케줄제안의 투표 시작 날짜"),
                                fieldWithPath("voteEndDate").description("수정된 클럽스케줄제안의 투표 끝나는 날짜"),
                                fieldWithPath("memberName").description("수정된 클럽스케줄제안을 작성한 멤버 이름"),
                                fieldWithPath("memberEmail").description("수정된 클럽스케줄제안을 작성한 멤버 이메일"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.suggestion-create.href").description("link to suggestion-create"),
                                fieldWithPath("_links.suggestion-getOne.href").description("link to suggestion-getOne"),
                                fieldWithPath("_links.suggestion-delete.href").description("link to suggestion-delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("작성자가 아닌 다른사람인 경우 클럽스케줄제안 수정실패")
    @Test
    public void 클럽스케줄제안_수정실패_권한없음() throws Exception {
        SuggestionCreateResponse createResponse = createSuggestion("test2@example.com");
        String updateTitle = "수정된 테스트 제안 제목";
        String updateContents = "수정된 테스트 제안 내용";
        String updateLocation = "수정된 제안 위치";
        int updateMinMember = 5;

        SuggestionUpdateRequest suggestionUpdateRequest = SuggestionUpdateRequest.builder()
                .title(updateTitle)
                .contents(updateContents)
                .location(updateLocation)
                .minMember(updateMinMember)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/suggestion/{id}", createResponse.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suggestionUpdateRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @DisplayName("클럽스케줄제안 삭제성공")
    @Test
    public void 클럽스케줄제안_삭제성공() throws Exception {
        SuggestionCreateResponse createResponse = createSuggestion("test@example.com");

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/suggestion/{id}", createResponse.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("message").exists())
                .andDo(document("suggestion-delete",
                        pathParameters(
                                parameterWithName("id").description("삭제할 클럽스케줄제안의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("suggestion-create").description("link to suggestion-create"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("success").description("클럽스케줄제안 삭제 성공 여부"),
                                fieldWithPath("message").description("클럽스케줄제안 삭제 성공 메시지"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.suggestion-create.href").description("link to suggestion-create"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("작성자가 아닌 다른사람인 경우 클럽스케줄제안 삭제실패")
    @Test
    public void 클럽스케줄제안_삭제실패_권한없음() throws Exception {
        SuggestionCreateResponse createResponse = createSuggestion("test2@example.com");

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/suggestion/{id}", createResponse.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @DisplayName("클럽스케줄제안 투표하기 성공")
    @Test
    public void 클럽스케줄제안_투표_성공() throws Exception {
        SuggestionCreateResponse suggestion = createSuggestion("test@example.com");
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        mvc.perform(RestDocumentationRequestBuilders.post("/api/suggestion/{id}/vote", suggestion.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suggestionVoteRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("agree").exists())
                .andDo(document("suggestion-vote",
                        pathParameters(
                                parameterWithName("id").description("투표가 진행되는 클럽스케줄제안 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("agree").description("투표의 찬성(true)/반대(false)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("생성된 투표체크의 고유아이디"),
                                fieldWithPath("agree").description("생성된 투표체크의 찬성 or 반대"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("클럽원이 아닌 경우 클럽스케줄제안 투표하기 실패")
    @Test
    public void 클럽원이_아닌_경우_클럽스케줄제안_투표_실패() throws Exception {
        SuggestionCreateResponse suggestion = createSuggestion("test2@example.com");
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        mvc.perform(RestDocumentationRequestBuilders.post("/api/suggestion/{id}/vote", suggestion.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suggestionVoteRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("message").exists())
                .andDo(document("suggestion-vote-fail-notClubMember",
                        pathParameters(
                                parameterWithName("id").description("투표가 진행되는 클럽스케줄제안 고유 아이디")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("agree").description("투표의 찬성(true)/반대(false)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("httpStatus").description("httpStatus"),
                                fieldWithPath("error").description("error code"),
                                fieldWithPath("message").description("message")
                        )
                ));
    }

    @DisplayName("클럽스케줄제안 중복투표 실패")
    @Test
    public void 클럽스케줄제안_중복투표_실패() throws Exception {
        SuggestionCreateResponse suggestion = createSuggestion("test@example.com");
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        scheduleSuggestionService.vote(suggestion.getId(), suggestionVoteRequest, "test@example.com");
        mvc.perform(RestDocumentationRequestBuilders.post("/api/suggestion/{id}/vote", suggestion.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suggestionVoteRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("message").exists())
                .andDo(document("suggestion-vote-fail-duplicateVoteCheck",
                        pathParameters(
                                parameterWithName("id").description("투표가 진행되는 클럽스케줄제안 고유 아이디")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("agree").description("투표의 찬성(true)/반대(false)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("httpStatus").description("httpStatus"),
                                fieldWithPath("error").description("error code"),
                                fieldWithPath("message").description("message")
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
}