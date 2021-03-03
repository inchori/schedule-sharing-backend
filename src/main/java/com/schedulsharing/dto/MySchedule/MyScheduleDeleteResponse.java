package com.schedulsharing.dto.MySchedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyScheduleDeleteResponse {
    private boolean success;
    private String message;
}
