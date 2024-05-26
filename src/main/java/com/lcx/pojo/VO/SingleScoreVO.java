package com.lcx.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleScoreVO {

    private String name; //姓名
    private String group; // 组别
    private String zone; // 赛区
    private String seatNum; // 座位号
    private int score; // 分数
    private int ranking;// 排名

}
