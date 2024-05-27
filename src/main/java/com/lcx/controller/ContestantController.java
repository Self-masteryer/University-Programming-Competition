package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.CheckQueryProcess;
import com.lcx.common.constant.Item;
import com.lcx.common.constant.Process;
import com.lcx.common.result.Result;
import com.lcx.pojo.VO.SingleScoreVO;
import com.lcx.service.ContestantService;
import com.lcx.service.Impl.StpInterfaceImpl;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Resource
    private ScoreService scoreService;

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

    // 查询笔试成绩
    @GetMapping("/writtenScore")
    @CheckQueryProcess(process = Process.WRITTEN, item = Item.SCORE)
    public Result<SingleScoreVO> getWrittenScore() {
        return Result.success(scoreService.getWrittenScore(StpUtil.getLoginIdAsInt()));
    }
}
