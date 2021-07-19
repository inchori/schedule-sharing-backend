package com.schedulsharing.web.member.dto;

import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    @NotNull
    @Size(min = 3, max = 50)
    @Email
    private String email;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    private String imagePath;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .name(this.name)
                .imagePath(this.imagePath)
                .roles(Collections.singletonList(MemberRole.USER))
                .build();
    }
}
