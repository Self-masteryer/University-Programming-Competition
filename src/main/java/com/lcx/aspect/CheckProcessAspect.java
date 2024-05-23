package com.lcx.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Step;
import com.lcx.common.exception.process.ProcessStatusError;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.Entity.UserInfo;
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

@Aspect
@Component
@Slf4j
public class CheckProcessAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Pointcut("execution(* com.lcx.controller.*.*(..)) && @annotation(com.lcx.annotation.CheckProcess) ")
    public void checkProcessPointCut() {
    }

    @Before("checkProcessPointCut()")
    public void checkProcessBefore(JoinPoint joinPoint) {
        //获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获得注解参数
        String process = methodSignature.getMethod().getAnnotation(CheckProcess.class).process();
        String step = methodSignature.getMethod().getAnnotation(CheckProcess.class).step();

        checkProcess(process, step);
    }

    @After("checkProcessPointCut()")
    @Transactional
    public void checkProcessAfter(JoinPoint joinPoint) {
        //获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获得注解参数
        String process = methodSignature.getMethod().getAnnotation(CheckProcess.class).process();
        String step = methodSignature.getMethod().getAnnotation(CheckProcess.class).step();

        if (step.equals(Step.RATE)) return;

        String[] written;
        if (process.equals(Process.WRITTEN)) written = Step.WRITTEN;
        else if (process.equals(Process.PRACTICE)) written = Step.PRACTICE;
        else if (process.equals(Process.Q_AND_A)) written = Step.Q_AND_A;
        else written = Step.FINAL;

        // 获得下一step
        for (int i = 0; i < written.length; i++) {
            if (written[i].equals(step)) {
                step = written[i + 1];
                break;
            }
        }
        //更新进程信息
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        String key = RedisUtil.getProcessKey(userInfo.getGroup(), userInfo.getZone());
        String value = RedisUtil.getProcessValue(process, step);
        stringRedisTemplate.opsForValue().set(key, value);
    }

    private boolean checkProcess(String process, String step) {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        //从redis获取当前进程状态
        String key = RedisUtil.getProcessKey(userInfo.getGroup(), userInfo.getZone());
        String[] values = stringRedisTemplate.opsForValue().get(key).split(":");

        //比赛还未开始
        if (values == null) throw new ProcessStatusError(ErrorMessageConstant.COMPETITION_HAS__NOT_BEGUN);
        else if (step.equals(Step.RATE) && step.equals(values[1])) return true;
        else if (process.equals(values[0]) && step.equals(values[1])) return true;
        // 进程错误
        else throw new ProcessStatusError(ErrorMessageConstant.PROCESS_STATUS_ERROR);
    }
}
