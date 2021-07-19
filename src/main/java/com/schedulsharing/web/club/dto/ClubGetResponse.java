package com.schedulsharing.web.club.dto;

import lombok.Data;

@Data
public class ClubGetResponse {
    private Long clubId;
    private String clubName;
    private String categories;
    private Long leaderId;
}
