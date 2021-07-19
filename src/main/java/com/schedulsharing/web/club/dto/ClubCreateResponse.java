package com.schedulsharing.web.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubCreateResponse {
    private Long clubId;
    private String clubName;
    private String categories;
    private Long leaderId;
}
