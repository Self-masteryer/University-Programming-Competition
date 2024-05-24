package com.lcx.service.Impl;

import com.lcx.common.constant.Zone;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.PreScoreMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.VO.ScoreVo;
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

    @Override
    public void addToNational(String group, String zone) {
        List<ScoreVo> scoreInfoList = contestantMapper
                .getScoreVoListByGroupAndZone(group, zone);
        scoreInfoList.sort(Comparator.comparingDouble(ScoreVo::getFinalScore).reversed());

        for (int i = 0; i < 5; i++) {
            ScoreVo scoreVo = scoreInfoList.get(i);
            Contestant contestant = Contestant.builder().uid(userInfoMapper.getUidByIDCard(scoreVo.getIdCard()))
                    .name(scoreVo.getName()).school(scoreVo.getSchool()).idCard(scoreVo.getIdCard())
                    .group(scoreVo.getGroup()).zone(Zone.N).build();

            contestantMapper.insert(contestant);
        }
    }
}
