package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.SignUpTime;
import com.lcx.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@Slf4j
@SaCheckRole("admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    //通过excel表格创建用户
    @PostMapping("/createUserByExcel")
    public void createUserByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        log.info("管理员通过excel表格创建用户");
        adminService.createUserByExcel(file, response);
    }

    //通过excel表格创建学校用户
    @PostMapping("/createSchoolByExcel")
    public void createSchoolByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        log.info("管理员通过excel表格创建学校用户");
        adminService.createSchoolByExcel(file, response);
    }

    //设置报名时间
    @PostMapping("/setSignUpTime")
    public Result setSignUpTime(@RequestBody SignUpTime signUpTime) {
        log.info("设置报名时间:{}~{}",signUpTime.getBegin(),signUpTime.getEnd());
        adminService.setSignUpTime(signUpTime);
        return Result.success("报名时间设置成功");
    }

}
