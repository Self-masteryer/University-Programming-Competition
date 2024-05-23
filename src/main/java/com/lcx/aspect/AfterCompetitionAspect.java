package com.lcx.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.controller.HostController;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.VO.ScoreVo;
import com.lcx.service.AdminService;
import com.lcx.service.HostService;
import com.lcx.service.Impl.HostServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Aspect
@Component
@Slf4j
public class AfterCompetitionAspect {

    @Resource
    private AdminService adminService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ContestantMapper contestantMapper;

    @Pointcut("execution(* com.lcx.controller.HostController.*(..)) && @annotation(com.lcx.annotation.AfterCompetition) ")
    public void afterCompetitionPointCut() {
    }

    @After("afterCompetitionPointCut()")
    @Transactional
    public void beforeCompetitionPointCut(JoinPoint joinPoint) {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        String group = userInfo.getGroup();
        String zone = userInfo.getZone();

        adminService.addStudentScore(group,zone);
        adminService.addPreScore(group,zone);
        adminService.addToNational(group,zone);
        adminService.deleteScore(group,zone);
    }
}
