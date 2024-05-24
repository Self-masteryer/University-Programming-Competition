package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.lcx.common.result.PageResult;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.StudentScorePageQuery;
import com.lcx.pojo.DTO.SignUpDTO;
import com.lcx.pojo.VO.SignUpVO;
import com.lcx.service.AdminService;
import com.lcx.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/school")
@Slf4j
public class SchoolController {

    @Resource
    private UserService userService;
    @Resource
    private AdminService adminService;

    // 报名
    @PostMapping("/signUp")
    @SaCheckRole(value = "school")
    public Result<SignUpVO> signUp(@RequestBody @Validated SignUpDTO signUpDTO) {
        SignUpVO signUpVO = userService.signUp(signUpDTO);
        return Result.success(signUpVO);
    }

    // 查询往届学生获奖情况
    @GetMapping("/studentScore")
    @SaCheckRole(value = {"school", "admin"}, mode = SaMode.OR)
    public Result<PageResult> pageQueryStudentScore(StudentScorePageQuery studentScorePageQuery) {
        return Result.success(adminService.pageQueryStudentScore(studentScorePageQuery));
    }
}
