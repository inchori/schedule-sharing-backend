package com.schedulsharing.web.schedule.club.dto.suggestion;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuggestionVoteCheckResponse {
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

    private VoteAgreeDto agree;

    private VoteDisagreeDto disagree;

    public void setVoteAgreeDto(VoteAgreeDto voteAgreeDto) {
        this.agree = voteAgreeDto;
    }

    public void setVoteDisagreeDto(VoteDisagreeDto voteDisagreeDto) {
        this.disagree = voteDisagreeDto;
    }
}

