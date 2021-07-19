package com.schedulsharing.web.dto.resource;

import com.schedulsharing.web.schedule.club.ClubScheduleController;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleCreateResponse;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleDeleteResponse;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleResponse;
import com.schedulsharing.web.schedule.club.dto.ClubScheduleUpdateResponse;
import com.schedulsharing.domain.schedule.ClubSchedule;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ClubScheduleResource extends EntityModel<ClubSchedule> {
    private static WebMvcLinkBuilder selfLinkBuilder = linkTo(ClubScheduleController.class);

    public static EntityModel<ClubScheduleCreateResponse> createClubScheduleLink(ClubScheduleCreateResponse createResponse) {
        List<Link> links = getSelfLink();
        links.add(selfLinkBuilder.slash(createResponse.getId()).withRel("clubSchedule-getOne"));
        links.add(selfLinkBuilder.slash(createResponse.getId()).withRel("clubSchedule-update"));
        links.add(selfLinkBuilder.slash(createResponse.getId()).withRel("clubSchedule-delete"));
        links.add(Link.of("/docs/index.html#resources-clubSchedule-create", "profile"));
        return EntityModel.of(createResponse, links);
    }

    public static EntityModel<ClubScheduleResponse> getClubScheduleLink(ClubScheduleResponse clubScheduleResponse, String email) {
        List<Link> links = getSelfLink(clubScheduleResponse.getId());
        links.add(selfLinkBuilder.withRel("clubSchedule-create"));
        if (clubScheduleResponse.getMemberEmail().equals(email)) {
            links.add(selfLinkBuilder.slash(clubScheduleResponse.getId()).withRel("clubSchedule-update"));
            links.add(selfLinkBuilder.slash(clubScheduleResponse.getId()).withRel("clubSchedule-delete"));
        }
        links.add(Link.of("/docs/index.html#resources-clubSchedule-getOne", "profile"));
        return EntityModel.of(clubScheduleResponse, links);
    }


    public static EntityModel<ClubScheduleUpdateResponse> updateClubScheduleLink(ClubScheduleUpdateResponse response) {
        List<Link> links = getSelfLink(response.getId());
        links.add(selfLinkBuilder.withRel("clubSchedule-create"));
        links.add(selfLinkBuilder.slash(response.getId()).withRel("clubSchedule-getOne"));
        links.add(selfLinkBuilder.slash(response.getId()).withRel("clubSchedule-delete"));
        links.add(Link.of("/docs/index.html#resources-clubSchedule-update", "profile"));
        return EntityModel.of(response, links);
    }

    public static EntityModel<ClubScheduleDeleteResponse> deleteClubScheduleLink(Long id, ClubScheduleDeleteResponse clubScheduleDeleteResponse) {
        List<Link> links = getSelfLink(id);
        links.add(selfLinkBuilder.withRel("clubSchedule-create"));
        links.add(Link.of("/docs/index.html#resources-clubSchedule-delete", "profile"));

        return EntityModel.of(clubScheduleDeleteResponse, links);
    }

    public static CollectionModel<EntityModel<ClubScheduleResponse>> getClubScheduleListLink(List<ClubScheduleResponse> responseList, Long clubId, String email) {
        List<Link> links = getSelfLink("list", clubId);

        List<EntityModel<ClubScheduleResponse>> entityModelList = responseList.stream()
                .map(response -> {
                    if (response.getMemberEmail().equals(email)) {
                        return EntityModel.of(response,
                                selfLinkBuilder.withRel("clubSchedule-create"),
                                selfLinkBuilder.slash(response.getId()).withRel("clubSchedule-getOne"),
                                selfLinkBuilder.slash(response.getId()).withRel("clubSchedule-update"),
                                selfLinkBuilder.slash(response.getId()).withRel("clubSchedule-delete"));
                    }
                    return EntityModel.of(response,
                            selfLinkBuilder.withRel("clubSchedule-create"),
                            selfLinkBuilder.slash(response.getId()).withRel("clubSchedule-getOne"));
                }).collect(Collectors.toList());
        links.add(Link.of("/docs/index.html#resources-clubSchedule-list", "profile"));

        return CollectionModel.of(entityModelList, links);
    }

    private static List<Link> getSelfLink(String slashNext, Long clubId) {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.slash(slashNext).slash(clubId).withSelfRel());
        return links;
    }

    private static List<Link> getSelfLink() {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.withSelfRel());
        return links;
    }

    private static List<Link> getSelfLink(Long clubScheduleId) {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.slash(clubScheduleId).withSelfRel());
        return links;
    }

    public static URI getCreatedUri(Long clubScheduleId) {
        return selfLinkBuilder.slash(clubScheduleId).toUri();
    }

}