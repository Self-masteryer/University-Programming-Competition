package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.lcx.common.result.Result;
import com.lcx.service.ContestantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contestant")
@SaCheckRole("contestant")
@Slf4j
public class ContestantController {
    @Resource
    private ContestantService contestantService;

    // 查询座位号
    @GetMapping("/seatNum")
    public Result<String> getSeatNum() {
        return Result.success(contestantService.getSeatNum());
    }

    // 查询组号
    @GetMapping("/signNum")
    public Result<String> getSignNum() {
        return Result.success(contestantService.getSignNum());
    }

}
