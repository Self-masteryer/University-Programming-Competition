package com.lcx.service;


import com.lcx.common.result.PageResult;

import com.lcx.domain.DTO.ScoreInfoQuery;
import com.lcx.domain.DTO.ScoreQuery;
import com.lcx.domain.DTO.StatusPageQuery;
import com.lcx.domain.DTO.TimePeriod;
import com.lcx.domain.VO.FinalSingleScore;
import com.lcx.domain.VO.ProcessVO;
import com.lcx.domain.VO.SingeScoreInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    void addUserByExcel(MultipartFile file, HttpServletResponse response);

    void addSchoolByExcel(MultipartFile file, HttpServletResponse response);

    void setSignUpTime(TimePeriod timePeriod);

    void startNationalCompetition();

    void setAsTourist(String group, String zone);

    List<ProcessVO> queryProcess(String group, String zone);

    PageResult queryStatus(StatusPageQuery statusPageQuery);

    List<SingeScoreInfo> queryPracticalScoreInfo(ScoreInfoQuery scoreInfoQuery);

    List<FinalSingleScore> queryPracticalScore(ScoreQuery scoreQuery);

    List<SingeScoreInfo> queryqAndAScoreInfo(ScoreInfoQuery scoreInfoQuery);

    List<FinalSingleScore> queryqAndAScore(ScoreQuery scoreQuery);

    void setWaiverNatQualTime(TimePeriod timePeriod);
}
