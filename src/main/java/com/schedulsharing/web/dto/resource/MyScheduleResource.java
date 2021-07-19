package com.schedulsharing.web.dto.resource;

import com.schedulsharing.web.schedule.my.MyScheduleController;
import com.schedulsharing.web.schedule.my.dto.MyScheduleCreateResponse;
import com.schedulsharing.web.schedule.my.dto.MyScheduleDeleteResponse;
import com.schedulsharing.web.schedule.my.dto.MyScheduleResponse;
import com.schedulsharing.web.schedule.my.dto.MyScheduleUpdateResponse;
import com.schedulsharing.domain.schedule.MySchedule;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class MyScheduleResource extends EntityModel<MySchedule> {
    private static WebMvcLinkBuilder selfLinkBuilder = linkTo(MyScheduleController.class);

    public static EntityModel<MyScheduleCreateResponse> createMyScheduleLink(MyScheduleCreateResponse createResponse) {
        List<Link> links = getSelfLink();
        links.add(selfLinkBuilder.slash(createResponse.getMyScheduleId()).withRel("mySchedule-getOne"));
        links.add(selfLinkBuilder.slash(createResponse.getMyScheduleId()).withRel("mySchedule-update"));
        links.add(selfLinkBuilder.slash(createResponse.getMyScheduleId()).withRel("mySchedule-delete"));
        links.add(Link.of("/docs/index.html#resources-mySchedule-create", "profile"));
        return EntityModel.of(createResponse, links);
    }

    public static EntityModel<MyScheduleResponse> getMyScheduleLink(MyScheduleResponse myScheduleResponse) {
        List<Link> links = getSelfLink(myScheduleResponse.getMyScheduleId());
        links.add(selfLinkBuilder.withRel("mySchedule-create"));
        links.add(selfLinkBuilder.slash(myScheduleResponse.getMyScheduleId()).withRel("mySchedule-update"));
        links.add(selfLinkBuilder.slash(myScheduleResponse.getMyScheduleId()).withRel("mySchedule-delete"));
        links.add(Link.of("/docs/index.html#resources-mySchedule-getOne", "profile"));
        return EntityModel.of(myScheduleResponse, links);
    }

    public static CollectionModel<EntityModel<MyScheduleResponse>> getMyScheduleListLink(List<MyScheduleResponse> myScheduleList, String email) {
        List<Link> links = getSelfLink("list", email);
        List<EntityModel<MyScheduleResponse>> entityModelList = myScheduleList.stream()
                .map(response -> {
                    return EntityModel.of(response,
                                selfLinkBuilder.withRel("mySchedule-create"),
                                selfLinkBuilder.slash(response.getMyScheduleId()).withRel("mySchedule-getOne"),
                                selfLinkBuilder.slash(response.getMyScheduleId()).withRel("mySchedule-update"),
                                selfLinkBuilder.slash(response.getMyScheduleId()).withRel("mySchedule-delete"));
                }).collect(Collectors.toList());
        links.add(Link.of("/docs/index.html#resources-mySchedule-list", "profile"));

        return CollectionModel.of(entityModelList, links);
    }

    public static EntityModel<MyScheduleUpdateResponse> updateMyScheduleLink(MyScheduleUpdateResponse updateResponse) {
        List<Link> links = getSelfLink(updateResponse.getMyScheduleId());
        links.add(selfLinkBuilder.withRel("mySchedule-create"));
        links.add(selfLinkBuilder.slash(updateResponse.getMyScheduleId()).withRel("mySchedule-delete"));
        links.add(selfLinkBuilder.slash(updateResponse.getMyScheduleId()).withRel("mySchedule-getOne"));
        links.add(Link.of("/docs/index.html#resources-mySchedule-update", "profile"));
        return EntityModel.of(updateResponse, links);
    }

    public static EntityModel<MyScheduleDeleteResponse> deleteMyScheduleLink(Long myScheuldeId, MyScheduleDeleteResponse deleteResponse) {
        List<Link> links = getSelfLink(myScheuldeId);
        links.add(selfLinkBuilder.withRel("mySchedule-create"));
        links.add(Link.of("/docs/index.html#resources-mySchedule-delete", "profile"));
        return EntityModel.of(deleteResponse, links);
    }


    private static List<Link> getSelfLink(String slashNext, String email) {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.slash(slashNext).slash(email).withSelfRel());
        return links;
    }

    private static List<Link> getSelfLink(Long myScheduleId) {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.slash(myScheduleId).withSelfRel());
        return links;
    }

    private static List<Link> getSelfLink() {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.withSelfRel());
        return links;
    }

    public static URI getCreatedUri(Long myScheduleId) {
        return selfLinkBuilder.slash(myScheduleId).toUri();
    }



}
