package com.schedulsharing.domain.schedule;

import com.schedulsharing.web.schedule.my.dto.MyScheduleCreateRequest;
import com.schedulsharing.web.schedule.my.dto.MyScheduleUpdateRequest;
import com.schedulsharing.domain.common.BaseTimeEntity;
import com.schedulsharing.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "my_schedule")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MySchedule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_schedule_id")
    private Long id;

    private String name;

    private String contents;

    private LocalDateTime scheduleStartDate;

    private LocalDateTime scheduleEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    private void setMember(Member member) {
        this.member = member;
        member.getMySchedules().add(this);
    }

    public void update(MyScheduleUpdateRequest updateRequest) {
        this.name = updateRequest.getName();
        this.contents = updateRequest.getContents();
        this.scheduleStartDate = updateRequest.getScheduleStartDate();
        this.scheduleEndDate = updateRequest.getScheduleEndDate();
    }

    public static MySchedule createMySchedule(MyScheduleCreateRequest createRequest, Member member) {
        MySchedule mySchedule = MySchedule.builder()
                .name(createRequest.getName())
                .contents(createRequest.getContents())
                .scheduleStartDate(createRequest.getScheduleStartDate())
                .scheduleEndDate(createRequest.getScheduleEndDate())
                .build();
        mySchedule.setMember(member);
        return mySchedule;
    }


}
