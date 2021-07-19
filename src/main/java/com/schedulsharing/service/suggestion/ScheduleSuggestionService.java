package com.schedulsharing.service.suggestion;

import com.schedulsharing.domain.club.Club;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.schedule.ScheduleSuggestion;
import com.schedulsharing.domain.schedule.repository.suggestion.ScheduleSuggestionRepository;
import com.schedulsharing.domain.vote.VoteCheck;
import com.schedulsharing.domain.vote.repository.VoteCheckRepository;
import com.schedulsharing.excpetion.PermissionException;
import com.schedulsharing.service.club.exception.ClubNotFoundException;
import com.schedulsharing.service.member.exception.MemberNotFoundException;
import com.schedulsharing.service.suggestion.exception.DuplicateVoteCheckException;
import com.schedulsharing.service.suggestion.exception.SuggestionNotFoundException;
import com.schedulsharing.web.dto.resource.SuggestionResource;
import com.schedulsharing.web.schedule.club.dto.suggestion.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleSuggestionService {
    private final ScheduleSuggestionRepository scheduleSuggestionRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final VoteCheckRepository voteCheckRepository;
    private final ModelMapper modelMapper;

    public EntityModel<SuggestionCreateResponse> create(SuggestionCreateRequest suggestionCreateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Club club = clubRepository.findById(suggestionCreateRequest.getClubId()).orElseThrow(ClubNotFoundException::new);
        checkClubMember(member, club);
        ScheduleSuggestion suggestion = ScheduleSuggestion.createSuggestion(suggestionCreateRequest, member, club);
        ScheduleSuggestion savedSuggestion = scheduleSuggestionRepository.save(suggestion);
        SuggestionCreateResponse createResponse = modelMapper.map(savedSuggestion, SuggestionCreateResponse.class);

        return SuggestionResource.createSuggestionLink(createResponse);
    }

    @Transactional(readOnly = true)
    public EntityModel<SuggestionVoteCheckResponse> getSuggestion(Long id, String email) {
        Optional<ScheduleSuggestion> optionalScheduleSuggestion = scheduleSuggestionRepository.findById(id);
        if (optionalScheduleSuggestion.isEmpty()) {
            throw new SuggestionNotFoundException();
        }
        ScheduleSuggestion scheduleSuggestion = optionalScheduleSuggestion.get();
        Optional<List<VoteCheck>> voteCheckAgree = voteCheckRepository.findBySuggestionIdAndAgreeTrue(id);

        Optional<List<VoteCheck>> voteCheckDisagree = voteCheckRepository.findBySuggestionIdAndDisagree(id);

        SuggestionVoteCheckResponse suggestionResponse = modelMapper.map(scheduleSuggestion, SuggestionVoteCheckResponse.class);
        if (voteCheckAgree.isEmpty()) {
            suggestionResponse.setVoteAgreeDto(VoteAgreeDto.builder().count(0).memberName(new ArrayList<>()).build());
        } else {
            List<String> memberNamesAgree = voteCheckAgree.get().stream().map(voteCheck -> voteCheck.getMember().getName()).collect(Collectors.toList());

            suggestionResponse.setVoteAgreeDto(VoteAgreeDto.builder().count(memberNamesAgree.size()).memberName(memberNamesAgree).build());
        }

        if (voteCheckDisagree.isEmpty()) {
            suggestionResponse.setVoteDisagreeDto(VoteDisagreeDto.builder().count(0).memberName(new ArrayList<>()).build());
        } else {
            List<String> memberNamesAgree = voteCheckDisagree.get().stream().map(voteCheck -> voteCheck.getMember().getName()).collect(Collectors.toList());
            suggestionResponse.setVoteDisagreeDto(VoteDisagreeDto.builder().count(memberNamesAgree.size()).memberName(memberNamesAgree).build());
        }

        return SuggestionResource.getSuggestionLink(suggestionResponse, email);
    }

    public EntityModel<SuggestionResponse> update(Long id, SuggestionUpdateRequest suggestionUpdateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ScheduleSuggestion suggestion = scheduleSuggestionRepository.findById(id).orElseThrow(SuggestionNotFoundException::new);
        if (!suggestion.getMember().equals(member)) {
            throw new PermissionException();
        }
        suggestion.update(suggestionUpdateRequest);
        SuggestionResponse suggestionResponse = modelMapper.map(suggestion, SuggestionResponse.class);

        return SuggestionResource.updateSuggestionLink(suggestionResponse);
    }

    public EntityModel<SuggestionDeleteResponse> delete(Long id, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ScheduleSuggestion suggestion = scheduleSuggestionRepository.findById(id).orElseThrow(SuggestionNotFoundException::new);
        if (!suggestion.getMember().equals(member)) {
            throw new PermissionException();
        }
        scheduleSuggestionRepository.deleteById(id);
        SuggestionDeleteResponse suggestionDeleteResponse = SuggestionDeleteResponse.builder()
                .success(true)
                .message("클럽스케줄제안을 삭제하였습니다.")
                .build();

        return SuggestionResource.deleteSuggestionLink(suggestionDeleteResponse, id);
    }

    @Transactional(readOnly = true)
    public CollectionModel<EntityModel<SuggestionResponse>> getSuggestionListConfirm(Long clubId, YearMonth yearMonth, String email) {
        List<ScheduleSuggestion> suggestions = scheduleSuggestionRepository.findAllByClubIdConfirm(clubId, yearMonth);
        List<SuggestionResponse> responseList = suggestions.stream()
                .map(clubSchedule -> modelMapper.map(clubSchedule, SuggestionResponse.class))
                .collect(Collectors.toList());

        return SuggestionResource.getSuggestionListConfirmLink(responseList, clubId, email);
    }

    @Transactional(readOnly = true)
    public CollectionModel<EntityModel<SuggestionResponse>> getSuggestionList(Long clubId, LocalDate now, String email) {
        List<ScheduleSuggestion> suggestions = scheduleSuggestionRepository.findAllByClubId(clubId, now);
        List<SuggestionResponse> responseList = suggestions.stream()
                .map(clubSchedule -> modelMapper.map(clubSchedule, SuggestionResponse.class))
                .collect(Collectors.toList());

        return SuggestionResource.getSuggestionListLink(responseList, clubId, email);
    }

    public EntityModel<SuggestionVoteResponse> vote(Long suggestionId, SuggestionVoteRequest suggestionVoteRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        ScheduleSuggestion suggestion = scheduleSuggestionRepository.findById(suggestionId).orElseThrow(SuggestionNotFoundException::new);
        Club club = suggestion.getClub();
        checkClubMember(member, club); //클럽원인지 검사
        if (voteCheckRepository.findBySuggestionIdAndMemberId(suggestionId, member.getId()).isPresent()) {
            throw new DuplicateVoteCheckException();
        }
        VoteCheck voteCheck = VoteCheck.createVoteCheck(suggestionVoteRequest, member, suggestion);
        VoteCheck vote = voteCheckRepository.save(voteCheck);
        SuggestionVoteResponse response = modelMapper.map(vote, SuggestionVoteResponse.class);

        if (voteCheckRepository.findBySuggestionIdAndAgreeTrue(suggestionId).isPresent()) {
            if (suggestion.getMinMember() <= voteCheckRepository.findBySuggestionIdAndAgreeTrue(suggestionId).get().size()
                    && suggestion.getVoteEndDate().isAfter(LocalDateTime.now())) {
                suggestion.updateConfirmTrue();
            }
        }

        return SuggestionResource.getSuggestionVoteLink(response, email, suggestionId);
    }

    private void checkClubMember(Member member, Club club) {
        List<Member> members = memberRepository.findAllByClubId(club.getId());
        List<Long> memberIdList = members.stream().map(Member::getId).collect(Collectors.toList());
        if (!memberIdList.contains(member.getId())) {
            throw new PermissionException();
        }
    }
}
