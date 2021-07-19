package com.schedulsharing.domain.club;

import com.schedulsharing.domain.common.BaseTimeEntity;
import com.schedulsharing.domain.schedule.ScheduleSuggestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "club")
public class Club extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long id;

    private String clubName;

    private String categories;

    private Long leaderId;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberClub> memberClubs = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<ScheduleSuggestion> suggestions = new ArrayList<>();

    public void addMemberClubs(List<MemberClub> memberClub) {
        for (MemberClub mClub : memberClub) {
            memberClubs.add(mClub);
            mClub.setClub(this);
        }
    }

    public void addMemberClub(MemberClub memberClub) {
        memberClubs.add(memberClub);
        memberClub.setClub(this);
    }

    public void update(String clubName, String categories) {
        this.clubName = clubName;
        this.categories = categories;
    }

    public static Club createClub(String clubName, Long leaderId, String categories, MemberClub memberClub) {
        Club club = Club.builder()
                .clubName(clubName)
                .leaderId(leaderId)
                .categories(categories)
                .build();
        club.addMemberClub(memberClub);

        return club;
    }

    public static Club inviteClub(Club club, List<MemberClub> memberClubs) {
        club.addMemberClubs(memberClubs);
        return club;
    }
}
