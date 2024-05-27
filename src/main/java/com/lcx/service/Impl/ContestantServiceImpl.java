package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.Zone;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.ScoreInfoMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.mapper.WrittenScoreMapper;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.VO.ScoreVO;
import com.lcx.pojo.Entity.SingleScore;
import com.lcx.pojo.VO.SingleScoreVO;
import com.lcx.service.ContestantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ContestantServiceImpl implements ContestantService {
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ScoreInfoMapper scoreInfoMapper;

    @Override
    public void addToNational(String group, String zone) {
        List<ScoreVO> scoreInfoList = contestantMapper
                .getScoreVoListByGroupAndZone(group, zone);
        scoreInfoList.sort(Comparator.comparingDouble(ScoreVO::getFinalScore).reversed());

        for (int i = 0; i < 5; i++) {
            ScoreVO scoreVo = scoreInfoList.get(i);
            Contestant contestant = Contestant.builder().uid(userInfoMapper.getUidByIDCard(scoreVo.getIdCard()))
                    .name(scoreVo.getName()).school(scoreVo.getSchool()).idCard(scoreVo.getIdCard())
                    .group(scoreVo.getGroup()).zone(Zone.N).build();

            contestantMapper.insert(contestant);
        }
    }

    @Override
    public String getSeatNum() {
        return scoreInfoMapper.getSeatNum(StpUtil.getLoginIdAsInt());
    }

    @Override
    public String getSignNum() {
        return scoreInfoMapper.getSignNum(StpUtil.getLoginIdAsInt());
    }

}
