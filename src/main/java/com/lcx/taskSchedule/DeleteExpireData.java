package com.lcx.taskSchedule;

import com.lcx.common.properties.MysqlProperties;
import com.lcx.mapper.SystemMysqlBackupsMapper;
import com.lcx.pojo.DAO.BackupsFilePath;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class DeleteExpireData {

    @Resource
    private MysqlProperties mysqlProperties;
    @Resource
    private SystemMysqlBackupsMapper systemMysqlBackupsMapper;

    // 每天00：00：00删除过期数据
    @Scheduled(cron = "0 0 0 * * ? ")
    public void backups(){
        // 获得过期文件
        List<BackupsFilePath> filePathList=systemMysqlBackupsMapper.getExpiredData(mysqlProperties.getDay());
        // 删除
        for (BackupsFilePath filePath : filePathList) {
            File file = new File(filePath.getPath() + filePath.getFileName());
            if (file.exists()) {
                if(file.delete()) log.info("成功删除备份文件:{}",filePath.getFileName());
                else log.error("备份文件:{}删除失败",filePath.getFileName());
            }else{
                log.error("备份文件:{}不存在",filePath.getFileName());
            }
        }
        // 删除数据库记录
        for (BackupsFilePath file : filePathList)
            systemMysqlBackupsMapper.deleteByFileName(file.getFileName());
    }
}
