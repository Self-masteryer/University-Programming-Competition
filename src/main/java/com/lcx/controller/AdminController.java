package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.lcx.annotation.AfterCompetition;
import com.lcx.annotation.CheckProcess;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Step;
import com.lcx.common.result.PageResult;
import com.lcx.common.result.Result;
import com.lcx.common.util.ConvertUtil;
import com.lcx.pojo.DTO.CompInfoDTO;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.DTO.SignUpTime;
import com.lcx.pojo.VO.SeatInfo;
import com.lcx.pojo.VO.SignGroup;
import com.lcx.pojo.VO.WrittenScore;
import com.lcx.service.AdminService;
import com.lcx.service.HostService;
import com.lcx.service.ScoreService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/admin")
@SaCheckRole("admin")
@Slf4j
public class AdminController {

    @Resource
    private AdminService adminService;
    @Resource
    private ScoreService scoreService;
    @Resource
    private HostService hostService;

    // 通过excel表格新增用户
    @PostMapping("/createUserByExcel")
    public void createUserByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        log.info("管理员通过excel表格新增用户");
        adminService.addUserByExcel(file, response);
    }

    // 通过excel表格新增学校用户
    @PostMapping("/createSchoolByExcel")
    public void createSchoolByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        log.info("管理员通过excel表格新增学校用户");
        adminService.addSchoolByExcel(file, response);
    }

    // 设置报名时间
    @PostMapping("/setSignUpTime")
    public Result setSignUpTime(@RequestBody SignUpTime signUpTime) {
        log.info("设置报名时间:{}~{}",signUpTime.getBegin(),signUpTime.getEnd());
        adminService.setSignUpTime(signUpTime);
        return Result.success("报名时间设置成功");
    }

    // 开启国赛
    @GetMapping("/startNationalCompetition")
    public Result startNationalCompetition() {
        adminService.startNationalCompetition();
        return Result.success();
    }

    // 分页查询往届成绩
    @GetMapping("/preScore")
    public Result<PageResult> queryPreScore(PreScorePageQuery preScorePageQuery) {
        return Result.success(scoreService.queryPreScore(preScorePageQuery));
    }

    // 开启比赛
    @PostMapping("/startCompetition")
    public Result startCompetition(@RequestBody CompInfoDTO compInfoDTO) {
        hostService.startCompetition(compInfoDTO.getGroup(), compInfoDTO.getZone());
        return Result.success();
    }

    // 推进下一流程
    @PostMapping("/nextProcess")
    public Result nextProcess(@RequestBody CompInfoDTO compInfoDTO) {
        String nextProcess = hostService.nextProcess(compInfoDTO.getGroup(),compInfoDTO.getZone());
        String group= ConvertUtil.parseGroupStr(compInfoDTO.getGroup());
        String zone = ConvertUtil.parseZoneStr(compInfoDTO.getZone());
        return Result.success(group+zone+"已推进至" + nextProcess + "环节");
    }

    // 座位号抽签
    @PostMapping("/seatDraw")
    @CheckProcess(process = Process.WRITTEN, step = Step.SEAT_DRAW)
    public Result<List<SeatInfo>> seatDraw(@RequestBody CompInfoDTO compInfoDTO) {
        List<SeatInfo> seatTable = hostService.seatDraw(compInfoDTO.getGroup(),compInfoDTO.getZone());
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
    public Result<List<WrittenScore>> scoreFilter(@RequestBody CompInfoDTO compInfoDTO) {
        return Result.success(hostService.scoreFilter(compInfoDTO.getGroup(),compInfoDTO.getZone()));
    }

    // 分组抽签
    @PostMapping("/groupDraw")
    @CheckProcess(process = Process.PRACTICE, step = Step.GROUP_DRAW)
    public Result<List<SignGroup>> groupDraw(@RequestBody CompInfoDTO compInfoDTO) {
        return Result.success(hostService.groupDraw(compInfoDTO.getGroup(),compInfoDTO.getZone()));
    }

    // 成绩导出
    @PostMapping("/exportScoreToPdf")
    @CheckProcess(process = Process.FINAL, step = Step.SCORE_EXPORT)
    @AfterCompetition
    public void exportScoreToPdf(@RequestBody CompInfoDTO compInfoDTO,HttpServletResponse response) {
        hostService.exportScoreToPdf(compInfoDTO.getGroup(),compInfoDTO.getZone(),response);
    }

}
