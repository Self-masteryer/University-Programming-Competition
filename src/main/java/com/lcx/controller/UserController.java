package com.lcx.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessage;
import com.lcx.common.result.Result;
import com.lcx.common.utils.AliOssUtil;

import com.lcx.domain.DTO.ChangePwdDTO;
import com.lcx.domain.DTO.ResetAccountDTO;
import com.lcx.domain.DTO.UserLoginDTO;
import com.lcx.domain.Entity.AccountInfo;
import com.lcx.domain.VO.PreScoreVO;
import com.lcx.service.ScoreService;
import com.lcx.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private ScoreService scoreService;
    @Resource
    private AliOssUtil aliOssUtil;

    // 登录
    @PostMapping("/login")
    public Result login(@RequestBody @Validated UserLoginDTO userLoginDTO) {
        userService.login(userLoginDTO);
        log.info("用户{}，登录成功", StpUtil.getLoginIdAsInt());
        return Result.success();
    }

    // 查询个人往届成绩
    @GetMapping("/myPreScore")
    public Result<List<PreScoreVO>> queryMyPreScore() {
        return Result.success(scoreService.queryMyPreScore());
    }

    // 重置用户名、密码
    @PutMapping("/usernameAndPassword")
    public Result resetUsernameAndPassword(@RequestBody @Validated ResetAccountDTO resetAccountDTO) {
        userService.resetUsernameAndPassword(resetAccountDTO);
        return Result.success("账号密码修改成功,请重新登录");
    }

    // 修改密码
    @PutMapping("/password")
    public Result changePwd(@RequestBody @Validated ChangePwdDTO changePwdDTO) {
        userService.changePwd(changePwdDTO);
        return Result.success("密码修改成功，请重新登录");
    }

    // 查询信息
    @GetMapping("/info")
    public Result<AccountInfo> getInfo() {
        return Result.success(userService.getInfo());
    }

    // 修改信息
    @PutMapping("/info")
    public Result updateInfo(@RequestBody @Validated AccountInfo accountInfo) {
        userService.updateInfo(accountInfo);
        return Result.success("修改信息成功");
    }

    // 上传文件
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String suffix = null;
            if (originalFileName != null)
                suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + suffix;
            String filePath = aliOssUtil.upload(file.getBytes(), newFileName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{}", e.getMessage());
        }
        return Result.error(ErrorMessage.UPLOAD_FAILED);
    }
}