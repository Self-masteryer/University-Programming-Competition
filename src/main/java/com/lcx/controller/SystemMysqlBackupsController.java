package com.lcx.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.lcx.common.properties.MysqlProperties;
import com.lcx.common.result.Result;
import com.lcx.pojo.Entity.SystemMysqlBackups;
import com.lcx.service.SystemMysqlBackupsService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/baskups")
@SaCheckRole("admin")
public class SystemMysqlBackupsController {

    @Resource
    private MysqlProperties mysqlProperties;
    @Resource
    private SystemMysqlBackupsService systemMysqlBackupsService;

    // 查询所有备份数据
    @GetMapping("/list")
    public Result backupsList() {
        List<SystemMysqlBackups> systemMysqlBackups = systemMysqlBackupsService.selectBackupsList();
        return Result.success(systemMysqlBackups);
    }

    // 备份mysql数据
    @PostMapping("/mysqlBackups")
    public Result mysqlBackups() {
        Object systemMysqlBackups = systemMysqlBackupsService.mysqlBackups(mysqlProperties.getPath(),
                mysqlProperties.getUrl(), mysqlProperties.getUsername(),
                mysqlProperties.getPassword(), mysqlProperties.getDatabase());
        return Result.success(systemMysqlBackups);
    }

    @PutMapping("/rollback")
    public Result rollback(@RequestParam @NotNull int id) {
        // 根据id查询查询已有的信息
        SystemMysqlBackups smb = systemMysqlBackupsService.selectListId(id);
        // 恢复数据库
        Object rollback = systemMysqlBackupsService
                .rollback(smb, mysqlProperties.getUsername(), mysqlProperties.getPassword());
        return Result.success(rollback,"恢复成功");
    }
}