package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.lcx.common.result.PageResult;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.DTO.SignUpTime;
import com.lcx.pojo.DTO.StudentScorePageQuery;
import com.lcx.service.AdminService;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@SaCheckRole("admin")
@Slf4j
public class AdminController {

    @Resource
    private AdminService adminService;
    @Resource
    private ScoreService scoreService;

    // 通过excel表格新增用户
    @PostMapping("/createUserByExcel")
    public void createUserByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        log.info("管理员通过excel表格新增用户");
        adminService.addUserByExcel(file, response);
    }

    // 通过excel表格新增学校用户
    @PostMapping("/createSchoolByExcel")
    public void createSchoolByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        log.info("管理员通过excel表格新增学校用户");
        adminService.addSchoolByExcel(file, response);
    }

    // 设置报名时间
    @PostMapping("/setSignUpTime")
    public Result setSignUpTime(@RequestBody SignUpTime signUpTime) {
        log.info("设置报名时间:{}~{}",signUpTime.getBegin(),signUpTime.getEnd());
        adminService.setSignUpTime(signUpTime);
        return Result.success("报名时间设置成功");
    }

    // 开启国赛
    @GetMapping("/startNationalCompetition")
    public Result startNationalCompetition() {
        adminService.startNationalCompetition();
        return Result.success();
    }

    // 分页查询往届成绩
    @GetMapping("/preScore")
    public Result<PageResult> queryPreScore(PreScorePageQuery preScorePageQuery) {
        return Result.success(scoreService.queryPreScore(preScorePageQuery));
    }

    // 查询往届学生获奖情况
    @GetMapping("/studentScore")
    public Result<PageResult> pageQueryStudentScore(StudentScorePageQuery studentScorePageQuery) {
        return Result.success(scoreService.pageQueryStudentScore(studentScorePageQuery));
    }

}
