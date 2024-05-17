package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.lcx.common.result.Result;
import com.lcx.service.HostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/host")
@SaCheckRole(value = {"host","admin"},mode = SaMode.OR)
@Slf4j
public class HostController {

    @Resource
    private HostService hostService;

    //开启比赛,并抽签
    @GetMapping("/startAndDraw")
    public Result startAndDraw(){
        hostService.startAndDraw();
        return Result.success("比赛开始，抽签完成");
    }

}
