package com.schedulsharing.domain.member;

import com.schedulsharing.web.member.dto.MemberUpdateRequest;
import com.schedulsharing.domain.common.BaseTimeEntity;
import com.schedulsharing.domain.club.MemberClub;
import com.schedulsharing.domain.vote.VoteCheck;
import com.schedulsharing.domain.schedule.ClubSchedule;
import com.schedulsharing.domain.schedule.MySchedule;
import com.schedulsharing.domain.schedule.ScheduleSuggestion;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
@EqualsAndHashCode(of = "id", callSuper = false)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String name;

    private String imagePath;

    public void update(MemberUpdateRequest memberUpdateRequest, PasswordEncoder passwordEncoder) {
        this.name = memberUpdateRequest.getName();
        this.password = passwordEncoder.encode(memberUpdateRequest.getPassword());
        this.imagePath = memberUpdateRequest.getImagePath();
    }

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<MemberClub> memberClubs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<ClubSchedule> clubSchedules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<MySchedule> mySchedules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<ScheduleSuggestion> suggestions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<VoteCheck> voteChecks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<MemberRole> roles;
}