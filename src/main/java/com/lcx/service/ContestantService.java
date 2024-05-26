package com.lcx.service;


import com.lcx.pojo.VO.SingleScoreVO;

public interface ContestantService {
    void addToNational(String group, String zone);

    String getSeatNum();

    String getSignNum();

    SingleScoreVO getWrittenScore();

}
