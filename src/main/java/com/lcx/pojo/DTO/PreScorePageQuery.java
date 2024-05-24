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
    private int session;// 届数
    private String group;// 组别
    private String zone;// 赛区
    private int ranking;// 排名
}