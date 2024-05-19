package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Step;
import com.lcx.common.result.Result;
import com.lcx.pojo.DTO.PracticalScoreDTO;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.pojo.VO.SignInfo;
import com.lcx.service.JudgementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/judgement")
@SaCheckRole(value = {"judgement","admin"},mode = SaMode.OR)
@Slf4j
public class judgementController {

    @Resource
    private JudgementService judgementService;

    @GetMapping("/signGroup/{signNum}")
    @CheckProcess(process = Process.PRACTICE,step = Step.RATE)
    public Result<SignGroup> getSignGroup(@PathVariable int signNum) {
        return Result.success(judgementService.getSignGroup(signNum));
    }

    @PostMapping("/rate")
    @CheckProcess(process = Process.PRACTICE,step = Step.RATE)
    public Result rate(@RequestBody PracticalScoreDTO practicalScoreDTO) {
        judgementService.rate(practicalScoreDTO);
        return Result.success("评分成功!");
    }
}
