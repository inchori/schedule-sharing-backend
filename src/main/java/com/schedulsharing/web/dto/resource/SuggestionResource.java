package com.schedulsharing.web.dto.resource;

import com.schedulsharing.web.schedule.club.ScheduleSuggestionController;
import com.schedulsharing.web.vote.dto.SuggestionVoteUpdateResponse;
import com.schedulsharing.web.schedule.club.dto.suggestion.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class SuggestionResource {
    private static WebMvcLinkBuilder selfLinkBuilder = linkTo(ScheduleSuggestionController.class);

    public static EntityModel<SuggestionCreateResponse> createSuggestionLink(SuggestionCreateResponse createResponse) {
        List<Link> links = getSelfLink();
        links.add(selfLinkBuilder.slash(createResponse.getId()).withRel("suggestion-getOne"));
        links.add(selfLinkBuilder.slash(createResponse.getId()).withRel("suggestion-update"));
        links.add(selfLinkBuilder.slash(createResponse.getId()).withRel("suggestion-delete"));
        links.add(Link.of("/docs/index.html#resources-suggestion-create", "profile"));
        return EntityModel.of(createResponse, links);
    }

    public static EntityModel<SuggestionVoteCheckResponse> getSuggestionLink(SuggestionVoteCheckResponse suggestionResponse, String email) {
        List<Link> links = getSelfLink(suggestionResponse.getId());
        if (suggestionResponse.getMemberEmail().equals(email)) {
            links.add(selfLinkBuilder.slash(suggestionResponse.getId()).withRel("suggestion-update"));
            links.add(selfLinkBuilder.slash(suggestionResponse.getId()).withRel("suggestion-delete"));
        }
        links.add(selfLinkBuilder.withRel("suggestion-create"));
        links.add(Link.of("/docs/index.html#resources-suggestion-getOne", "profile"));
        return EntityModel.of(suggestionResponse, links);
    }

    public static EntityModel<SuggestionResponse> updateSuggestionLink(SuggestionResponse suggestionResponse) {
        List<Link> links = getSelfLink(suggestionResponse.getId());
        links.add(selfLinkBuilder.withRel("suggestion-create"));
        links.add(selfLinkBuilder.slash(suggestionResponse.getId()).withRel("suggestion-getOne"));
        links.add(selfLinkBuilder.slash(suggestionResponse.getId()).withRel("suggestion-delete"));
        links.add(Link.of("/docs/index.html#resources-suggestion-update", "profile"));
        return EntityModel.of(suggestionResponse, links);
    }

    public static EntityModel<SuggestionDeleteResponse> deleteSuggestionLink(SuggestionDeleteResponse suggestionDeleteResponse, Long id) {
        List<Link> links = getSelfLink(id);
        links.add(selfLinkBuilder.withRel("suggestion-create"));
        links.add(Link.of("/docs/index.html#resources-suggestion-delete", "profile"));
        return EntityModel.of(suggestionDeleteResponse, links);
    }

    public static CollectionModel<EntityModel<SuggestionResponse>> getSuggestionListConfirmLink(List<SuggestionResponse> responseList, Long clubId, String email) {
        List<Link> links = getSelfLink("confirmList", clubId);

        List<EntityModel<SuggestionResponse>> entityModelList = getListLink(responseList, email);
        links.add(Link.of("/docs/index.html#resources-suggestion-confirmList", "profile"));

        return CollectionModel.of(entityModelList, links);
    }

    public static CollectionModel<EntityModel<SuggestionResponse>> getSuggestionListLink(List<SuggestionResponse> responseList, Long clubId, String email) {
        List<Link> links = getSelfLink("list", clubId);

        List<EntityModel<SuggestionResponse>> entityModelList = getListLink(responseList, email);
        links.add(Link.of("/docs/index.html#resources-suggestion-list", "profile"));

        return CollectionModel.of(entityModelList, links);
    }

    public static EntityModel<SuggestionVoteResponse> getSuggestionVoteLink(SuggestionVoteResponse response, String email, Long suggestionId) {
        List<Link> links = getSelfLink("vote", suggestionId);
        links.add(Link.of("/docs/index.html#resources-suggestion-vote", "profile"));

        return EntityModel.of(response, links);
    }

    private static List<EntityModel<SuggestionResponse>> getListLink(List<SuggestionResponse> responseList, String email) {
        return responseList.stream()
                .map(response -> {
                    if (response.getMemberEmail().equals(email)) {
                        return EntityModel.of(response,
                                selfLinkBuilder.withRel("suggestion-create"),
                                selfLinkBuilder.slash(response.getId()).withRel("suggestion-getOne"),
                                selfLinkBuilder.slash(response.getId()).withRel("suggestion-update"),
                                selfLinkBuilder.slash(response.getId()).withRel("suggestion-delete"));
                    }
                    return EntityModel.of(response,
                            selfLinkBuilder.withRel("suggestion-create"),
                            selfLinkBuilder.slash(response.getId()).withRel("suggestion-getOne"));
                }).collect(Collectors.toList());
    }

    public static EntityModel<SuggestionVoteUpdateResponse> updateVoteLink(SuggestionVoteUpdateResponse voteUpdateResponse) {
        List<Link> links = getSelfLink(voteUpdateResponse.getId());
        links.add(selfLinkBuilder.withRel("suggestion-vote"));
        links.add(Link.of("/docs/index.html#resources-vote-update", "profile"));
        return EntityModel.of(voteUpdateResponse, links);
    }

    private static List<Link> getSelfLink() {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.withSelfRel());
        return links;
    }

    private static List<Link> getSelfLink(Long suggestionId) {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.slash(suggestionId).withSelfRel());
        return links;
    }

    private static List<Link> getSelfLink(String slashNext, Long clubId) {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.slash(slashNext).slash(clubId).withSelfRel());
        return links;
    }

    public static URI getCreatedUri(Long suggestionId) {
        return selfLinkBuilder.slash(suggestionId).toUri();
    }


}
