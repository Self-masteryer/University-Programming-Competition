package com.lcx.service.Impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.exception.process.ProcessStatusError;
import com.lcx.common.result.PageResult;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.*;
import com.lcx.pojo.DAO.ScoreDAO;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.DTO.StudentScorePageQuery;
import com.lcx.pojo.Entity.PreScore;
import com.lcx.pojo.Entity.ScoreInfo;
import com.lcx.pojo.Entity.StudentScore;
import com.lcx.pojo.VO.PreScoreVO;
import com.lcx.pojo.VO.ScoreVO;
import com.lcx.pojo.VO.SingleScoreVO;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ScoreServiceImpl implements ScoreService {
    @Resource
    private PreScoreMapper preScoreMapper;
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private ScoreInfoMapper scoreInfoMapper;
    @Resource
    private WrittenScoreMapper writtenScoreMapper;
    @Resource
    private StudentScoreMapper studentScoreMapper;
    @Resource
    private QAndAScoreMapper qAndAScoreMapper;
    @Resource
    private PracticalScoreMapper practicalScoreMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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

    // 计算最终成绩
    @Override
    public void calculateFinalScore(String group, String zone) {
        List<ScoreDAO> scoreDAOList = scoreInfoMapper.getScoreDAOByUid(group, zone);
        for (ScoreDAO scoreDAO : scoreDAOList) {
            float finalScore;
            if (zone.equals(Zone.N)) finalScore = scoreDAO.getPracticalScore() / 2 + scoreDAO.getQAndAScore() / 2;
            else finalScore = (scoreDAO.getWrittenScore() + 2 * scoreDAO.getPracticalScore()
                    + 2 * scoreDAO.getQAndAScore()) / 5;
            scoreInfoMapper.updateFinalScore(scoreDAO.getUid(), finalScore);
        }
    }

    @Override
    @Transactional
    public PageResult pageQueryStudentScore(StudentScorePageQuery studentScorePageQuery) {
        PageHelper.startPage(studentScorePageQuery.getPageNo(), studentScorePageQuery.getPageSize());
        Page<StudentScore> page = studentScoreMapper.pageQuery(studentScorePageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void deleteWrittenScore(String group, String zone) {
        writtenScoreMapper.deleteByGroupAndZone(group, zone);
    }

    @Override
    @Transactional
    public void addStudentScore(String group, String zone) {
        List<ScoreVO> scoreInfoList = contestantMapper
                .getScoreVoListByGroupAndZone(group, zone);
        scoreInfoList.sort(Comparator.comparingDouble(ScoreVO::getFinalScore).reversed());

        int session = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("session")));
        for (int i = 0; i < 5; i++) {
            ScoreVO scoreVo = scoreInfoList.get(i);
            StudentScore studentScore = StudentScore.builder().name(scoreVo.getName()).idCard(scoreVo.getIdCard())
                    .school(scoreVo.getSchool()).session(session).score(scoreVo.getFinalScore()).build();
            if (i < 2) studentScore.setPrize(Prize.PROVINCIAL_FIRST_PRIZE);
            else studentScore.setPrize(Prize.PROVINCIAL_SECOND_PRIZE);

            studentScoreMapper.insert(studentScore);
        }
    }

    @Override
    @Transactional
    public void deleteScore(String group, String zone) {
        List<Integer> uidList = contestantMapper.getUidListByGroupAndZone(group, zone);
        for (Integer uid : uidList) {
            scoreInfoMapper.deleteByUid(uid);// 成绩信息
            practicalScoreMapper.deleteByUid(uid);// 实战成绩
            qAndAScoreMapper.deleteByUid(uid);// 快问快打成绩
            contestantMapper.deleteByUidAndZone(uid, zone);// 选手信息
        }
    }

    @Override
    @Transactional
    public SingleScoreVO getWrittenScore(int uid) {
        return writtenScoreMapper.getVOByUid(uid);
    }
}
