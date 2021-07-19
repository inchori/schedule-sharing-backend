package com.schedulsharing.web.club.dto;

import lombok.Data;

@Data
public class ClubInviteResponse {
    private boolean success;
    private String message;

    public ClubInviteResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
