package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.UserLoginDTO;
import com.lcx.pojo.VO.PreScoreVO;
import com.lcx.service.ScoreService;
import com.lcx.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@SaCheckLogin
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private ScoreService scoreService;

    @SaIgnore
    @PostMapping("/login")
    public Result login(@RequestBody @Validated UserLoginDTO userLoginDTO) {
        //登录，成功则返回id
        int id = userService.login(userLoginDTO);
        StpUtil.login(id);
        return Result.success("登录成功");
    }

    // 查询个人往届成绩
    @GetMapping("/myPreScore")
    public Result<List<PreScoreVO>> queryMyPreScore() {
        return Result.success(scoreService.queryMyPreScore());
    }
}
