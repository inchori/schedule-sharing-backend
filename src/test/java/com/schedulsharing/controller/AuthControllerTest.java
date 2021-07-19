package com.schedulsharing.controller;

import com.schedulsharing.web.member.dto.LoginRequestDto;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthControllerTest extends ApiDocumentationTest{
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
    }

    @DisplayName("로그인 성공")
    @Test
    public void 로그인_성공() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);


        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("test@example.com")
                .password("1234")
                .build();

        mvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andExpect(jsonPath("access_token").exists())
                .andDo(document("member-login",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("로그인 이메일"),
                                fieldWithPath("password").description("로그인 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("로그인인 한 유저의 고유 아이디"),
                                fieldWithPath("name").description("로그인인 한 유저의 이름"),
                                fieldWithPath("email").description("로그인인 한 유저의 이메일"),
                                fieldWithPath("imagePath").description("로그인인 한 유저의 프로필파일"),
                                fieldWithPath("access_token").description("로그인인 한 유저의 access_token"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("로그인 실패")
    @Test
    public void 로그인실패() throws Exception{
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);


        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("test@example.com")
                .password("xxx")
                .build();

        mvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("httpStatus").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("error").exists())
                .andDo(document("member-login-fail",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("로그인 이메일"),
                                fieldWithPath("password").description("로그인 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("httpStatus").description("httpStatus"),
                                fieldWithPath("error").description("Error Code"),
                                fieldWithPath("message").description("아이디와 비밀번호를 확인해달라는 메시지")

                        )
                ));
    }
}