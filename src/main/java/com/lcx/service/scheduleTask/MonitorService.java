package com.lcx.service.scheduleTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
public class MonitorService {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    @Autowired
    public MonitorService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void startTask() {
        // 使用Cron表达式来定义任务执行的时间
        scheduledFuture = taskScheduler.schedule(() -> {
            // 这里是任务的逻辑
            System.out.println("Task is running...");
        }, new CronTrigger("0/5 * * * * ?")); // 每5秒执行一次
    }

    public void stopTask() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true); // 取消任务
        }
    }

}
