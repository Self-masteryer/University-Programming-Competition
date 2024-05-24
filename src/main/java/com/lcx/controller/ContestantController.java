package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.lcx.service.ContestantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contestant")
@SaCheckRole("contestant")
@Slf4j
public class ContestantController {
    @Resource
    private ContestantService contestantService;
}
