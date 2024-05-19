package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Step;
import com.lcx.common.result.Result;
import com.lcx.pojo.VO.DistrictScoreVO;
import com.lcx.pojo.VO.SeatInfo;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.service.HostService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/host")
@SaCheckRole(value = {"host", "admin"}, mode = SaMode.OR)
@Slf4j
public class HostController {

    @Resource
    private HostService hostService;

    // 开启比赛
    @GetMapping("/startCompetition")
    public Result startCompetition() {
        hostService.startCompetition();
        return Result.success("比赛开始");
    }

    // 推进下一流程
    @GetMapping("/nextProcess")
    public Result nextProcess(@NotEmpty String process) {
        hostService.nextProcess(process);
        return Result.success("已开启" + process + "流程");
    }

    // 座位号抽签
    @GetMapping("/seatDraw")
    @CheckProcess(process = Process.WRITTEN, step = Step.SEAT_DRAW)
    public Result<List<SeatInfo>> seatDraw() {
        List<SeatInfo> seatTable = hostService.seatDraw();
        return Result.success(seatTable);
    }

    // 通过excel上传笔试成绩
    @PostMapping("/postWrittenScoreByExcel")
    @CheckProcess(process = Process.WRITTEN, step = Step.POST_WRITTEN_SCORE)
    public Result postWrittenScoreByExcel(@RequestParam("file") MultipartFile file) {
        hostService.postWrittenScoreByExcel(file);
        return Result.success("成绩上传成功");
    }

    // 按笔试成绩筛选
    @GetMapping("/scoreFilter")
    @CheckProcess(process = Process.WRITTEN, step = Step.SCORE_FILTER)
    public Result<List<DistrictScoreVO>> scoreFilter() {
        return Result.success(hostService.scoreFilter());
    }

    // 分组抽签
    @GetMapping("/groupDraw")
    @CheckProcess(process = Process.PRACTICE, step = Step.GROUP_DRAW)
    public Result<List<SignGroup>> groupDraw() {
        return Result.success(hostService.groupDraw());
    }

}
