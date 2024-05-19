package com.lcx.service;

import com.lcx.pojo.DTO.PracticalScoreDTO;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.pojo.VO.SignInfo;

public interface JudgementService {

    void rate(PracticalScoreDTO practicalScoreDTO);

    SignGroup getSignGroup(int signNum);

}
