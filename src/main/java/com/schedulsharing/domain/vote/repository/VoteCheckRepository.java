package com.schedulsharing.domain.vote.repository;

import com.schedulsharing.domain.vote.VoteCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteCheckRepository extends JpaRepository<VoteCheck, Long> {
    @Query("select vc from VoteCheck vc,Member m where vc.scheduleSuggestion.id=:suggestionId and vc.member.id=:memberId")
    Optional<VoteCheck> findBySuggestionIdAndMemberId(@Param("suggestionId") Long suggestionId, @Param("memberId") Long memberId);

    @Query("select vc from VoteCheck vc left join vc.scheduleSuggestion ss where vc.scheduleSuggestion.id=:suggestionId and vc.agree=true")
    Optional<List<VoteCheck>> findBySuggestionIdAndAgreeTrue(@Param("suggestionId") Long id);

    @Query("select vc from VoteCheck vc left join vc.scheduleSuggestion ss where vc.scheduleSuggestion.id=:suggestionId and vc.agree=false ")
    Optional<List<VoteCheck>> findBySuggestionIdAndDisagree(@Param("suggestionId") Long id);
}
