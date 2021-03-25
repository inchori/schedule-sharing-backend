package com.schedulsharing.controller;

import com.schedulsharing.dto.member.LoginRequestDto;
import com.schedulsharing.dto.member.LoginResponseDto;
import com.schedulsharing.dto.resource.AuthResource;
import com.schedulsharing.entity.member.Member;
import com.schedulsharing.jwt.JwtFilter;
import com.schedulsharing.jwt.TokenProvider;
import com.schedulsharing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;

    @PostMapping("/authenticate")
    public ResponseEntity authorize(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        System.out.println("loginDto = " + loginRequestDto);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String token = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token);
        Member member = memberRepository.findByEmail(authentication.getName()).get();

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .imagePath(member.getImagePath())
                .access_token(token)
                .build();

        return new ResponseEntity(AuthResource.authorizeLink(loginResponseDto), httpHeaders, HttpStatus.OK);
    }
}
