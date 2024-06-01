package com.lcx.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingeScoreInfo {

    private int signNum;
    private int uid;// 选手id
    private String contestantName;
    private int jid;
    private String judgementName;
    private int score;
    private String group;
    private String zone;

}
