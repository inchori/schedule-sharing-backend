package com.schedulsharing.web.schedule.club.dto.suggestion;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteAgreeDto {
    private int count;
    private List<String> memberName;
}
