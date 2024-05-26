package com.lcx.service.Impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.exception.RateException;
import com.lcx.common.exception.SignNumOutOfBoundException;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.*;
import com.lcx.pojo.DTO.ScoreDTO;
import com.lcx.pojo.Entity.*;
import com.lcx.pojo.VO.ScoreVO;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.service.JudgementService;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgementServiceImpl implements JudgementService {

    @Resource
    private ScoreInfoMapper scoreInfoMapper;
    @Resource
    private PracticalScoreMapper practicalScoreMapper;
    @Resource
    private QAndAScoreMapper qAndAScoreMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ScoreService scoreService;

    @Override
    @Transactional
    public SignGroup getSignGroup(int signNum) {
        if (signNum < 1 || signNum > 15)
            throw new SignNumOutOfBoundException(ErrorMessageConstant.SIGN_NUM_OUT_OF_BOUND);

        SaSession session = StpUtil.getSession();
        String key = RedisUtil.getSignGroupsKey(session.getString(Group.GROUP), session.getString(Zone.ZONE));
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        List<SignGroup> signGroups = JSON.parseArray(jsonStr, SignGroup.class);

        return signGroups.get(signNum - 1);
    }

    @Override
    @Transactional
    public Student getContestant(int num) {
        if (num < 1 || num > 30)
            throw new SignNumOutOfBoundException(ErrorMessageConstant.SIGN_NUM_OUT_OF_BOUND);

        SaSession session = StpUtil.getSession();
        String key = RedisUtil.getSignsKey(session.getString(Group.GROUP), session.getString(Zone.ZONE));
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        List<Student> students = JSON.parseArray(jsonStr, Student.class);

        return students.get(num - 1);
    }

    @Override
    @Transactional
    public void practiceRate(ScoreDTO scoreDTO) {
        int uid = scoreDTO.getUid();
        int jid = StpUtil.getLoginIdAsInt();
        // 检验是否重复打分
        int time = practicalScoreMapper.checkTime(uid, jid);
        if (time != 0) throw new RateException(ErrorMessageConstant.REPEATED_RATE);

        String group = StpUtil.getSession().getString(Group.GROUP);
        String zone = StpUtil.getSession().getString(Zone.ZONE);

        // 分数
        Score score = Score.builder().uid(uid)
                .sid(scoreInfoMapper.getId(uid)).jid(jid)
                .score(Integer.parseInt(scoreDTO.getScore())).build();
        practicalScoreMapper.insert(score);

        synchronized (JudgementServiceImpl.class) {

            // 已打分评委数量加一
            String key = RedisUtil.getRateTimesKey(uid);// 几位评委已打分
            String times = RedisUtil.stringNumAddOne(stringRedisTemplate.opsForValue().get(key));
            stringRedisTemplate.opsForValue().set(key, times);

            // 判断五位评委是否打分完毕
            if (times.equals("5")) {
                List<Float> scores = practicalScoreMapper.getScoresByUid(uid);
                float sum = 0;
                for (Float s : scores) sum += s;
                // 插入平均分
                scoreInfoMapper.updatePracticalScoreByUid(uid, sum / 5);

                // 删除rateTimes
                stringRedisTemplate.delete(key);

                // 已打分选手数量加一
                key = RedisUtil.getRateNumsKey(group, zone);// 几位选手已打分
                String nums = RedisUtil.stringNumAddOne(stringRedisTemplate.opsForValue().get(key));
                stringRedisTemplate.opsForValue().set(key, nums);

                //判断30位选手是否全部打分完毕
                if (nums.equals("30")) {
                    // 清除rateNums
                    stringRedisTemplate.delete(key);

                    // 推进流程
                    key = RedisUtil.getProcessKey(group, zone);
                    String value = RedisUtil.getProcessValue(Process.PRACTICE, Step.NEXT);
                    stringRedisTemplate.opsForValue().set(key, value);

                    // 删除signGroups
                    key = RedisUtil.getSignGroupsKey(group, zone);
                    String json = stringRedisTemplate.opsForValue().get(key);
                    // 保存signGroups数据
                    List<SignGroup> signGroups = JSON.parseArray(json, SignGroup.class);
                    stringRedisTemplate.delete(key);

                    // 存储signs
                    List<Student> signs = new ArrayList<>();
                    for (SignGroup signGroup : signGroups) {
                        signs.add(signGroup.getA());
                        signs.add(signGroup.getB());
                    }
                    key = RedisUtil.getSignsKey(group, zone);
                    stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(signs));
                }
            }
        }
    }

    @Override
    @Transactional
    public void qAndARate(ScoreDTO scoreDTO) {

        int uid = scoreDTO.getUid();
        int jid = StpUtil.getLoginIdAsInt();
        // 检验是否重复打分
        int time = qAndAScoreMapper.checkTime(uid, jid);
        if (time != 0) throw new RateException(ErrorMessageConstant.REPEATED_RATE);

        String group = StpUtil.getSession().getString(Group.GROUP);
        String zone = StpUtil.getSession().getString(Zone.ZONE);

        // 插入实战成绩
        Score score = Score.builder().uid(uid)
                .sid(scoreInfoMapper.getId(uid)).jid(jid)
                .score(Integer.parseInt(scoreDTO.getScore())).build();
        qAndAScoreMapper.insert(score);

        synchronized (JudgementServiceImpl.class) {

            // 已打分评委数量加一
            String key = RedisUtil.getRateTimesKey(uid);// 几位评委已打分
            String times = RedisUtil.stringNumAddOne(stringRedisTemplate.opsForValue().get(key));
            stringRedisTemplate.opsForValue().set(key, times);

            // 判断五位评委是否打分完毕
            if (times.equals("5")) {
                List<Float> scores = qAndAScoreMapper.getScoresByUid(uid);
                float sum = 0;
                for (Float s : scores) sum += s;
                // 插入平均分
                scoreInfoMapper.updateQAndAScoreByUid(uid, sum / 5);

                // 删除rateTimes
                stringRedisTemplate.delete(key);

                // 已打分选手数量加一
                key = RedisUtil.getRateNumsKey(group, zone);// 几位选手已打分
                String nums = RedisUtil.stringNumAddOne(stringRedisTemplate.opsForValue().get(key));
                stringRedisTemplate.opsForValue().set(key, nums);

                //判断30位选手是否全部打分完毕
                if (nums.equals("30")) {
                    // 删除rateNums
                    stringRedisTemplate.delete(key);

                    // 推进流程
                    key = RedisUtil.getProcessKey(group, zone);
                    String value = RedisUtil.getProcessValue(Process.Q_AND_A, Step.NEXT);
                    stringRedisTemplate.opsForValue().set(key, value);

                    // 删除signs
                    key = RedisUtil.getSignsKey(group, zone);
                    stringRedisTemplate.delete(key);

                    // 计算总分
                    scoreService.calculateFinalScore(group,zone);
                }
            }
        }
    }

}