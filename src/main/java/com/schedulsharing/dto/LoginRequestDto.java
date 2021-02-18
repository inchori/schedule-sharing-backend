package com.schedulsharing.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotNull
    @Size(min = 3, max = 50)
    @Email
    private String email;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;
}
