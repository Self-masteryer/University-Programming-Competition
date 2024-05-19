package com.lcx.service;

import com.lcx.pojo.DTO.ScoreDTO;
import com.lcx.pojo.VO.SignGroup;

public interface JudgementService {

    SignGroup getSignGroup(int signNum);

    void rate(ScoreDTO scoreDTO,String process);

}
