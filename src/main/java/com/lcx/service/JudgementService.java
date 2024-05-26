package com.lcx.service;

import com.lcx.pojo.DTO.ScoreDTO;
import com.lcx.pojo.Entity.Student;
import com.lcx.pojo.VO.SignGroup;

public interface JudgementService {

    SignGroup getSignGroup(int signNum);

    Student getContestant(int num);

    void practiceRate(ScoreDTO scoreDTO);

    void qAndARate(ScoreDTO scoreDTO);
}
