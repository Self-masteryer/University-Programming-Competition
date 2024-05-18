package com.lcx.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistrictScoreVO {

    private String name; //姓名
    private String group; // 组别
    private String zone; // 赛区
    private String seatNum; // 座位号 （唯一标识）
    private String signNum; // 签号
    private int writtenScore; // 笔试成绩
    private int practiceScore; // 实战成绩
    private int qAndAScore; // 快问快打成绩
    private int finalScore; // 最终成绩

}
