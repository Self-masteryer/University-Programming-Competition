package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class School {

    private int id;
    private int uid;
    private String name;
    //本届参赛选手人数
    private int num;
    private String group;
    private String zone;

}
