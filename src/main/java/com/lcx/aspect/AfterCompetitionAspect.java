package com.lcx.aspect;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.properties.MysqlProperties;
import com.lcx.common.utils.ConvertUtil;
import com.lcx.common.utils.RedisUtil;
import com.lcx.domain.DTO.CompInfoDTO;
import com.lcx.mapper.ContestantMapper;

import com.lcx.service.AdminService;
import com.lcx.service.ContestantService;
import com.lcx.service.ScoreService;
import com.lcx.service.SystemMysqlBackupsService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class AfterCompetitionAspect {

    @Resource
    private MysqlProperties mysqlProperties;
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
    @Resource
    private SystemMysqlBackupsService systemMysqlBackupsService;
    @Resource
    private ContestantMapper contestantMapper;

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

        // 将成绩存储到学校的学生成绩
        scoreService.addStudentScore(group, zone);
        // 将成绩存储到往届成绩
        scoreService.addPreScore(group, zone);
        // 删除成绩信息
        scoreService.deleteScore(group, zone);

        String competition=stringRedisTemplate.opsForValue().get(RedisUtil.COMPETITION);

        // 区赛
        if (Objects.equals(competition, Process.DISTRICT)) {
            // 删除未晋级选手
            contestantService.deleteContestant(group,zone);
            // 将选手、笔试阶段淘汰的选手、主持人、评委设置为游客身份
            adminService.setAsTourist(group, zone);
            // 删除笔试成绩
            scoreService.deleteWrittenScore(group, zone);
            // 设置默认放弃国赛资格时间段
            if(nowNum.equals("1")){
                String begin= ConvertUtil.parseDateTimeStr(LocalDateTime.now());
                String end= ConvertUtil.parseDateTimeStr(LocalDateTimeUtil.endOfDay(LocalDate.now()).plusDays(7));
                stringRedisTemplate.opsForValue().set(Time.WAIVER_NAT_QUAL_BEGIN_TIME,begin);
                stringRedisTemplate.opsForValue().set(Time.WAIVER_NAT_QUAL_END_TIME,end);
            }else if(nowNum.equals("12")){
                String end= ConvertUtil.parseDateTimeStr(LocalDateTimeUtil.endOfDay(LocalDate.now()).plusDays(7));
                stringRedisTemplate.opsForValue().set(Time.WAIVER_NAT_QUAL_END_TIME,end);
            }
        }// 国赛
        else if (Objects.equals(competition, Process.NATIONAL)) {
            // 删除全部选手
            contestantMapper.deleteByGroupAndZone(group,zone);
            // 设置为游客
            adminService.setAsTourist(group, zone);
            // 删除redis进程信息
            String key = RedisUtil.getProcessKey(group,zone);
            stringRedisTemplate.delete(key);
            // 结束
            if(nowNum.equals("2")){
                // 备份数据
                systemMysqlBackupsService.mysqlBackups(mysqlProperties.getPath(),
                        mysqlProperties.getUrl(), mysqlProperties.getUsername(),
                        mysqlProperties.getPassword(), mysqlProperties.getDatabase());
                // 关闭数据库自动备份
                autoBackupsService.StopAutoBackups();

                // 删除redis数据
                stringRedisTemplate.delete(RedisUtil.COMPETITION);
                stringRedisTemplate.delete(RedisUtil.FINISH_COMPETITION_NUM);
                stringRedisTemplate.delete(RedisUtil.getSuperviseKey(Supervise.STATUS));
                stringRedisTemplate.delete(RedisUtil.getSuperviseKey(Supervise.RATE));
            }
        }
    }

}
