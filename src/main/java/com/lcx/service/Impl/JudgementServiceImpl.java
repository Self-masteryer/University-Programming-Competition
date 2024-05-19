package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.exception.SignNumOutOfBoundException;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.DistrictScoreMapper;
import com.lcx.mapper.PracticalScoreMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.DAO.SignInfoDAO;
import com.lcx.pojo.DTO.PracticalScoreDTO;
import com.lcx.pojo.Entity.DistrictScore;
import com.lcx.pojo.Entity.PracticalScore;
import com.lcx.pojo.Entity.Student;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.pojo.VO.SignInfo;
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
        SignGroup signGroup = SignGroup.builder().A(A).B(B).build();
        return signGroup;
    }

    @Override
    @Transactional
    public void rate(PracticalScoreDTO practicalScoreDTO) {

        // 获得成绩表ID
        DistrictScore districtScore = districtScoreMapper.getByUid(practicalScoreDTO.getUid());

        // 构建实战成绩
        PracticalScore practicalScore = PracticalScore.builder().uid(practicalScoreDTO.getUid())
                .sid(districtScore.getId()).jid(StpUtil.getLoginIdAsInt())
                .score(practicalScoreDTO.getPracticalScore()).build();

        practicalScoreMapper.insert(practicalScore);
    }

}