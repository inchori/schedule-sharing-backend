package com.schedulsharing.web.member;

import com.schedulsharing.service.member.MemberService;
import com.schedulsharing.web.member.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public SignUpResponseDto signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {

        return memberService.signup(signUpRequestDto);
    }

    @PostMapping("/checkEmail")
    public EmailCheckResponseDto existedEmailCheck(@RequestBody @Valid EmailCheckRequestDto emailCheckRequestDto) {

        return memberService.emailCheck(emailCheckRequestDto.getEmail());
    }

    @GetMapping("/getClubs")
    public List<GetClubsResponse> getClubs(Authentication authentication) {

        return memberService.getClubs(authentication.getName());
    }

    @GetMapping("/search")
    public MemberResponse getMemberByEmail(@RequestParam("email") String email) {

        return memberService.getMemberByEmail(email);
    }

    @GetMapping("/{id}")
    public MemberResponse getMemberById(@PathVariable("id") Long id) {
        return memberService.getMemberById(id);
    }

    @PutMapping("/{id}")
    public MemberUpdateResponse updateMember(@PathVariable("id") Long id,
                                             @RequestBody @Valid MemberUpdateRequest memberUpdateRequest,
                                             Authentication authentication) {
        return memberService.updateMember(id, memberUpdateRequest, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public MemberDeleteResponse deleteMember(@PathVariable("id") Long id, Authentication authentication) {
        return memberService.deleteMember(id, authentication.getName());
    }
}
