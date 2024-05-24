package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lcx.common.result.PageResult;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.PreScoreMapper;
import com.lcx.mapper.ScoreInfoMapper;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.Entity.PreScore;
import com.lcx.pojo.Entity.ScoreInfo;
import com.lcx.pojo.VO.PreScoreVO;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {
    @Resource
    private PreScoreMapper preScoreMapper;
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private ScoreInfoMapper scoreInfoMapper;

    @Override
    public void addPreScore(String group, String zone) {
        List<Integer> uidList = contestantMapper.getUidListByGroupAndZone(group, zone);
        List<ScoreInfo> scoreInfoList = new ArrayList<>();
        for (Integer uid : uidList) scoreInfoList.add(scoreInfoMapper.getByUid(uid));
        scoreInfoList.sort(Comparator.comparingDouble(ScoreInfo::getFinalScore).reversed());
        for (int i = 0; i < scoreInfoList.size(); i++) {
            ScoreInfo scoreInfo = scoreInfoList.get(i);
            PreScore preScore = new PreScore();
            BeanUtils.copyProperties(scoreInfo, preScore);
            preScore.setRanking(i + 1);

            preScoreMapper.insert(preScore);
        }
    }

    @Override
    public PageResult queryPreScore(PreScorePageQuery preScorePageQuery) {
        PageHelper.startPage(preScorePageQuery.getPageNo(), preScorePageQuery.getPageSize());
        Page<PreScoreVO> page = preScoreMapper.pageQuery(preScorePageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<PreScoreVO> queryMyPreScore() {
        return preScoreMapper.getListByUid(StpUtil.getLoginIdAsInt());
    }

}
