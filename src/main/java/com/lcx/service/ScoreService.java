package com.lcx.service;

import com.lcx.common.result.PageResult;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.DTO.StudentScorePageQuery;
import com.lcx.pojo.VO.PreScoreVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScoreService {

    void addPreScore(String group, String zone);

    PageResult queryPreScore(PreScorePageQuery preScorePageQuery);

    List<PreScoreVO> queryMyPreScore();

    void calculateFinalScore(String group, String zone);

    PageResult pageQueryStudentScore(StudentScorePageQuery studentScorePageQuery);

    @Transactional
    void deleteWrittenScore(String group, String zone);

    @Transactional
    void addStudentScore(String group, String zone);

    @Transactional
    void deleteScore(String group, String zone);
}
