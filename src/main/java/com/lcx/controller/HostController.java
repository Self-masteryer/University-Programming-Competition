package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.AfterCompetition;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Step;
import com.lcx.common.result.Result;
import com.lcx.common.util.ConvertUtil;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.VO.WrittenScore;
import com.lcx.pojo.VO.SeatInfo;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.service.HostService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/host")
@SaCheckRole(value = "host")
@Slf4j
public class HostController {

    @Resource
    private HostService hostService;
    @Resource
    private UserInfoMapper userInfoMapper;

    // 开启比赛
    @GetMapping("/startDistrictCompetition")
    public Result startCompetition() {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        hostService.startCompetition(userInfo.getGroup(),userInfo.getZone());
        return Result.success();
    }

    // 推进下一流程
    @GetMapping("/nextProcess")
    public Result nextProcess() {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        String nextProcess = hostService.nextProcess(userInfo.getGroup(),userInfo.getZone());
        String group= ConvertUtil.parseGroupStr(userInfo.getGroup());
        String zone = ConvertUtil.parseZoneStr(userInfo.getZone());
        return Result.success(group+zone+"已推进至" + nextProcess + "环节");
    }

    // 座位号抽签
    @GetMapping("/seatDraw")
    @CheckProcess(process = Process.WRITTEN, step = Step.SEAT_DRAW)
    public Result<List<SeatInfo>> seatDraw() {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        List<SeatInfo> seatTable = hostService.seatDraw(userInfo.getGroup(),userInfo.getZone());
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
    public Result<List<WrittenScore>> scoreFilter() {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        return Result.success(hostService.scoreFilter(userInfo.getGroup(),userInfo.getZone()));
    }

    // 分组抽签
    @GetMapping("/groupDraw")
    @CheckProcess(process = Process.PRACTICE, step = Step.GROUP_DRAW)
    public Result<List<SignGroup>> groupDraw() {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        return Result.success(hostService.groupDraw(userInfo.getGroup(),userInfo.getZone()));
    }

    // 成绩导出
    @GetMapping("/exportScoreToPdf")
    @CheckProcess(process = Process.FINAL, step = Step.SCORE_EXPORT)
    @AfterCompetition
    public void exportScoreToPdf(HttpServletResponse response) {
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        hostService.exportScoreToPdf(userInfo.getGroup(),userInfo.getZone(),response);
    }

}
