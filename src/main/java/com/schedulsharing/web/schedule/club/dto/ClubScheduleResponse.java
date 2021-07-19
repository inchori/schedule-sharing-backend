package com.schedulsharing.web.schedule.club.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "clubScheduleList")
public class ClubScheduleResponse {
    private Long id;
    private String name;
    private String contents;
    private LocalDateTime startMeetingDate;
    private LocalDateTime endMeetingDate;
    private String memberName;
    private String memberEmail;
}
