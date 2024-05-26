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
public class PreScorePageQuery {
    @NotNull
    private int pageSize;
    @NotNull
    private int pageNo;

    private String name;
    private String group;// 组别
    private String zone;// 赛区
    private Integer session;// 届数
    private Integer ranking;// 排名
}