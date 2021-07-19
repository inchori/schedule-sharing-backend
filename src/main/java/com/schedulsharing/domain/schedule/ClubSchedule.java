package com.schedulsharing.domain.schedule;

import com.schedulsharing.web.schedule.club.dto.ClubScheduleCreateRequest;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleUpdateRequest;
import com.schedulsharing.domain.common.BaseTimeEntity;
import com.schedulsharing.domain.club.Club;
import com.schedulsharing.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "club_schedule")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_schedule_id")
    private Long id;

    private String name;

    private String contents;

    private LocalDateTime startMeetingDate;

    private LocalDateTime endMeetingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //양방향 연관관계가 생길 경우 상대편에도 값을 넣어주기 위해 편의메서드를 만들어 놓음
    public void setClub(Club club) {
        this.club = club;
    }

    public void setMember(Member member) {
        this.member = member;
        member.getClubSchedules().add(this);
    }

    public void update(ClubScheduleUpdateRequest updateRequest) {
        this.name = updateRequest.getName();
        this.contents = updateRequest.getContents();
        this.startMeetingDate = updateRequest.getStartMeetingDate();
        this.endMeetingDate = updateRequest.getEndMeetingDate();
    }

    public static ClubSchedule createClubSchedule(ClubScheduleCreateRequest createRequest, Member member, Club club) {
        ClubSchedule clubSchedule = ClubSchedule.builder()
                .name(createRequest.getName())
                .contents(createRequest.getContents())
                .startMeetingDate(createRequest.getStartMeetingDate())
                .endMeetingDate(createRequest.getEndMeetingDate())
                .build();
        clubSchedule.setClub(club);
        clubSchedule.setMember(member);

        return clubSchedule;
    }
}
