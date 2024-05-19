package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.constant.Process;
import com.lcx.common.exception.SignNumOutOfBoundException;
import com.lcx.common.exception.process.NoSuchProcessException;
import com.lcx.mapper.*;
import com.lcx.pojo.DAO.SignInfoDAO;
import com.lcx.pojo.DTO.ScoreDTO;
import com.lcx.pojo.Entity.DistrictScore;
import com.lcx.pojo.Entity.Score;
import com.lcx.pojo.Entity.Student;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.service.JudgementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JudgementServiceImpl implements JudgementService {

    @Resource
    private DistrictScoreMapper districtScoreMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private PracticalScoreMapper practicalScoreMapper;
    @Resource
    private QAndAScoreMapper qAndAScoreMapper;

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
        DistrictScore districtScore = districtScoreMapper.getByUid(uid);

        // 分数
        Score score = Score.builder().uid(scoreDTO.getUid())
                .sid(districtScore.getId()).jid(StpUtil.getLoginIdAsInt())
                .score(scoreDTO.getScore()).build();

        if (process.equals(Process.PRACTICE)) {
            //插入实战成绩
            practicalScoreMapper.insert(score);
            //检查五位裁判是否打分完毕
            int count = practicalScoreMapper.getCountByUid(uid);
            if (count == 5) {
                List<Integer> scores = practicalScoreMapper.getScoresByUid(uid);
                int sum = 0;
                for (Integer s : scores) sum += s;
                // 插入评价分
                districtScoreMapper.updatePracticalScoreByUid(uid, (float) sum / 5);
            }
        } else if (process.equals(Process.Q_AND_A)) {
            // 插入问答成绩
            qAndAScoreMapper.insert(score);
            //检查五位裁判是否打分完毕
            int count = qAndAScoreMapper.getCountByUid(uid);
            if (count == 5) {
                List<Integer> scores = qAndAScoreMapper.getScoresByUid(uid);
                int sum = 0;
                for (Integer s : scores) sum += s;
                // 插入平均分
                districtScoreMapper.updateQAndAScoreByUid(uid, (float) sum / 5);
            }
        } else {
            throw new NoSuchProcessException(ErrorMessageConstant.NO_SUCH_PROCESS_EXCEPTION);
        }
    }

}