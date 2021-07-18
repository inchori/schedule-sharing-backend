package com.schedulsharing.controller;

import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.web.club.dto.ClubInviteRequest;
import com.schedulsharing.web.club.dto.ClubUpdateRequest;
import com.schedulsharing.web.member.dto.LoginRequestDto;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.web.member.dto.SignUpResponseDto;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
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

import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



class ClubControllerTest extends ApiDocumentationTest{
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

    @DisplayName("클럽 정상적으로 생성하기")
    @Test
    public void 정상적인_클럽생성() throws Exception {
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
                                linkWithRel("club-invite").description("link to club-invite"),
                                linkWithRel("club-getOne").description("link to club-getOne"),
                                linkWithRel("club-update").description("link to club-update"),
                                linkWithRel("club-delete").description("link to club-delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestFields(
                                fieldWithPath("clubName").description("생성할 클럽의 이름"),
                                fieldWithPath("categories").description("생성할 클럽의 카테고리")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("clubId").description("생성된 클럽의 고유 아이디"),
                                fieldWithPath("clubName").description("생성된 클럽의 이름"),
                                fieldWithPath("categories").description("생성된 클럽의 카테고리"),
                                fieldWithPath("leaderId").description("클럽을 만든 멤버의 고유 아이디"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-invite.href").description("link to club-invite"),
                                fieldWithPath("_links.club-getOne.href").description("link to club-getOne"),
                                fieldWithPath("_links.club-update.href").description("link to club-update"),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("클럽에 정상적으로 초대하기")
    @Test
    public void 클럽_초대하기() throws Exception {
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

        Long clubId = createClub("test@example.com");

        ClubInviteRequest clubInviteRequest = ClubInviteRequest.builder()
                .memberIds(List.of(member1Id, member2Id))
                .build();
        //when
        mvc.perform(RestDocumentationRequestBuilders.post("/api/club/{clubId}/invite", clubId)
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
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create"),
                                linkWithRel("club-update").description("link to club-update"),
                                linkWithRel("club-getOne").description("link to club-getOne"),
                                linkWithRel("club-delete").description("link to club-delete")
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
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.club-update.href").description("link to club-update"),
                                fieldWithPath("_links.club-getOne.href").description("link to club-getOne"),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("클럽을 만든사람이 아닌 다른사람이 초대할 경우 오류")
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

        Long clubId = createClub("test2@example.com");

        ClubInviteRequest clubInviteRequest = ClubInviteRequest.builder()
                .memberIds(List.of(member1Id, member2Id))
                .build();
        //when
        mvc.perform(post("/api/club/{clubId}/invite", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubInviteRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("success").value(false));
    }

    @DisplayName("클럽장인 경우 클럽 조회하면 추가적인 링크가 보여야한다.")
    @Test
    public void 클럽장_클럽조회() throws Exception {
        Long clubId = createClub("test@example.com");
        mvc.perform(RestDocumentationRequestBuilders.get("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.club-invite.href").exists())
                .andExpect(jsonPath("_links.club-update.href").exists())
                .andExpect(jsonPath("_links.club-delete.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-getOne",
                        pathParameters(
                                parameterWithName("clubId").description("조회할 클럽의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create"),
                                linkWithRel("club-invite").description("link to club-invite"),
                                linkWithRel("club-update").description("link to club-invite"),
                                linkWithRel("club-delete").description("link to club-delete")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("clubId").description("조회한 클럽의 고유아이디"),
                                fieldWithPath("clubName").description("조회한 클럽의 이름"),
                                fieldWithPath("categories").description("조회한 클럽의 카테고리"),
                                fieldWithPath("leaderId").description("조회한 클럽을 만든 멤버의 고유아이디"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.club-update.href").description("link to club-update, 클럽장인경우에만 보입니다."),
                                fieldWithPath("_links.club-invite.href").description("link to club-invite, 클럽장인경우에만 보입니다."),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete, 클럽장인경우에만 보입니다."),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("클럽장이 아닌 경우 클럽 조회하면 추가적인 안보여야한다.")
    @Test
    public void 클럽장이_아닌경우_클럽조회() throws Exception {
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("테스터2")
                .imagePath("imagePath10")
                .build();
        memberService.signup(signUpRequestDto1);
        Long clubId = createClub("test2@example.com");
        mvc.perform(RestDocumentationRequestBuilders.get("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists());
    }

    @DisplayName("조회할 클럽이 없는 클럽인 경우")
    @Test
    public void 조회할_클럽_없음() throws Exception {
        mvc.perform(RestDocumentationRequestBuilders.get("/api/club/11111")
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("httpStatus").exists())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("message").exists());
    }

    @DisplayName("클럽 수정 성공")
    @Test
    public void 클럽수정성공() throws Exception {
        Long clubId = createClub("test@example.com");
        ClubUpdateRequest clubUpdateRequest = ClubUpdateRequest.builder()
                .clubName("수정된 클럽이름")
                .categories("수정된 클럽카테고리")
                .build();
        mvc.perform(RestDocumentationRequestBuilders.put("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("categories").exists())
                .andExpect(jsonPath("leaderId").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.club-invite.href").exists())
                .andExpect(jsonPath("_links.club-delete.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-update",
                        pathParameters(
                                parameterWithName("clubId").description("수정할 클럽의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create"),
                                linkWithRel("club-getOne").description("link to club-getOne"),
                                linkWithRel("club-invite").description("link to club-invite"),
                                linkWithRel("club-delete").description("link to club-delete")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("clubId").description("수정된 클럽의 고유 아이디"),
                                fieldWithPath("clubName").description("수정된 클럽의 이름"),
                                fieldWithPath("categories").description("수정된 클럽의 카테고리"),
                                fieldWithPath("leaderId").description("클럽을 만든 멤버의 고유 아이디"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete"),
                                fieldWithPath("_links.club-getOne.href").description("link to club-getOne"),
                                fieldWithPath("_links.club-invite.href").description("link to club-invite"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("클럽 삭제 성공")
    @Test
    public void 클럽삭제성공() throws Exception {
        Long clubId = createClub("test@example.com");

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-delete",
                        pathParameters(
                                parameterWithName("clubId").description("삭제할 클럽의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("success").description("삭제를 성공했다면 true 그렇지 않다면 false"),
                                fieldWithPath("message").description("삭제를 성공했는지에 대한 메시지"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }


    @DisplayName("권한이 없을 경우 클럽 삭제 실패")
    @Test
    public void 클럽삭제실패() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("테스터2")
                .imagePath("imagePath10")
                .build();
        memberService.signup(signUpRequestDto).getContent();

        Long clubId = createClub("test2@example.com");

        mvc.perform(delete("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private Long createClub(String email) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName("동네친구")
                .categories("밥")
                .build();
        ClubCreateResponse clubCreateResponse = clubService.createClub(clubCreateRequest, email).getContent();
        return clubCreateResponse.getClubId();
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