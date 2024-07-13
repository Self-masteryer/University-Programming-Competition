package com.lcx.service.Impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessage;
import com.lcx.common.constant.Group;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Time;
import com.lcx.common.constant.Zone;
import com.lcx.common.exception.RoleVerificationException;
import com.lcx.common.exception.process.ProcessStatusException;
import com.lcx.common.exception.time.TimePeriodErrorException;
import com.lcx.common.utils.RedisUtil;
import com.lcx.domain.Entity.Contestant;
import com.lcx.domain.Entity.UserInfo;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.PreScoreMapper;
import com.lcx.mapper.ScoreInfoMapper;
import com.lcx.mapper.UserInfoMapper;

import com.lcx.service.ContestantService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ContestantServiceImpl implements ContestantService {
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ScoreInfoMapper scoreInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PreScoreMapper preScoreMapper;

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

    // 放弃国赛资格
    @Override
    @Transactional
    public void waiverNatCompQual() {
        // 检验区赛是否结束
        SaSession saSession = StpUtil.getSession();
        String key=RedisUtil.getProcessKey(saSession.getString(Group.GROUP),saSession.getString(Zone.ZONE));
        if(!Process.PROCESS_STEP[10].equals(stringRedisTemplate.opsForValue().get(key)))
            throw new ProcessStatusException(ErrorMessage.PROCESS_STATUS_ERROR);

        // 校验是否为国赛选手
        Contestant contestant = contestantMapper.getByUid(StpUtil.getLoginIdAsInt());
        if(contestant==null)
            throw new RoleVerificationException(ErrorMessage.ROLE_VERIFICATION_EXCEPTION);

        // 校验是否在规定的时间内
        String beginTime = stringRedisTemplate.opsForValue().get(Time.WAIVER_NAT_QUAL_BEGIN_TIME);
        if (beginTime == null)
            throw new TimePeriodErrorException(ErrorMessage.TIME_ERROR);
        long begin = Long.parseLong(Objects.requireNonNull(beginTime));
        long end = Long.parseLong(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(Time.WAIVER_NAT_QUAL_END_TIME)));
        long now = System.currentTimeMillis();
        // 报名时间异常
        if (now < begin || now > end)
            throw new TimePeriodErrorException(ErrorMessage.TIME_ERROR);

        // 删除选手信息
        contestantMapper.deleteByUid(contestant.getUid());
        UserInfo userInfo = UserInfo.builder().uid(contestant.getUid()).group("").zone("").build();
        userInfoMapper.update(userInfo);

        // 从redis中获得顺延递补的选手排名
        key = RedisUtil.getNextNatContestantKey(contestant.getGroup(), contestant.getZone());
        String next = stringRedisTemplate.opsForValue().get(key)
                ==null?"6":stringRedisTemplate.opsForValue().get(key);
        //  保存下一名排名序号
        stringRedisTemplate.opsForValue().set(key, RedisUtil.stringNumAddOne(next));

        // 添加下一位选手信息
        String session=stringRedisTemplate.opsForValue().get(RedisUtil.SESSION);
        contestant=preScoreMapper.getContestant(session,contestant.getGroup(),contestant.getZone(),next);
        contestantMapper.insert(contestant);
    }

    // 删除未晋级选手
    @Override
    @Transactional
    public void deleteContestant(String group, String zone) {
        List<Integer> uidList = preScoreMapper.getUnqualUidList(group,zone);
        for (Integer uid : uidList)
            contestantMapper.deleteByUid(uid);
    }

}
