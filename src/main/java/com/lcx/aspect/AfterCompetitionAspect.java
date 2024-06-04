package com.lcx.aspect;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.Group;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Role;
import com.lcx.common.constant.Zone;
import com.lcx.common.util.RedisUtil;
import com.lcx.pojo.DTO.CompInfoDTO;
import com.lcx.service.AdminService;
import com.lcx.service.ContestantService;
import com.lcx.service.ScoreService;
import com.lcx.taskSchedule.AutoBackupsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Aspect
@Component
@Slf4j
public class AfterCompetitionAspect {

    @Resource
    private AdminService adminService;
    @Resource
    private ScoreService scoreService;
    @Resource
    private ContestantService contestantService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private AutoBackupsService autoBackupsService;

    @Pointcut("execution(* com.lcx.controller.*.*(..)) && @annotation(com.lcx.annotation.AfterCompetition) ")
    public void afterCompetitionPointCut() {
    }

    @After("afterCompetitionPointCut()")
    @Transactional
    public void beforeCompetitionPointCut(JoinPoint joinPoint) {
        SaSession session = StpUtil.getSession();
        String group, zone;
        // 主持人
        if (session.getInt(Role.ROLE) == 2) {
            group = session.getString(Group.GROUP);
            zone = session.getString(Zone.ZONE);
        }// 管理员
        else {
            Object[] args = joinPoint.getArgs();
            CompInfoDTO compInfoDTO = (CompInfoDTO) args[0];
            group = compInfoDTO.getGroup();
            zone = compInfoDTO.getZone();
        }

        // 完赛数量加一
        String finalCompetitionNum=stringRedisTemplate.opsForValue().get(RedisUtil.FINISH_COMPETITION_NUM);
        String nowNum=RedisUtil.stringNumAddOne(finalCompetitionNum);
        stringRedisTemplate.opsForValue().set(RedisUtil.FINISH_COMPETITION_NUM,nowNum);

        // 存储学生成绩
        scoreService.addStudentScore(group, zone);
        // 存储往届成绩
        scoreService.addPreScore(group, zone);
        // 将选手、笔试阶段淘汰的选手、主持人、评委设置为游客身份
        adminService.setAsTourist(group, zone);
        // 区赛
        if (Objects.equals(stringRedisTemplate.opsForValue().get(RedisUtil.COMPETITION), Process.DISTRICT)) {
            // 添加国赛选手
            contestantService.addToNational(group, zone);
            // 删除笔试成绩
            scoreService.deleteWrittenScore(group, zone);
        }
        // 删除成绩信息和区赛选手
        scoreService.deleteScore(group, zone);

        // 国赛结束
        if (Objects.equals(stringRedisTemplate.opsForValue().get(RedisUtil.COMPETITION), Process.NATIONAL) && nowNum.equals("2")) {
            // 关闭数据库自动备份
            autoBackupsService.StopAutoBackups();
        }
    }

}
