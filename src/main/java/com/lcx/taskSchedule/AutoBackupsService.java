package com.lcx.taskSchedule;

import com.lcx.common.properties.MysqlProperties;
import com.lcx.service.SystemMysqlBackupsService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
public class AutoBackupsService {

    @Resource
    private TaskScheduler taskScheduler;
    @Resource
    private SystemMysqlBackupsService systemMysqlBackupsService;
    @Resource
    private MysqlProperties mysqlProperties;

    private ScheduledFuture<?> AutoBackupsScheduledFuture;

    // 启动自动备份
    public void StartAutoBackups() {
        AutoBackupsScheduledFuture = taskScheduler.schedule(()-> systemMysqlBackupsService.mysqlBackups(mysqlProperties.getPath(),
                mysqlProperties.getUrl(), mysqlProperties.getUsername(),
                mysqlProperties.getPassword(), mysqlProperties.getDatabase()),new CronTrigger("0 0 0 * * ? "));// 每天00：00：00
    }

    // 关闭自动备份
    public void StopAutoBackups() {
        if(AutoBackupsScheduledFuture != null) {
            AutoBackupsScheduledFuture.cancel(true);
        }
    }

}
