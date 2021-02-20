package com.schedulsharing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedulsharing.config.RestDocsConfiguration;
import com.schedulsharing.dto.Club.ClubCreateRequest;
import com.schedulsharing.dto.Club.ClubCreateResponse;
import com.schedulsharing.dto.Club.ClubInviteRequest;
import com.schedulsharing.dto.member.LoginRequestDto;
import com.schedulsharing.dto.member.SignUpRequestDto;
import com.schedulsharing.dto.member.SignUpResponseDto;
import com.schedulsharing.repository.ClubRepository;
import com.schedulsharing.repository.MemberRepository;
import com.schedulsharing.service.ClubService;
import com.schedulsharing.service.MemberService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
class ClubControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubService clubService;
    @Autowired
    private ClubRepository clubRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
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

    @DisplayName("팀 정상적으로 생성하기")
    @Test
    public void 정상적인_팀생성() throws Exception {
        String clubName = "동네친구";
        String categories = "밥";
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();

        mvc.perform(post("/api/club")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("categories").exists())
                .andExpect(jsonPath("leaderId").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-create",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("clubName").description("생성할 모임의 이름"),
                                fieldWithPath("categories").description("생성할 모임의 카테고리")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("clubId").description("생성된 모임의 고유 아이디"),
                                fieldWithPath("clubName").description("생성된 모임의 이름"),
                                fieldWithPath("categories").description("생성된 모임의 카테고리"),
                                fieldWithPath("leaderId").description("모임을 만든 멤버의 고유 아이디"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("팀 정상적으로 초대하기")
    @Test
    public void 팀_초대하기() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("테스터2")
                .imagePath("imagePath10")
                .build();
        SignUpResponseDto signUpResponseDto1 = memberService.signup(signUpRequestDto1).getContent();
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test3@example.com")
                .password("123456")
                .name("테스터3")
                .imagePath("imagePath101")
                .build();
        SignUpResponseDto signUpResponseDto2 = memberService.signup(signUpRequestDto2).getContent();

        Long member1Id = signUpResponseDto1.getId();
        Long member2Id = signUpResponseDto2.getId();

        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName("동네친구")
                .categories("밥")
                .build();
        ClubCreateResponse clubCreateResponse = clubService.createClub(clubCreateRequest, "test@example.com").getContent();
        Long clubId = clubCreateResponse.getClubId();

        ClubInviteRequest clubInviteRequest = ClubInviteRequest.builder()
                .memberIds(List.of(member1Id, member2Id))
                .build();
        //when
        mvc.perform(RestDocumentationRequestBuilders.post("/api/club/{clubId}/invite",clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubInviteRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-invite",
                        pathParameters(
                                parameterWithName("clubId").description("초대할 클럽의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("memberIds").description("초대할 멤버들의 고유한 아이디 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("success").description("초대를 성공했다면 true 그렇지 않다면 false"),
                                fieldWithPath("message").description("초대를 성공했는지에 대한 메시지"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("club을 만든사람이 아닌 다른사람이 초대할 경우 오류")
    @Test
    public void 클럽장이아닌다른사람이초대_InvalidGrantException() throws Exception {
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("테스터2")
                .imagePath("imagePath10")
                .build();
        SignUpResponseDto signUpResponseDto1 = memberService.signup(signUpRequestDto1).getContent();
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test3@example.com")
                .password("123456")
                .name("테스터3")
                .imagePath("imagePath101")
                .build();
        SignUpResponseDto signUpResponseDto2 = memberService.signup(signUpRequestDto2).getContent();

        Long member1Id = signUpResponseDto1.getId();
        Long member2Id = signUpResponseDto2.getId();

        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName("동네친구")
                .categories("밥")
                .build();
        ClubCreateResponse clubCreateResponse = clubService.createClub(clubCreateRequest, "test2@example.com").getContent();
        Long clubId = clubCreateResponse.getClubId();

        ClubInviteRequest clubInviteRequest = ClubInviteRequest.builder()
                .memberIds(List.of(member1Id, member2Id))
                .build();
        //when
        mvc.perform(post("/api/club/{clubId}/invite",clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubInviteRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("success").value(false));
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