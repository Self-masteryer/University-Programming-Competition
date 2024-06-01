package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.Zone;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.ScoreInfoMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.VO.GrageVO;
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

    // 添加国赛选手
    @Override
    public void addToNational(String group, String zone) {
        List<GrageVO> scoreInfoList = contestantMapper
                .getScoreVoListByGroupAndZone(group, zone);
        scoreInfoList.sort(Comparator.comparingDouble(GrageVO::getFinalScore).reversed());

        for (int i = 0; i < 5; i++) {
            GrageVO grageVo = scoreInfoList.get(i);
            Contestant contestant = Contestant.builder().uid(userInfoMapper.getUidByIDCard(grageVo.getIdCard()))
                    .name(grageVo.getName()).school(grageVo.getSchool()).idCard(grageVo.getIdCard())
                    .group(grageVo.getGroup()).zone(Zone.N).build();

            contestantMapper.insert(contestant);
        }
    }

    // 获得座位号
    @Override
    public String getSeatNum() {
        return scoreInfoMapper.getSeatNum(StpUtil.getLoginIdAsInt());
    }

    // 获得组号
    @Override
    public String getSignNum() {
        return scoreInfoMapper.getSignNum(StpUtil.getLoginIdAsInt());
    }

}
