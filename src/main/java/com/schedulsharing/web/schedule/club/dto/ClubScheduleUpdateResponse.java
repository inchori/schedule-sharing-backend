package com.schedulsharing.web.schedule.club.dto;

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
