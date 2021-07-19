package com.schedulsharing.web.schedule.club.dto.suggestion;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuggestionCreateResponse {
    private Long id;

    private String title;

    private String contents;

    private String location;

    private int minMember;

    private LocalDateTime scheduleStartDate;

    private LocalDateTime scheduleEndDate;

    private LocalDateTime voteStartDate;

    private LocalDateTime voteEndDate;
}
