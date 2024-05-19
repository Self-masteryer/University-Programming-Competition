package com.lcx.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.enumeration.Process;
import com.lcx.common.exception.process.ProcessStatusError;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.Entity.UserInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CheckProcessAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Pointcut("execution(* com.lcx.controller.*.*(..)) && @annotation(com.lcx.annotation.CheckProcess) ")
    public void checkProcessPointCut(){}

    @Before("checkProcessPointCut()")
    public void checkProcess(JoinPoint joinPoint){
        //获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获得注解
        Process process=methodSignature.getMethod().getAnnotation(CheckProcess.class).value();

        if(process==null) return;
        else if(process==Process.WRITTEN) checkProcess(com.lcx.common.constant.Process.WRITTEN);
        else if(process==Process.PRACTICE) checkProcess(com.lcx.common.constant.Process.PRACTICE);
        else if(process==Process.Q_AND_A) checkProcess(com.lcx.common.constant.Process.Q_AND_A);
    }

    private boolean checkProcess(String process) {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        //从redis获取当前进程状态
        String key = RedisUtil.getProcessKey(userInfo.getGroup(),userInfo.getZone());
        String reidsProcess = stringRedisTemplate.opsForValue().get(key);
        // 不存在或不相等，抛异常
        if (reidsProcess == null || !reidsProcess.equals(process))
            throw new ProcessStatusError(ErrorMessageConstant.PROCESS_STATUS_ERROR);
        else return true;
    }
}
