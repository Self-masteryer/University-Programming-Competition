package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.annotation.AfterCompetition;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.result.Result;
import com.lcx.common.util.ConvertUtil;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.pojo.DTO.CompInfoDTO;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.Entity.SingleScore;
import com.lcx.pojo.VO.CommonScore;
import com.lcx.pojo.VO.GroupScore;
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
@SaCheckRole(value = {"host", "admin"}, mode = SaMode.OR)
@Slf4j
public class HostController {

    @Resource
    private HostService hostService;
    @Resource
    private UserInfoMapper userInfoMapper;

    // 开启比赛
    @PostMapping("/startDistrictCompetition")
    public Result startCompetition(@RequestBody CompInfoDTO compInfoDTO) {
        SaSession session = StpUtil.getSession();
        if (session.getInt(Role.ROLE) == Role.HOST)// 主持人
            hostService.startCompetition(session.getString(Group.GROUP), session.getString(Zone.ZONE));
        else// 管理员
            hostService.startCompetition(compInfoDTO.getGroup(), compInfoDTO.getZone());
        return Result.success();
    }

    // 推进下一流程
    @PostMapping("/nextProcess")
    public Result nextProcess(@RequestBody CompInfoDTO compInfoDTO) {
        SaSession session = StpUtil.getSession();
        if (session.getInt(Role.ROLE) == Role.HOST)// 主持人
            hostService.nextProcess(session.getString(Group.GROUP), session.getString(Zone.ZONE));
        else// 管理员
            hostService.nextProcess(compInfoDTO.getGroup(), compInfoDTO.getZone());
        return Result.success();
    }

    // 座位号抽签
    @PostMapping("/seatDraw")
    @CheckProcess(process = Process.WRITTEN, step = Step.SEAT_DRAW)
    public Result<List<SeatInfo>> seatDraw(@RequestBody CompInfoDTO compInfoDTO) {
        SaSession session = StpUtil.getSession();
        List<SeatInfo> seatTable;

        if (session.getInt(Role.ROLE) == Role.HOST)// 主持人
            seatTable = hostService.seatDraw(session.getString(Group.GROUP), session.getString(Zone.ZONE));
        else// 管理员
            seatTable = hostService.seatDraw(compInfoDTO.getGroup(), compInfoDTO.getZone());

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
    @PostMapping("/scoreFilter")
    @CheckProcess(process = Process.WRITTEN, step = Step.SCORE_FILTER)
    public Result<List<SingleScore>> scoreFilter(@RequestBody CompInfoDTO compInfoDTO) {
        SaSession session = StpUtil.getSession();
        List<SingleScore> scoreList;

        if (session.getInt(Role.ROLE) == Role.HOST)// 主持人
            scoreList = hostService.scoreFilter(session.getString(Group.GROUP), session.getString(Zone.ZONE));
        else// 管理员
            scoreList = hostService.scoreFilter(compInfoDTO.getGroup(), compInfoDTO.getZone());

        return Result.success(scoreList);
    }

    // 分组抽签
    @PostMapping("/groupDraw")
    @CheckProcess(process = Process.PRACTICE, step = Step.GROUP_DRAW)
    public Result<List<SignGroup>> groupDraw(@RequestBody CompInfoDTO compInfoDTO) {
        SaSession session = StpUtil.getSession();
        List<SignGroup> signGroupList;

        if (session.getInt(Role.ROLE) == Role.HOST)// 主持人
            signGroupList = hostService.groupDraw(session.getString(Group.GROUP), session.getString(Zone.ZONE));
        else// 管理员
            signGroupList = hostService.groupDraw(compInfoDTO.getGroup(), compInfoDTO.getZone());

        return Result.success(signGroupList);
    }

    // 查询每组实战对决分数
    @GetMapping("/groupScore")
    @CheckProcess(process = Process.PRACTICE, step = Step.RATE)
    public Result<GroupScore> groupScore(int aUid, int bUid) {
        return Result.success(hostService.getGroupScore(aUid, bUid));
    }

    // 查询快问快答成绩
    @GetMapping("/qAndAScore")
    @CheckProcess(process = Process.Q_AND_A, step = Step.RATE)
    public Result<CommonScore> getQAndAScore(int uid) {
        return Result.success(hostService.getQAndAScore(uid));
    }

    // 成绩导出
    @PostMapping("/exportScoreToPdf")
    @CheckProcess(process = Process.FINAL, step = Step.SCORE_EXPORT)
    @AfterCompetition
    public void exportScoreToPdf(HttpServletResponse response, @RequestBody CompInfoDTO compInfoDTO) {
        SaSession session = StpUtil.getSession();

        if (session.getInt(Role.ROLE) == Role.HOST)// 主持人
            hostService.exportScoreToPdf(session.getString(Group.GROUP), session.getString(Zone.ZONE),response);
        else// 管理员
            hostService.exportScoreToPdf(compInfoDTO.getGroup(), compInfoDTO.getZone(),response);
    }

}
