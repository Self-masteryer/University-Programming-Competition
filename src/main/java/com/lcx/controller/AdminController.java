package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.lcx.common.result.Result;
import com.lcx.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")

@Slf4j
public class AdminController {

    @Resource
    private AdminService adminService;

    //通过excel表格创建用户
    @PostMapping("/createUserByExcel")
    @SaCheckLogin
    public Result createUserByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        adminService.createUserByExcel(file,response);
        return Result.success();
    }
}
