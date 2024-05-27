package com.lcx.service;

import com.lcx.common.result.PageResult;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.DTO.StudentScorePageQuery;
import com.lcx.pojo.VO.PreScoreVO;
import com.lcx.pojo.VO.SingleScoreVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScoreService {

    void addPreScore(String group, String zone);

    PageResult queryPreScore(PreScorePageQuery preScorePageQuery);

    List<PreScoreVO> queryMyPreScore();

    void calculateFinalScore(String group, String zone);

    PageResult pageQueryStudentScore(StudentScorePageQuery studentScorePageQuery);

    void deleteWrittenScore(String group, String zone);

    void addStudentScore(String group, String zone);

    void deleteScore(String group, String zone);

    SingleScoreVO getWrittenScore(int uid);
}
