package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreScore {

    private int id;
    private int uid;
    private String school;// 学校
    private String session;// 届数
    private String group;// 组别
    private String zone;// 赛区
    private String seatNum;// 座位号
    private String signNum;// 签号
    private float writtenScore;// 笔试成绩
    private float practicalScore;// 实战成绩
    private float qAndAScore;// 快问快打成绩
    private float finalScore;// 最终成绩
    private int ranking;// 排名
}
