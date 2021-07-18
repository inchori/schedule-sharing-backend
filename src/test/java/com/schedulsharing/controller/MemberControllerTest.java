package com.schedulsharing.controller;

import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.service.member.MemberService;
import com.schedulsharing.web.member.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MemberControllerTest extends ApiDocumentationTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubService clubService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
    }

    @DisplayName("회원가입 성공")
    @Test
    public void 회원가입_성공() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();

        mvc.perform(post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("member-signup",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("애플리케이션에서 사용할 이름"),
                                fieldWithPath("imagePath").description("프로필파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("회원가입한 멤버의 고유 아이디"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("name").description("애플리케이션에서 사용할 이름"),
                                fieldWithPath("imagePath").description("프로필파일"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("회원가입할 때 이메일이 중복된 경우")
    @Test
    public void 중복된이메일_회원가입() throws Exception {
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto1);

        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester2")
                .password("12345")
                .imagePath("imagePath2")
                .build();

        mvc.perform(post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto2)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists());
    }

    @DisplayName("중복된 이메일 체크")
    @Test
    public void 중복된이메일체크() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email(email)
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto1);
        EmailCheckRequestDto emailCheckRequestDto = new EmailCheckRequestDto(email);

        mvc.perform(post("/api/member/checkEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailCheckRequestDto)))
                .andDo(print())
                .andExpect(jsonPath("message").exists())
                .andDo(document("member-checkEmail",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("중복검사 이메일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("duplicate").description("이메일이 중복되었다면 true 중복되지 않았다면 false"),
                                fieldWithPath("message").description("이메일 중복 확인 메시지"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("로그인한 유저의 클럽가져오기")
    @Test
    public void 로그인유저의_클럽조회() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("테스터")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);

        createClub(email, "동네친구", "밥");
        createClub(email, "스터디 모임", "스터디");
        createClub(email, "회사 모임", "회식");

        mvc.perform(get("/api/member/getClubs")
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.clubList[0].clubId").exists())
                .andExpect(jsonPath("_embedded.clubList[0].clubName").exists())
                .andExpect(jsonPath("_embedded.clubList[0].categories").exists())
                .andExpect(jsonPath("_embedded.clubList[0].leaderId").exists())
                .andDo(document("member-getClubs",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.clubList[0].clubId").description("클럽의 고유 아이디"),
                                fieldWithPath("_embedded.clubList[0].clubName").description("클럽의 이름"),
                                fieldWithPath("_embedded.clubList[0].categories").description("클럽의 카테고리"),
                                fieldWithPath("_embedded.clubList[0].leaderId").description("클럽을 생성한 멤버의 고유아이디"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("이메일로 검색해서 유저찾기")
    @Test
    public void 이메일검색() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("테스터")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);
        String email2 = "test2@example.com";
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email(email2)
                .password("1234")
                .name("테스터")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto2);

        mvc.perform(get("/api/member/search")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .param("email", email2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andDo(document("member-findByEmail",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestParameters(
                                parameterWithName("email").description("검색할 멤버의 이메일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("이메일검색 결과 나온 멤버의 고유 아이디"),
                                fieldWithPath("email").description("이메일검색 결과 나온 멤버의 이메일"),
                                fieldWithPath("name").description("이메일검색 결과 나온 멤버의 이름"),
                                fieldWithPath("imagePath").description("이메일검색 결과 나온 멤버의 이미지 경로"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("이메일로 검색시 해당 유저가 없는 경우")
    @Test
    public void 없는멤버_이메일검색() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("테스터")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);


        mvc.perform(get("/api/member/search")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .param("email", "test3@example.com"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("httpStatus").exists())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("message").exists())
                .andDo(document("member-findByEmail-fail",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        requestParameters(
                                parameterWithName("email").description("검색할 멤버의 이메일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("httpStatus").description("httpStatus"),
                                fieldWithPath("error").description("error 종류"),
                                fieldWithPath("message").description("해당 이메일을 가진 멤버가 없다는 메시지")
                        )
                ));
    }

    @DisplayName("멤버 id로 단건 조회")
    @Test
    public void 멤버_id로_단건_조회() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("테스터")
                .imagePath("imagePath")
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto).getContent();
        mvc.perform(RestDocumentationRequestBuilders.get("/api/member/{id}", signUpResponseDto.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andDo(document("member-findById",
                        pathParameters(
                                parameterWithName("id").description("멤버의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("멤버의 고유아이디"),
                                fieldWithPath("email").description("멤버의 이메일 주소"),
                                fieldWithPath("name").description("멤버의 이름"),
                                fieldWithPath("imagePath").description("멤버의 프로필 사진 경로"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("멤버 수정 성공 테스트")
    @Test
    public void 멤버_수정_성공_테스트() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("테스터")
                .password("1234")
                .imagePath("imagePath")
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto).getContent();
        MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
                .name("수정할 이름")
                .password("수정할 비밀번호")
                .imagePath("수정할 이미지 경로")
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/member/{id}", signUpResponseDto.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andDo(document("member-update",
                        pathParameters(
                                parameterWithName("id").description("수정할 멤버의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인한 유저의 토큰"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 멤버의 이름"),
                                fieldWithPath("password").description("수정할 멤버의 비밀번호"),
                                fieldWithPath("imagePath").description("수정할 멤버의 프로필 사진 경로")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수정한 멤버의 고유아이디"),
                                fieldWithPath("name").description("수정한 멤버의 이름"),
                                fieldWithPath("email").description("멤버의 이메일"),
                                fieldWithPath("imagePath").description("수정한 멤버의 프로필 사진 경로"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));

    }


    @DisplayName("멤버 탈퇴 및 삭제 성공 테스트")
    @Test
    public void 멤버_삭제_성공() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("테스터")
                .password("1234")
                .imagePath("imagePath")
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto).getContent();

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/member/{id}", signUpResponseDto.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").exists())
                .andDo(document("member-delete",
                        pathParameters(
                                parameterWithName("id").description("삭제할 멤버의 고유 아이디")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
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
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));


    }

    private void createClub(String email, String name, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(name)
                .categories(categories)
                .build();
        clubService.createClub(clubCreateRequest, email);
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