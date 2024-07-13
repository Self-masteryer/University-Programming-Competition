package com.lcx.service;


import com.lcx.domain.DTO.ScoreDTO;
import com.lcx.domain.Entity.Student;
import com.lcx.domain.VO.SignGroup;

public interface JudgementService {

    SignGroup getSignGroup(int signNum);

    Student getContestant(int num);

    void practiceRate(ScoreDTO scoreDTO);

    void qAndARate(ScoreDTO scoreDTO);
}
