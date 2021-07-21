package com.schedulsharing.web.schedule.my.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MyScheduleDeleteResponse {
    private boolean success;
    private String message;

    public MyScheduleDeleteResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
