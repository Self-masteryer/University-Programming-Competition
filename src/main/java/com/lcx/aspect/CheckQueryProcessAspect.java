package com.lcx.aspect;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.CheckQueryProcess;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.exception.process.ProcessStatusException;
import com.lcx.common.util.RedisUtil;
import com.lcx.pojo.DTO.CompInfoDTO;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CheckQueryProcessAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("execution(* com.lcx.controller.*.*(..)) && @annotation(com.lcx.annotation.CheckQueryProcess)")
    public void checkQueryProcessPointCut() {
    }

    @Before("checkQueryProcessPointCut()")
    public void beforeCheckQueryProcess(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        CheckQueryProcess annotation = methodSignature.getMethod().getAnnotation(CheckQueryProcess.class);
        // 获得注解参数
        String process = annotation.process();
        String item = annotation.item();

        int flag;// 标记
        if (process.equals(Process.WRITTEN)) {
            // 检验是否为区赛,不为区赛则抛异常
            String competition = stringRedisTemplate.opsForValue().get(RedisUtil.COMPETITION);
            if (competition == null || competition.equals(Process.NATIONAL))
                throw new ProcessStatusException(ErrorMessage.PROCESS_STATUS_ERROR);

            if (item.equals(Item.SCORE)) flag = 3;
            else flag = 1;
        } else if (process.equals(Process.PRACTICE)) flag = 5;
        else flag = 7;

        String value = getValue(joinPoint);
        String[] processStep = Process.PROCESS_STEP;
        // <flag时查询到，表示未到能查询的流程
        for (int i = 0; i < flag; i++)
            if (value.equals(processStep[i]))
                throw new ProcessStatusException(ErrorMessage.PROCESS_STATUS_ERROR);

    }

    private String getValue(JoinPoint joinPoint) {
        String group, zone;

        // 判断是否为管理员 注入group、zone
        SaSession session = StpUtil.getSession();
        if (session.getInt(Role.ROLE)==Role.ADMIN) {
            Object[] args = joinPoint.getArgs();
            CompInfoDTO compInfoDTO = (CompInfoDTO) args[0];
            group = compInfoDTO.getGroup();
            zone = compInfoDTO.getZone();
        } else {
            group = session.getString(Group.GROUP);
            zone = session.getString(Zone.ZONE);
        }

        String key = RedisUtil.getProcessKey(group, zone);
        return stringRedisTemplate.opsForValue().get(key);
    }
}
