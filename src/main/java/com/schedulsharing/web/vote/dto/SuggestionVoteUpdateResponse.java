package com.schedulsharing.web.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionVoteUpdateResponse {
    private Long id;
    private boolean agree;
}
