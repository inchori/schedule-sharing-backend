package com.schedulsharing.web.schedule.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubScheduleCreateResponse {
    private Long id;
    private String name;
    private String contents;
    private LocalDateTime startMeetingDate;
    private LocalDateTime endMeetingDate;
}
