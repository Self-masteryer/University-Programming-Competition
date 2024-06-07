package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
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
import com.lcx.pojo.VO.GrageVO;
import com.lcx.pojo.VO.SingleScoreVO;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    // 添加往届成绩
    @Override
    public void addPreScore(String group, String zone) {
        List<ScoreInfo> scoreInfoList = scoreInfoMapper.getListByGroupAndZone(group,zone);
        scoreInfoList.sort(Comparator.comparingDouble(ScoreInfo::getFinalScore).reversed());
        for (int i = 0; i < scoreInfoList.size(); i++) {
            ScoreInfo scoreInfo = scoreInfoList.get(i);
            PreScore preScore = new PreScore();
            BeanUtils.copyProperties(scoreInfo, preScore);
            preScore.setSchool(contestantMapper.getSchoolByUid(scoreInfo.getUid()));
            preScore.setRanking(i + 1);

            preScoreMapper.insert(preScore);
        }
    }

    // 查询往届成绩
    @Override
    public PageResult queryPreScore(PreScorePageQuery preScorePageQuery) {
        PageHelper.startPage(preScorePageQuery.getPageNo(), preScorePageQuery.getPageSize());
        Page<PreScoreVO> page = preScoreMapper.pageQuery(preScorePageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    // 查询个人往届成绩
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

    // 查询学生成绩
    @Override
    @Transactional
    public PageResult pageQueryStudentScore(StudentScorePageQuery studentScorePageQuery) {
        PageHelper.startPage(studentScorePageQuery.getPageNo(), studentScorePageQuery.getPageSize());
        Page<StudentScore> page = studentScoreMapper.pageQuery(studentScorePageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    // 删除笔试成绩
    @Override
    @Transactional
    public void deleteWrittenScore(String group, String zone) {
        writtenScoreMapper.deleteByGroupAndZone(group, zone);
    }

    // 添加学生成绩
    @Override
    @Transactional
    public void addStudentScore(String group, String zone) {
        List<GrageVO> scoreInfoList = contestantMapper
                .getScoreVoListByGroupAndZone(group, zone);
        scoreInfoList.sort(Comparator.comparingDouble(GrageVO::getFinalScore).reversed());

        int session = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("session")));
        for (int i = 0; i < 5; i++) {
            GrageVO grageVo = scoreInfoList.get(i);
            StudentScore studentScore = StudentScore.builder().name(grageVo.getName()).idCard(grageVo.getIdCard())
                    .school(grageVo.getSchool()).session(session).score(grageVo.getFinalScore()).build();
            if(Objects.equals(stringRedisTemplate.opsForValue().get(RedisUtil.COMPETITION), Process.DISTRICT)){
                if (i < 2) studentScore.setPrize(Prize.PROVINCIAL_FIRST_PRIZE);
                else studentScore.setPrize(Prize.PROVINCIAL_SECOND_PRIZE);
            }else{
                if (i < 2) studentScore.setPrize(Prize.NATIONAL_FIRST_PRIZE);
                else studentScore.setPrize(Prize.NATIONAL_SECOND_PRIZE);
            }

            studentScoreMapper.insert(studentScore);
        }
    }

    // 删除成绩单
    @Override
    @Transactional
    public void deleteScore(String group, String zone) {
        List<Integer> uidList = contestantMapper.getUidListByGroupAndZone(group, zone);
        for (Integer uid : uidList) {
            scoreInfoMapper.deleteByUid(uid);// 成绩信息
            practicalScoreMapper.deleteByUid(uid);// 实战成绩
            qAndAScoreMapper.deleteByUid(uid);// 快问快打成绩
        }
    }

    // 查询笔试成绩
    @Override
    @Transactional
    public SingleScoreVO getWrittenScore(int uid) {
        return writtenScoreMapper.getVOByUid(uid);
    }
}
