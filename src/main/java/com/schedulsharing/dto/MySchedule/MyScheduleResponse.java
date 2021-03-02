package com.schedulsharing.dto.MySchedule;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyScheduleResponse {
    private Long myScheduleId;
    private String name;
    private String contents;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
