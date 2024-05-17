package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistrictScore {
    private int id;
    private int uid;
    private String seatNum;// 座位号
    private String signNum;// 签号
    private int writtenScore;// 比试成绩
    private int practiceScore;// 实战成绩
    private int qAndAScore;// 快问快打成绩
    private int finalScore;// 最终成绩
}
