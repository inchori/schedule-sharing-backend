package com.schedulsharing.domain.vote;

import com.schedulsharing.domain.common.BaseTimeEntity;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionVoteRequest;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateRequest;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.schedule.ScheduleSuggestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "vote_check")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteCheck extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_check_id")
    private Long id;

    private boolean agree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_suggestion_id")
    private ScheduleSuggestion scheduleSuggestion;

    public static VoteCheck createVoteCheck(SuggestionVoteRequest suggestionVoteRequest, Member member, ScheduleSuggestion scheduleSuggestion) {
        return VoteCheck.builder()
                .agree(suggestionVoteRequest.isAgree())
                .member(member)
                .scheduleSuggestion(scheduleSuggestion)
                .build();
    }

    public void update(SuggestionVoteUpdateRequest updateRequest) {
        this.agree = updateRequest.isAgree();
    }
}
