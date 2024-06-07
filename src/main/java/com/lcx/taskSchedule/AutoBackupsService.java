package com.lcx.taskSchedule;

import com.lcx.common.properties.MysqlProperties;
import com.lcx.service.SystemMysqlBackupsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class AutoBackupsService {

    @Resource
    private TaskScheduler taskScheduler;
    @Resource
    private SystemMysqlBackupsService systemMysqlBackupsService;
    @Resource
    private MysqlProperties mysqlProperties;

    private ScheduledFuture<?> AutoBackupsScheduledFuture;

    // 启动自动备份
    public void StartAutoBackups(String cronExpression) {
        AutoBackupsScheduledFuture = taskScheduler.schedule(()-> systemMysqlBackupsService.mysqlBackups(mysqlProperties.getPath(),
                mysqlProperties.getUrl(), mysqlProperties.getUsername(),
                mysqlProperties.getPassword(), mysqlProperties.getDatabase()),new CronTrigger(cronExpression));
        log.info("自动备份已开启:{}", cronExpression);
    }

    // 关闭自动备份
    public void StopAutoBackups() {
        if(AutoBackupsScheduledFuture != null)
            AutoBackupsScheduledFuture.cancel(true);
        log.info("自动备份已关闭");
    }

}
