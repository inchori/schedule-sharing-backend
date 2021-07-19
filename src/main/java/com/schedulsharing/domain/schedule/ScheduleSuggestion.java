package com.schedulsharing.domain.schedule;

import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionCreateRequest;
import com.schedulsharing.web.schedule.club.dto.suggestion.SuggestionUpdateRequest;
import com.schedulsharing.domain.common.BaseTimeEntity;
import com.schedulsharing.domain.club.Club;
import com.schedulsharing.domain.vote.VoteCheck;
import com.schedulsharing.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "schedule_suggestion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleSuggestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_suggestion_id")
    private Long id;

    private String title;

    private String contents;

    private String location;

    private int minMember;

    private boolean isConfirm;

    private LocalDateTime scheduleStartDate;

    private LocalDateTime scheduleEndDate;

    private LocalDateTime voteStartDate;

    private LocalDateTime voteEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "scheduleSuggestion", cascade = CascadeType.REMOVE)
    private List<VoteCheck> voteChecks = new ArrayList<>();

    public void setClub(Club club) {
        this.club = club;
        club.getSuggestions().add(this);
    }

    public void setMember(Member member) {
        this.member = member;
        member.getSuggestions().add(this);
    }
    //테스트를 위해 생성 추후에 따로 로직 처리해야함
    public void updateConfirmTrue(){
        this.isConfirm=true;
    }

    public void update(SuggestionUpdateRequest suggestionUpdateRequest) {
        this.title = suggestionUpdateRequest.getTitle();
        this.contents = suggestionUpdateRequest.getContents();
        this.minMember = suggestionUpdateRequest.getMinMember();
        this.location = suggestionUpdateRequest.getLocation();
        this.scheduleStartDate = suggestionUpdateRequest.getScheduleStartDate();
        this.scheduleEndDate = suggestionUpdateRequest.getScheduleEndDate();
        this.voteStartDate = suggestionUpdateRequest.getVoteStartDate();
        this.voteEndDate = suggestionUpdateRequest.getVoteEndDate();
    }

    public static ScheduleSuggestion createSuggestion(SuggestionCreateRequest suggestionCreateRequest, Member member, Club club) {
        ScheduleSuggestion scheduleSuggestion = ScheduleSuggestion.builder()
                .title(suggestionCreateRequest.getTitle())
                .contents(suggestionCreateRequest.getContents())
                .location(suggestionCreateRequest.getLocation())
                .minMember(suggestionCreateRequest.getMinMember())
                .scheduleStartDate(suggestionCreateRequest.getScheduleStartDate())
                .scheduleEndDate(suggestionCreateRequest.getScheduleEndDate())
                .voteStartDate(suggestionCreateRequest.getVoteStartDate())
                .voteEndDate(suggestionCreateRequest.getVoteEndDate())
                .isConfirm(false)
                .build();
        scheduleSuggestion.setClub(club);
        scheduleSuggestion.setMember(member);

        return scheduleSuggestion;
    }
}
