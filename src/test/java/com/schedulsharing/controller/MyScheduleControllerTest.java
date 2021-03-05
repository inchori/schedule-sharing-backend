package com.schedulsharing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedulsharing.config.RestDocsConfiguration;
import com.schedulsharing.dto.MySchedule.MyScheduleCreateRequest;
import com.schedulsharing.dto.MySchedule.MyScheduleCreateResponse;
import com.schedulsharing.dto.MySchedule.MyScheduleUpdateRequest;
import com.schedulsharing.dto.MySchedule.MyYearMonthRequest;
import com.schedulsharing.dto.member.LoginRequestDto;
import com.schedulsharing.dto.member.SignUpRequestDto;
import com.schedulsharing.entity.member.Member;
import com.schedulsharing.repository.MemberRepository;
import com.schedulsharing.repository.myschedule.MyScheduleRepository;
import com.schedulsharing.service.MemberService;
import com.schedulsharing.service.MyScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.YearMonth;

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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class MyScheduleControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MyScheduleRepository myScheduleRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MyScheduleService myScheduleService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        myScheduleRepository.deleteAll();
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
                .name("테스터")
                .imagePath("imagePath2")
                .build();

        memberService.signup(signUpRequestDto2);
    }

    @DisplayName("내 스케줄 생성")
    @Test
    public void 내_스케줄_생성() throws Exception {
        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name("나 스케줄 생성 테스트")
                .contents("스터디 모임")
                .scheduleStartDate(LocalDateTime.now())
                .scheduleEndDate(LocalDateTime.now())
                .build();

        mvc.perform(post("/api/myschedule")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("나 스케줄 생성 테스트"))
                .andExpect(jsonPath("contents").value("스터디 모임"))
                .andExpect(jsonPath("scheduleStartDate").exists())
                .andExpect(jsonPath("scheduleEndDate").exists())
                .andDo(document("mySchedule-create",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-getOne").description("link to getOne"),
                                linkWithRel("mySchedule-update").description("link to update"),
                                linkWithRel("mySchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("생성할 나의 스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("생성할 나의 스케줄의 내용"),
                                fieldWithPath("scheduleStartDate").description("생성할 나의 스케줄의 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("생성할 나의 스케줄의 끝나는 날짜")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("myScheduleId").description("생성된 나의 스케줄의 고유아이디"),
                                fieldWithPath("name").description("생성된 나의 스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("생성된 나의 스케줄의 내용"),
                                fieldWithPath("scheduleStartDate").description("생성된 나의 스케줄의 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("생성된 나의 스케줄의 끝나는 날짜"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.mySchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_links.mySchedule-update.href").description("link to update"),
                                fieldWithPath("_links.mySchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));


    }

    @DisplayName("내 스케줄 단건 조회")
    @Test
    public void 내_스케줄_단건_조회() throws Exception {
        MyScheduleCreateResponse mySchedule = createMyScheduleByTest();
        mvc.perform(RestDocumentationRequestBuilders.get("/api/myschedule/{id}", mySchedule.getMyScheduleId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("myScheduleId").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("startDate").exists())
                .andExpect(jsonPath("endDate").exists())
                .andDo(document("mySchedule-getOne",
                        pathParameters(
                                parameterWithName("id").description("내 스케줄의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-create").description("link to create"),
                                linkWithRel("mySchedule-update").description("link to update"),
                                linkWithRel("mySchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("myScheduleId").description("조회한 나의 스케줄의 고유아이디"),
                                fieldWithPath("name").description("조회한 나의 스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("조회한 나의 스케줄의 내용"),
                                fieldWithPath("startDate").description("조회한 나의 스케줄의 시작 날짜"),
                                fieldWithPath("endDate").description("조회한 나의 스케줄의 끝나는 날짜"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_links.mySchedule-update.href").description("link to update"),
                                fieldWithPath("_links.mySchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("년,월에 해당하는 나의 스케줄 리스트 조회하기")
    @Test
    public void 나의_스케줄_리스트조회() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        for (int i = 0; i < 3; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-2 내 스케줄 이름 테스트" + i)
                    .contents("2021-2 내 스케줄 내용 테스트" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 2, 15, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 5; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-3 내 스케줄 이름 테스트" + i)
                    .contents("2021-3 내 스케줄 내용 테스트" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 2, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 3; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-4 내 스케줄 이름 테스트" + i)
                    .contents("2021-4 내 스케줄 내용 테스트" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        MyYearMonthRequest myYearMonthRequest = MyYearMonthRequest.builder()
                .myYearMonth(YearMonth.of(2021, 3))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.get("/api/myschedule/list")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(myYearMonthRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].myScheduleId").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].name").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].contents").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].startDate").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].endDate").exists())
                .andDo(document("mySchedule-list",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("myYearMonth").description("나의 스케줄리스트를 조회할 year,month")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.myScheduleResponseList[0].myScheduleId").description("조회한 나의 스케줄리스트중 첫번째 스케줄의 고유아이디"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].name").description("조회한 나의 스케줄리스트중 첫번째 스케줄의 이름"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].contents").description("조회한 나의 스케줄리스트중 첫번째 스케줄의 내용"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].startDate").description("조회한 나의 스케줄리스트중 첫번째 스케줄의 시작날짜"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].endDate").description("조회한 나의 스케줄리스트중 첫번째 스케줄의 종날짜"),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-update.href").description("link to update 작성자에 경우에만 보입니다."),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-delete.href").description("link to delete 작성자에 경우에만 보입니다."),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("내 스케줄 수정 성공")
    @Test
    public void 내_스케줄_수정_성공() throws Exception {
        MyScheduleCreateResponse mySchedule = createMyScheduleByTest();
        MyScheduleUpdateRequest updateRequest = MyScheduleUpdateRequest.builder()
                .name("수정된 나의 스케줄 이름")
                .contents("수정된 나의 스케줄 내용")
                .scheduleStartDate(LocalDateTime.now().plusDays(1))
                .scheduleEndDate(LocalDateTime.now().plusDays(1))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/myschedule/{id}", mySchedule.getMyScheduleId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("myScheduleId").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("scheduleStartDate").exists())
                .andExpect(jsonPath("scheduleEndDate").exists())
                .andDo(document("mySchedule-update",
                        pathParameters(
                                parameterWithName("id").description("수정할 나의 스케줄의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-create").description("link to create"),
                                linkWithRel("mySchedule-getOne").description("link to getOne"),
                                linkWithRel("mySchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 나의 스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("수정할 나의 스케줄의 내용"),
                                fieldWithPath("scheduleStartDate").description("수정할 나의 스케줄의 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("수정할 나의 스케줄의 끝나는 날짜")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("myScheduleId").description("수정한 나의 스케줄의 고유아이디"),
                                fieldWithPath("name").description("수정한 나의 스케줄의 이름 또는 제목"),
                                fieldWithPath("contents").description("수정한 나의 스케줄의 내용"),
                                fieldWithPath("scheduleStartDate").description("수정한 나의 스케줄의 시작 날짜"),
                                fieldWithPath("scheduleEndDate").description("수정한 나의 스케줄의 끝나는 날짜"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_links.mySchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_links.mySchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));

    }

    @DisplayName("나의 스케줄 삭제 성공")
    @Test
    public void 나의_스케줄_삭제_성공() throws Exception {
        MyScheduleCreateResponse createResponse = createMyScheduleByTest();
        mvc.perform(RestDocumentationRequestBuilders.delete("/api/myschedule/{id}", createResponse.getMyScheduleId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").exists())
                .andDo(document("mySchedule-delete",
                        pathParameters(
                                parameterWithName("id").description("삭제할 나의 스케줄의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-create").description("link to create"),
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
                                fieldWithPath("_links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));

    }

    private MyScheduleCreateResponse createMyScheduleByTest() {
        String email = "test@example.com";
        String name = "나의 스케줄 생성 테스트";
        String contents = "스터디 모임";
        LocalDateTime scheduleStartDate = LocalDateTime.now();
        LocalDateTime scheduleEndDate = LocalDateTime.now();

        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name(name)
                .contents(contents)
                .scheduleStartDate(scheduleStartDate)
                .scheduleEndDate(scheduleEndDate)
                .build();

        return myScheduleService.create(createRequest, email).getContent();
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

}
