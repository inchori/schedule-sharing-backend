package com.schedulsharing.domain.club;

import com.schedulsharing.domain.common.BaseTimeEntity;
import com.schedulsharing.domain.member.Member;
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
@Table(name = "member_club")
public class MemberClub extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_club_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    public void setClub(Club club) {
        this.club = club;
    }

    public void addMember(Member member) {
        member.getMemberClubs().add(this);
        this.member = member;
    }

    public static MemberClub createMemberClub(Member member) {
        MemberClub memberClub = MemberClub.builder().build();
        memberClub.addMember(member);

        return memberClub;
    }

    public static List<MemberClub> inviteMemberClub(List<Member> member) {
        List<MemberClub> memberClubs = new ArrayList<>();
        for (Member m : member) {
            MemberClub memberClub = MemberClub.builder().build();
            memberClub.addMember(m);
            memberClubs.add(memberClub);
        }
        return memberClubs;
    }
}
