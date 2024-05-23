package com.lcx.pojo.DTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreDTO {

    private int uid;
    @Pattern(regexp = "^(100|[0-9]|[1-9][0-9])$")
    private String score;

}
