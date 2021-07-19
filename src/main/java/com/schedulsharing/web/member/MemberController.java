package com.schedulsharing.web.member;

import com.schedulsharing.web.member.dto.EmailCheckRequestDto;
import com.schedulsharing.web.member.dto.MemberUpdateRequest;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {

        return ResponseEntity.ok().body(memberService.signup(signUpRequestDto));
    }

    @PostMapping("/checkEmail")
    public ResponseEntity existedEmailCheck(@RequestBody @Valid EmailCheckRequestDto emailCheckRequestDto) {

        return ResponseEntity.ok(memberService.emailCheck(emailCheckRequestDto.getEmail()));
    }

    @GetMapping("/getClubs")
    public ResponseEntity getClubs(Authentication authentication) {

        return ResponseEntity.ok(memberService.getClubs(authentication.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity getMemberByEmail(@RequestParam("email") String email) {

        return ResponseEntity.ok(memberService.getMemberByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity getMemberById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateMember(@PathVariable("id") Long id,
                                       @RequestBody @Valid MemberUpdateRequest memberUpdateRequest,
                                       Authentication authentication) {
        return ResponseEntity.ok(memberService.updateMember(id, memberUpdateRequest, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMember(@PathVariable("id") Long id, Authentication authentication) {
        return ResponseEntity.ok(memberService.deleteMember(id, authentication.getName()));
    }
}
