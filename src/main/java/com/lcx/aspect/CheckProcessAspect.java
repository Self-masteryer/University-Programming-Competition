package com.lcx.aspect;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.exception.process.ProcessStatusException;
import com.lcx.common.utils.RedisUtil;

import com.lcx.domain.DTO.CompInfoDTO;
import com.lcx.taskSchedule.AutoBackupsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class CheckProcessAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private AutoBackupsService autoBackupsService;
    private String group;
    private String zone;
    private String key;
    private String value;

    @Pointcut("execution(* com.lcx.controller.*.*(..)) && @annotation(com.lcx.annotation.CheckProcess) ")
    public void checkProcessPointCut() {}

    @Before("checkProcessPointCut()")
    public void checkProcessBefore(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获得注解参数
        String process = methodSignature.getMethod().getAnnotation(CheckProcess.class).process();
        String step = methodSignature.getMethod().getAnnotation(CheckProcess.class).step();

        // 判断是管理员还是主持人或评委
        SaSession session = StpUtil.getSession();
        if (session.get(Role.ROLE).equals(Role.ADMIN)) {
            Object[] args = joinPoint.getArgs();
            CompInfoDTO compInfoDTO = (CompInfoDTO) args[0];
            group = compInfoDTO.getGroup();
            zone = compInfoDTO.getZone();
        } else {
            group = session.getString(Group.GROUP);
            zone = session.getString(Zone.ZONE);
        }

        checkProcess(process, step);
    }

    @After("checkProcessPointCut()")
    @Transactional
    public void checkProcessAfter() {
        // 打分环节，跳过
        if (value.split(":")[1].equals(Step.RATE)) return;

        String[] processStep=Process.PROCESS_STEP;
        for(int i=0;i<processStep.length;i++)
            if (processStep[i].equals(value)){
                value=processStep[i+1];
                break;
            }

        //更新进程信息
        stringRedisTemplate.opsForValue().set(key, value);
        log.info("{}:{}已进入{}环节",group,zone,value);

        if(value.split(":")[1].equals(Step.RATE)){
            // 关闭每天00：00自动备份数据库
            autoBackupsService.StopAutoBackups();
            // 每一小时备份一次
            autoBackupsService.StartAutoBackups("0 0 0/1 * * ?");
        }
    }

    private void checkProcess(String process, String step) {
        //从redis获取当前进程状态
        key = RedisUtil.getProcessKey(group, zone);
        value = stringRedisTemplate.opsForValue().get(key);

        String candidatedValue=RedisUtil.getProcessValue(process,step);
        if(!Objects.equals(candidatedValue,value))
            throw new ProcessStatusException(ErrorMessage.PROCESS_STATUS_ERROR);
    }
}
