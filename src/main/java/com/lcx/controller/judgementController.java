package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Step;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.ScoreDTO;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.service.JudgementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/judgement")
@SaCheckRole(value = {"judgement", "admin"}, mode = SaMode.OR)
@Slf4j
public class judgementController {

    @Resource
    private JudgementService judgementService;

    @GetMapping("/signGroup/{signNum}")
    @CheckProcess(process = "", step = Step.RATE)
    public Result<SignGroup> getSignGroup(@PathVariable int signNum) {
        return Result.success(judgementService.getSignGroup(signNum));
    }

    @PostMapping("/practiceRate")
    @CheckProcess(process = Process.PRACTICE, step = Step.RATE)
    public Result practiceRate(@RequestBody ScoreDTO scoreDTO) {
        judgementService.rate(scoreDTO,Process.PRACTICE);
        return Result.success("实战环节评分成功!");
    }

    @PostMapping("/qAndARate")
    @CheckProcess(process = Process.Q_AND_A, step = Step.RATE)
    public Result qAndARate(@RequestBody ScoreDTO scoreDTO) {
        judgementService.rate(scoreDTO,Process.Q_AND_A);
        return Result.success("问答环节评分成功!");
    }

}
