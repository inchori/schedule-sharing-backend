package com.schedulsharing.domain.member.repository;

import com.schedulsharing.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String username);

    @Query("select m from Member m ,Club c, MemberClub mc " +
            "where m.id=mc.member.id and c.id=mc.club.id and c.id=:id")
    List<Member> findAllByClubId(@Param("id") Long id);
}
