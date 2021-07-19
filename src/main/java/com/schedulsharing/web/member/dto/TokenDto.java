package com.schedulsharing.web.member.dto;

import lombok.Data;

@Data
public class TokenDto {
    private String access_token;

    public TokenDto(String access_token) {
        this.access_token = access_token;
    }
}
