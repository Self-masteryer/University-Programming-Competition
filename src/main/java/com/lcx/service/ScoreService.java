package com.lcx.service;

import com.lcx.common.result.PageResult;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.VO.PreScoreVO;

import java.util.List;

public interface ScoreService {

    void addPreScore(String group, String zone);

    PageResult queryPreScore(PreScorePageQuery preScorePageQuery);

    List<PreScoreVO> queryMyPreScore();

}
