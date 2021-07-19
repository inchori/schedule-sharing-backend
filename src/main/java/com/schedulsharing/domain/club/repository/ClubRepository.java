package com.schedulsharing.domain.club.repository;

import com.schedulsharing.domain.club.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {
    @Query("select c from Member m ,Club c, MemberClub mc " +
            "where m.id=mc.member.id and c.id=mc.club.id and m.email=:email")
    List<Club> findByMemberEmail(@Param("email") String email);
}
