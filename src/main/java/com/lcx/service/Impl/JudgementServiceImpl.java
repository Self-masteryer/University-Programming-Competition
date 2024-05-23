package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Step;
import com.lcx.common.constant.Zone;
import com.lcx.common.exception.RateException;
import com.lcx.common.exception.SignNumOutOfBoundException;
import com.lcx.common.exception.process.NoSuchProcessException;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.*;
import com.lcx.pojo.DAO.ScoreDAO;
import com.lcx.pojo.DAO.SignInfoDAO;
import com.lcx.pojo.DTO.ScoreDTO;
import com.lcx.pojo.Entity.*;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.service.JudgementService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class JudgementServiceImpl implements JudgementService {

    @Resource
    private ScoreInfoMapper scoreInfoMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private PracticalScoreMapper practicalScoreMapper;
    @Resource
    private QAndAScoreMapper qAndAScoreMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public SignGroup getSignGroup(int signNum) {
        if (signNum < 1 || signNum > 15)
            throw new SignNumOutOfBoundException(ErrorMessageConstant.SIGN_NUM_OUT_OF_BOUND);
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        //获得分组信息
        List<SignInfoDAO> list = contestantMapper
                .getSignInfoListByGroupAndZone(userInfo.getGroup(), userInfo.getZone());
        list.sort(((o1, o2) -> {
            int signNum1 = Integer.parseInt(o1.getSignNum().substring(1));
            int signNum2 = Integer.parseInt(o2.getSignNum().substring(1));
            return signNum1 - signNum2 == 0 ? o1.getSignNum().charAt(0) - o2.getSignNum().charAt(0) : signNum1 - signNum2;
        }));
        int index = (signNum - 1) * 2;
        Student A = contestantMapper.getStudentByUid(list.get(index).getUid());
        Student B = contestantMapper.getStudentByUid(list.get(index + 1).getUid());
        return SignGroup.builder().A(A).B(B).build();
    }

    @Override
    @Transactional
    public void rate(ScoreDTO scoreDTO, String process) {

        // 获得成绩表ID
        int uid = scoreDTO.getUid();
        ScoreInfo scoreInfo = scoreInfoMapper.getByUid(uid);

        // 分数
        int jid = StpUtil.getLoginIdAsInt();
        Score score = Score.builder().uid(scoreDTO.getUid())
                .sid(scoreInfo.getId()).jid(jid)
                .score(Integer.parseInt(scoreDTO.getScore())).build();

        if (process.equals(Process.PRACTICE)) {

            // 检验是否重复打分
            int time = practicalScoreMapper.checkTime(uid, jid);
            if (time != 0) throw new RateException(ErrorMessageConstant.REPEATED_RATE);

            // 插入实战成绩
            practicalScoreMapper.insert(score);

            synchronized (JudgementServiceImpl.class) {
                // 检查五位裁判是否打分完毕
                int count = practicalScoreMapper.getCountByUidList(Collections.singletonList(uid));
                if (count == 5) {
                    List<Float> scores = practicalScoreMapper.getScoresByUid(uid);
                    float sum = 0;
                    for (Float s : scores) sum += s;
                    // 插入平均分
                    scoreInfoMapper.updatePracticalScoreByUid(uid, sum / 5);

                    //判断30位选手是否全部打分完毕
                    UserInfo userInfo = userInfoMapper.getByUid(uid);
                    List<Integer> uidList = contestantMapper.getUidListByGroupAndZone(userInfo.getGroup(), userInfo.getZone());
                    count = practicalScoreMapper.getCountByUidList(uidList);
                    //该进程打分流程完毕
                    if (count == 150) {
                        String key = RedisUtil.getProcessKey(userInfo.getGroup(), userInfo.getZone());
                        String value = RedisUtil.getProcessValue(Process.PRACTICE, Step.NEXT);
                        stringRedisTemplate.opsForValue().set(key, value);
                    }
                }
            }
        } else if (process.equals(Process.Q_AND_A)) {

            // 检验是否重复打分
            int time = qAndAScoreMapper.checkTime(uid, jid);
            if (time != 0) throw new RateException(ErrorMessageConstant.REPEATED_RATE);

            // 插入问答成绩
            qAndAScoreMapper.insert(score);

            synchronized (JudgementServiceImpl.class) {
                //检查五位裁判是否打分完毕
                int count = qAndAScoreMapper.getCountByUidList(Collections.singletonList(uid));
                if (count == 5) {
                    List<Float> scores = qAndAScoreMapper.getScoresByUid(uid);
                    float sum = 0;
                    for (Float s : scores) sum += s;
                    // 插入平均分
                    scoreInfoMapper.updateQAndAScoreByUid(uid, sum / 5);

                    // 计算总成绩
                    UserInfo userInfo = userInfoMapper.getByUid(uid);
                    calculateFinalScore(uid, userInfo.getZone());

                    // 判断30位选手是否全部打分完毕
                    List<Integer> uidList = contestantMapper.getUidListByGroupAndZone(userInfo.getGroup(), userInfo.getZone());
                    count = qAndAScoreMapper.getCountByUidList(uidList);
                    // 该进程打分流程完毕
                    if (count == 150) {
                        String key = RedisUtil.getProcessKey(userInfo.getGroup(), userInfo.getZone());
                        String value = RedisUtil.getProcessValue(Process.Q_AND_A, Step.NEXT);
                        stringRedisTemplate.opsForValue().set(key, value);
                    }
                }
            }
        } else {
            throw new NoSuchProcessException(ErrorMessageConstant.NO_SUCH_PROCESS_EXCEPTION);
        }
    }

    // 计算最终成绩
    public void calculateFinalScore(int uid, String zone) {
        ScoreDAO score = scoreInfoMapper.getScoreDAOByUid(uid);
        float finalScore;
        if (zone.equals(Zone.N)) finalScore = score.getPracticalScore() / 2 + score.getQAndAScore() / 2;
        else finalScore =
                (score.getWrittenScore() + 2 * score.getPracticalScore() + 2 * score.getQAndAScore()) / 5;
        scoreInfoMapper.updateFinalScore(uid, zone, finalScore);
    }
}