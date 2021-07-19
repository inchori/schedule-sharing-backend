package com.schedulsharing.web.schedule.club.dto.suggestion;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "suggestionList")
public class SuggestionResponse {
    private Long id;

    private String title;

    private String contents;

    private String location;

    private int minMember;

    private boolean confirm;

    private LocalDateTime scheduleStartDate;

    private LocalDateTime scheduleEndDate;

    private LocalDateTime voteStartDate;

    private LocalDateTime voteEndDate;

    private String memberName;

    private String memberEmail;
}
