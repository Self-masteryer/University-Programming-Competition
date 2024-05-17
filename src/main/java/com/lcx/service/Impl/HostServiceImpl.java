package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Time;
import com.lcx.common.exception.StartTimeException;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.Entity.DistrictScore;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.service.HostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HostServiceImpl implements HostService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ContestantMapper contestantMapper;

    //开启比赛,并抽签
    @Override
    public void startAndDraw() {
        //判断报名是否已结束
        String instantStr = stringRedisTemplate.opsForValue().get(Time.SIGN_UP_END_TIME);
        if (instantStr == null) throw new StartTimeException(ErrorMessageConstant.START_TIME_ERROR);
        long end = Long.parseLong(instantStr);
        long now = System.currentTimeMillis();
        if (now < end) throw new StartTimeException(ErrorMessageConstant.START_TIME_ERROR);

        //获得组别、赛区信息
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        String group = userInfo.getGroup();
        String zone = userInfo.getZone();
        log.info("{}:{} 开启比赛,并抽签", group, zone);
        //将比赛进程存进redis
        String key = "process" + ":" + group + ":" + zone;
        stringRedisTemplate.opsForValue().set(key, Process.WRITTEN);
        //随机获得座位号
        int count = contestantMapper.getCountByGroupAndZone(group,zone);
        List<Integer> nums = new ArrayList<>();
        for(int i=1;i<=count;i++) nums.add(i);
        Collections.shuffle(nums);

        List<Contestant> list = contestantMapper.getListByGroupAndZone(group,zone);
        for(int i=0;i<count;i++) {
            Contestant contestant = list.get(i);
            DistrictScore districtScore = DistrictScore.builder().uid(contestant.getUid()).build();
            String seatNum = group+zone+nums.get(i);
            districtScore.setSeatNum(seatNum);
        }
    }
}
