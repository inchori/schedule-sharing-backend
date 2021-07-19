package com.schedulsharing.service.vote;

import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.vote.VoteCheck;
import com.schedulsharing.domain.vote.repository.VoteCheckRepository;
import com.schedulsharing.excpetion.PermissionException;
import com.schedulsharing.service.member.exception.MemberNotFoundException;
import com.schedulsharing.service.vote.exception.VoteNotFoundException;
import com.schedulsharing.web.dto.resource.SuggestionResource;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateRequest;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {
    private final MemberRepository memberRepository;
    private final VoteCheckRepository voteCheckRepository;
    private final ModelMapper modelMapper;

    public EntityModel<SuggestionVoteUpdateResponse> updateVote(Long id, SuggestionVoteUpdateRequest updateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        VoteCheck voteCheck = voteCheckRepository.findById(id).orElseThrow(VoteNotFoundException::new);
        if (!voteCheck.getMember().equals(member)) {
            throw new PermissionException();
        }
        voteCheck.update(updateRequest);
        SuggestionVoteUpdateResponse voteUpdateResponse = modelMapper.map(voteCheck, SuggestionVoteUpdateResponse.class);

        return SuggestionResource.updateVoteLink(voteUpdateResponse);
    }
}
