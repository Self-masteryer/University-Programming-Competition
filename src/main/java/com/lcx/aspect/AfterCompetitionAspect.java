package com.lcx.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.DTO.CompInfoDTO;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.service.AdminService;
import com.lcx.service.ContestantService;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Slf4j
public class AfterCompetitionAspect {

    @Resource
    private AdminService adminService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ScoreService scoreService;
    @Resource
    private ContestantService contestantService;

    @Pointcut("execution(* com.lcx.controller.*.*(..)) && @annotation(com.lcx.annotation.AfterCompetition) ")
    public void afterCompetitionPointCut() {
    }

    @After("afterCompetitionPointCut()")
    @Transactional
    public void beforeCompetitionPointCut(JoinPoint joinPoint) {
        // 判断是管理员还是主持人
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());

        String group, zone;
        if (userInfo != null) {
            group = userInfo.getGroup();
            zone = userInfo.getZone();
        } else {
            Object[] args = joinPoint.getArgs();
            CompInfoDTO compInfoDTO = (CompInfoDTO) args[0];
            group = compInfoDTO.getGroup();
            zone = compInfoDTO.getZone();
        }

        // 存储学生成绩
        adminService.addStudentScore(group, zone);
        // 存储往届成绩
        scoreService.addPreScore(group, zone);
        // 设置为游客身份
        adminService.setAsTourist(group, zone);
        // 添加国赛选手
        contestantService.addToNational(group, zone);
        // 删除成绩信息和区赛选手
        adminService.deleteScore(group, zone);
    }

}
