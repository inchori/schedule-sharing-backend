package com.schedulsharing.dto.ClubSchedule;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubScheduleUpdateResponse {
    private Long id;

    private String name;

    private String contents;

    private LocalDateTime startMeetingDate;

    private LocalDateTime endMeetingDate;
}
