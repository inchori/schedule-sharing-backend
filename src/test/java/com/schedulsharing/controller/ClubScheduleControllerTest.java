package com.schedulsharing.controller;

import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleCreateRequest;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleCreateResponse;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleUpdateRequest;
import com.schedulsharing.web.member.dto.LoginRequestDto;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.schedule.repository.clubSchedule.ClubScheduleRepository;
import com.schedulsharing.service.schedule.ClubScheduleService;
import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.service.member.MemberService;
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

class ClubScheduleControllerTest extends ApiDocumentationTest{
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubService clubService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ClubScheduleRepository clubScheduleRepository;
    @Autowired
    private ClubScheduleService clubScheduleService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
        clubScheduleRepository.deleteAll();
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

        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("1234")
                .name("테스터2")
                .imagePath("imagePath2")
                .build();

        memberService.signup(signUpRequestDto2);
    }

    @DisplayName("클럽스케줄 생성하기")
    @Test
    public void 클럽스케줄_생성() throws Exception {
        String email = "test@example.com";
        String clubName = "동네친구";
        String categories = "밥";
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        ClubCreateResponse content = clubService.createClub(clubCreateRequest, email);

        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                .name("클럽 스케줄 생성 테스트")
                .contents("스터디 모임")
                .startMeetingDate(LocalDateTime.now())
                .endMeetingDate(LocalDateTime.now())
                .clubId(content.getClubId())
                .build();

        mvc.perform(post("/api/clubSchedule")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("클럽 스케줄 생성 테스트"))
                .andExpect(jsonPath("contents").value("스터디 모임"))
                .andExpect(jsonPath("startMeetingDate").exists())
                .andExpect(jsonPath("endMeetingDate").exists())
                .andDo(document("clubSchedule-create",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("clubSchedule-getOne").description("link to getOne"),
                                linkWithRel("clubSchedule-update").description("link to update"),
                                linkWithRel("clubSchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("생성할 클럽스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("생성할 클럽스케줄의 내용"),
                                fieldWithPath("startMeetingDate").description("생성할 클럽스케줄의 시작 날짜"),
                                fieldWithPath("endMeetingDate").description("생성할 클럽스케줄의 끝나는 날짜"),
                                fieldWithPath("clubId").description("생성할 클럽스케줄이 포함되어 있는 클럽의 고유 아이디")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("생성된 클럽스케줄의 고유아이디"),
                                fieldWithPath("name").description("생성된 클럽스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("생성된 클럽스케줄의 내용"),
                                fieldWithPath("startMeetingDate").description("생성된 클럽스케줄의 시작 날짜"),
                                fieldWithPath("endMeetingDate").description("생성된 클럽스케줄의 끝나는 날짜"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.clubSchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_links.clubSchedule-update.href").description("link to update"),
                                fieldWithPath("_links.clubSchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("잘못된 요청데이터로 클럽스케줄 생성하면 badRequest가 나와야한다")
    @Test
    public void 잘못된데이터로_클럽스케줄_생성() throws Exception {
        String email = "test@example.com";
        String clubName = "동네친구";
        String categories = "밥";
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        clubService.createClub(clubCreateRequest, email);

        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder().build();

        mvc.perform(post("/api/clubSchedule")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("클럽 스케줄 단건 조회 스케줄을 작성한 사람일 경우")
    @Test
    public void 클럽스케줄_단건조회_작성자() throws Exception {
        ClubScheduleCreateResponse clubSchedule = createClubScheduleByTest();

        mvc.perform(RestDocumentationRequestBuilders.get("/api/clubSchedule/{id}", clubSchedule.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("startMeetingDate").exists())
                .andExpect(jsonPath("endMeetingDate").exists())
                .andExpect(jsonPath("memberEmail").exists())
                .andExpect(jsonPath("memberName").exists())
                .andDo(document("clubSchedule-getOne",
                        pathParameters(
                                parameterWithName("id").description("조회할 클럽스케줄의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("clubSchedule-create").description("link to create"),
                                linkWithRel("clubSchedule-update").description("link to update 작성자에 경우에만 보입니다."),
                                linkWithRel("clubSchedule-delete").description("link to delete 작성자에 경우에만 보입니다."),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("조회한 클럽스케줄의 고유아이디"),
                                fieldWithPath("name").description("조회한 클럽스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("조회한 클럽스케줄의 내용"),
                                fieldWithPath("startMeetingDate").description("조회한 클럽스케줄의 시작 날짜"),
                                fieldWithPath("endMeetingDate").description("조회한 클럽스케줄의 끝나는 날짜"),
                                fieldWithPath("memberName").description("조회한 클럽스케줄을 작성한 사람의 이름"),
                                fieldWithPath("memberEmail").description("조회한 클럽스케줄을 작성한 사람의 이메일"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.clubSchedule-create.href").description("link to create"),
                                fieldWithPath("_links.clubSchedule-update.href").description("link to update 작성자에 경우에만 보입니다."),
                                fieldWithPath("_links.clubSchedule-delete.href").description("link to delete 작성자에 경우에만 보입니다."),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("년,월에 해당하는 클럽스케줄 리스트 조회하기")
    @Test
    public void 클럽스케줄_리스트조회() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        for (int i = 0; i < 3; i++) {
            ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                    .name("2021-2 클럽 이름 테스트" + i)
                    .contents("2021-2 클럽 내용 테스트" + i)
                    .startMeetingDate(LocalDateTime.of(2021, 2, 15, 0, 0).plusDays(i))
                    .endMeetingDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();
            clubScheduleService.create(createRequest, member.getEmail()).getContent();
        }
        for (int i = 0; i < 5; i++) {
            ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                    .name("2021-3 클럽 이름 테스트" + i)
                    .contents("2021-3 클럽 내용 테스트" + i)
                    .startMeetingDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .endMeetingDate(LocalDateTime.of(2021, 3, 2, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();
            clubScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 3; i++) {
            ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                    .name("2021-4 클럽 이름 테스트" + i)
                    .contents("2021-4 클럽 내용 테스트" + i)
                    .startMeetingDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .endMeetingDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();
            clubScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        mvc.perform(RestDocumentationRequestBuilders.get("/api/clubSchedule/list/{clubId}", clubCreateResponse.getClubId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .param("yearMonth", String.valueOf(YearMonth.of(2021,3))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.clubScheduleList[0].id").exists())
                .andExpect(jsonPath("_embedded.clubScheduleList[0].name").exists())
                .andExpect(jsonPath("_embedded.clubScheduleList[0].contents").exists())
                .andExpect(jsonPath("_embedded.clubScheduleList[0].startMeetingDate").exists())
                .andExpect(jsonPath("_embedded.clubScheduleList[0].endMeetingDate").exists())
                .andExpect(jsonPath("_embedded.clubScheduleList[0].memberName").exists())
                .andExpect(jsonPath("_embedded.clubScheduleList[0].memberEmail").exists())
                .andDo(document("clubSchedule-list",
                        pathParameters(
                                parameterWithName("clubId").description("클럽스케줄을 가져올 클럽 고유 아이디")
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
                                fieldWithPath("_embedded.clubScheduleList[0].id").description("조회한 클럽스케줄리스트중 첫번째 클럽스케줄의 고유아이디"),
                                fieldWithPath("_embedded.clubScheduleList[0].name").description("조회한 클럽스케줄리스트중 첫번째 클럽스케줄의 이름"),
                                fieldWithPath("_embedded.clubScheduleList[0].contents").description("조회한 클럽스케줄리스트중 첫번째 클럽스케줄의 내용"),
                                fieldWithPath("_embedded.clubScheduleList[0].startMeetingDate").description("조회한 클럽스케줄리스트중 첫번째 클럽스케줄의 시작날짜"),
                                fieldWithPath("_embedded.clubScheduleList[0].endMeetingDate").description("조회한 클럽스케줄리스트중 첫번째 클럽스케줄의 시작날짜"),
                                fieldWithPath("_embedded.clubScheduleList[0].memberName").description("조회한 클럽스케줄리스트중 첫번째 클럽스케줄을 작성한 멤버의 이름"),
                                fieldWithPath("_embedded.clubScheduleList[0].memberEmail").description("조회한 클럽스케줄리스트중 첫번째 클럽스케줄을 작성한 멤버의 이메일"),
                                fieldWithPath("_embedded.clubScheduleList[0]._links.clubSchedule-create.href").description("link to create"),
                                fieldWithPath("_embedded.clubScheduleList[0]._links.clubSchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_embedded.clubScheduleList[0]._links.clubSchedule-update.href").description("link to update 작성자에 경우에만 보입니다."),
                                fieldWithPath("_embedded.clubScheduleList[0]._links.clubSchedule-delete.href").description("link to delete 작성자에 경우에만 보입니다."),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("클럽 스케줄 수정하기")
    @Test
    public void 클럽스케줄_수정() throws Exception {
        ClubScheduleCreateResponse clubSchedule = createClubScheduleByTest();
        ClubScheduleUpdateRequest clubScheduleUpdateRequest = ClubScheduleUpdateRequest.builder()
                .name("수정된 클럽 스케줄 이름")
                .contents("수정된 클럽 스케줄 내용")
                .startMeetingDate(LocalDateTime.now().plusDays(1))
                .endMeetingDate(LocalDateTime.now().plusDays(1))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/clubSchedule/{id}", clubSchedule.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubScheduleUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("startMeetingDate").exists())
                .andExpect(jsonPath("endMeetingDate").exists())
                .andDo(document("clubSchedule-update",
                        pathParameters(
                                parameterWithName("id").description("수정할 클럽스케줄의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("clubSchedule-create").description("link to create"),
                                linkWithRel("clubSchedule-getOne").description("link to getOne"),
                                linkWithRel("clubSchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 클럽스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("수정할 클럽스케줄의 내용"),
                                fieldWithPath("startMeetingDate").description("수정할 클럽스케줄의 시작 날짜"),
                                fieldWithPath("endMeetingDate").description("수정할 클럽스케줄의 끝나는 날짜")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수정한 클럽스케줄의 고유아이디"),
                                fieldWithPath("name").description("수정한 클럽스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("수정한 클럽스케줄의 내용"),
                                fieldWithPath("startMeetingDate").description("수정한 클럽스케줄의 시작 날짜"),
                                fieldWithPath("endMeetingDate").description("수정한 클럽스케줄의 끝나는 날짜"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.clubSchedule-create.href").description("link to create"),
                                fieldWithPath("_links.clubSchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_links.clubSchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("다른사람의 클럽 스케줄 수정할 경우 오류")
    @Test
    public void 클럽스케줄_수정_작성자가_아닌_경우_실패() throws Exception {
        ClubScheduleCreateResponse clubSchedule = createClubScheduleByTest2(); //test2@example.com
        ClubScheduleUpdateRequest clubScheduleUpdateRequest = ClubScheduleUpdateRequest.builder()
                .name("수정된 클럽 스케줄 이름")
                .contents("수정된 클럽 스케줄 내용")
                .startMeetingDate(LocalDateTime.now().plusDays(1))
                .endMeetingDate(LocalDateTime.now().plusDays(1))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/clubSchedule/{id}", clubSchedule.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubScheduleUpdateRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @DisplayName("자신의 클럽 스케줄 삭제하기")
    @Test
    public void 클럽스케줄_삭제_성공() throws Exception {
        ClubScheduleCreateResponse clubSchedule = createClubScheduleByTest();
        mvc.perform(RestDocumentationRequestBuilders.delete("/api/clubSchedule/{id}", clubSchedule.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").exists())
                .andDo(document("clubSchedule-delete",
                        pathParameters(
                                parameterWithName("id").description("삭제할 클럽스케줄의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("clubSchedule-create").description("link to create"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("success").description("삭제를 성공했는 지"),
                                fieldWithPath("message").description("삭제 성공 message"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.clubSchedule-create.href").description("link to create"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("다른사람의 클럽 스케줄 삭제할 경우 오류")
    @Test
    public void 클럽스케줄_삭제_작성자가_아닌_경우_오류() throws Exception {
        ClubScheduleCreateResponse clubSchedule = createClubScheduleByTest2();
        mvc.perform(RestDocumentationRequestBuilders.delete("/api/clubSchedule/{id}", clubSchedule.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isForbidden());
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

    private ClubCreateResponse createClub(Member savedMember, String clubName, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        return clubService.createClub(clubCreateRequest, savedMember.getEmail());
    }

    private ClubScheduleCreateResponse createClubScheduleByTest() {
        String email = "test@example.com";
        String clubName = "동네친구";
        String categories = "밥";
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        ClubCreateResponse content = clubService.createClub(clubCreateRequest, email);

        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                .name("클럽 스케줄 생성 테스트")
                .contents("스터디 모임")
                .startMeetingDate(LocalDateTime.now())
                .endMeetingDate(LocalDateTime.now())
                .clubId(content.getClubId())
                .build();
        return clubScheduleService.create(createRequest, email).getContent();
    }

    private ClubScheduleCreateResponse createClubScheduleByTest2() {
        String email = "test2@example.com";
        String clubName = "동네친구";
        String categories = "밥";
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        ClubCreateResponse content = clubService.createClub(clubCreateRequest, email);

        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                .name("클럽 스케줄 생성 테스트2")
                .contents("스터디 모임2")
                .startMeetingDate(LocalDateTime.now())
                .endMeetingDate(LocalDateTime.now())
                .clubId(content.getClubId())
                .build();
        return clubScheduleService.create(createRequest, email).getContent();
    }
}