package com.schedulsharing.web.member.dto;

import lombok.Data;

@Data
public class EmailCheckResponseDto {
    private boolean duplicate;
    private String message;

    public EmailCheckResponseDto(boolean duplicate, String message) {
        this.duplicate = duplicate;
        this.message = message;
    }

}
