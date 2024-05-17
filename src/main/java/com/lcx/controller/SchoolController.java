package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.SignUpDTO;
import com.lcx.pojo.VO.SignUpVO;
import com.lcx.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/school")
@Slf4j
@SaCheckRole(value = {"school","admin"},mode = SaMode.OR)
public class SchoolController {

    @Resource
    private UserService userService;

    // 报名
    @PostMapping("/signUp")
    public Result<SignUpVO> signUp(@RequestBody @Validated SignUpDTO signUpDTO) {
        SignUpVO signUpVO = userService.signUp(signUpDTO);
        return Result.success(signUpVO);
    }
}
