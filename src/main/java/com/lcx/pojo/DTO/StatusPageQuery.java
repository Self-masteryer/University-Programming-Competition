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
public class StatusPageQuery {

    @NotNull
    private int pageSize;
    @NotNull
    private int pageNo;

    private String name;// 真实姓名
    private int rid;// 角色
    private String group;// 组别
    private String zone;// 赛区

}
