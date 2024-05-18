package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.UserLoginDTO;
import com.lcx.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@SaCheckLogin
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @SaIgnore
    @PostMapping("/login")
    public Result login(@RequestBody @Validated UserLoginDTO userLoginDTO) {
        //登录，成功则返回id
        int id = userService.login(userLoginDTO);
        StpUtil.login(id);
        return Result.success("登录成功");
    }
}
