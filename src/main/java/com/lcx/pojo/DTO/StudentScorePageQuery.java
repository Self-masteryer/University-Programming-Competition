package com.lcx.pojo.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentScorePageQuery {
    @NotNull
    private int pageSize;
    @NotNull
    private int pageNo;

    private String name;
    private String school;
    private Integer session;
    private String prize;

}
