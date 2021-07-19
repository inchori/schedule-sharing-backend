package com.schedulsharing.web.dto.resource;

import com.schedulsharing.web.member.AuthController;
import com.schedulsharing.web.member.dto.LoginResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AuthResource {

    private static WebMvcLinkBuilder selfLinkBuilder = linkTo(AuthController.class);

    public static EntityModel<LoginResponseDto> authorizeLink(LoginResponseDto loginResponseDto) {
        List<Link> links = getSelfLink("authenticate");

        links.add(Link.of("/docs/index.html#resources-member-login", "profile"));
        return EntityModel.of(loginResponseDto, links);
    }

    private static List<Link> getSelfLink(String slashNext) {
        List<Link> links = new ArrayList<>();
        links.add(selfLinkBuilder.slash(slashNext).withSelfRel());
        return links;
    }
}
