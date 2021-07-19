package com.schedulsharing.web.schedule.club.dto.suggestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionDeleteResponse {
    private boolean success;
    private String message;
}
