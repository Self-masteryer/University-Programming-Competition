package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.CheckQueryProcess;
import com.lcx.common.constant.Item;
import com.lcx.common.constant.Process;
import com.lcx.common.result.Result;
import com.lcx.service.ContestantService;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @CheckQueryProcess(process = Process.WRITTEN, item = Item.NUM)
    public Result getSeatNum() {
        return Result.success(contestantService.getSeatNum(),"查询成功");
    }

    // 查询组号
    @GetMapping("/signNum")
    @CheckQueryProcess(process = Process.PRACTICE, item = Item.NUM)
    public Result getSignNum() {
        return Result.success(contestantService.getSignNum(),"查询成功");
    }

    // 查询笔试成绩
    @GetMapping("/writtenScore")
    @CheckQueryProcess(process = Process.WRITTEN)
    public Result getWrittenScore() {
        return Result.success(scoreService.getWrittenScore(StpUtil.getLoginIdAsInt()),"查询成功");
    }

    // 放弃国赛资格
    @PostMapping("/waiverNatCompQual")
    public Result waiverNatCompQual() {
        contestantService.waiverNatCompQual();
        log.info("ID:{}成功放弃国赛资格",StpUtil.getLoginIdAsInt());
        return Result.success();
    }

}
