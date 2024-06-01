package com.lcx.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreInfoQuery {

    private String contestantName;
    private String judgementName;
    private String group;
    private String zone;

}
