package com.schedulsharing.dto.Club;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubUpdateRequest {
    @NotEmpty
    private String clubName;
    @NotEmpty
    private String categories;
}
