package com.lcx.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.lcx.common.exception.BaseException;
import com.lcx.common.exception.FileException;
import com.lcx.common.exception.NetworkException;
import com.lcx.mapper.SystemMysqlBackupsMapper;
import com.lcx.pojo.Entity.SystemMysqlBackups;
import com.lcx.service.SystemMysqlBackupsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SystemMysqlBackupsServiceImpl implements SystemMysqlBackupsService {

    public static final String FILE_SUFFIX = ".sql";

    @Resource
    private SystemMysqlBackupsMapper systemMysqlBackupsMapper;

    // 根据ID查询
    @Override
    public SystemMysqlBackups selectListId(int id) {
        return systemMysqlBackupsMapper.selectListId(id);
    }

    // 查询所有备份数据
    @Override
    public List<SystemMysqlBackups> selectBackupsList() {
        return systemMysqlBackupsMapper.selectBackupsList();
    }

    // mysql备份接口
    @Override
    public Object mysqlBackups(String filePath, String url, String userName, String password,String databaseName) {
        // 获取ip
        final String ip = url.substring(13, 22);
        // 获取端口号
        final String port = url.substring(23, 27);
        // 获得数据库文件名称
        StringBuilder mysqlFileName = new StringBuilder()
                .append(databaseName)
                .append("_")
                .append(DateUtil.format(new Date(), "yyyy-MM-dd-HH-mm-ss"))
                .append(FILE_SUFFIX);
        // 获得备份命令
        StringBuilder cmd = new StringBuilder()
                .append("mysqldump ")
                .append("--no-tablespaces ")
                .append("-h")
                .append(ip)
                .append(" -u")
                .append(userName)
                .append(" -p")
                .append(password)
                // 排除MySQL备份表
                .append(" --ignore-table ")
                .append(databaseName)
                .append(".mysql_backups ")
                .append(databaseName)
                .append(" > ")
                .append(filePath)
                .append(mysqlFileName);
        // 判断文件路径是否存在
        if (!FileUtil.exist(filePath)) FileUtil.mkdir(filePath);

        String[] command = new String[]{"cmd", "/c", String.valueOf(cmd)};

        SystemMysqlBackups smb = new SystemMysqlBackups();
        // 备份信息存放到数据库
        smb.setMysqlIp(ip);
        smb.setMysqlPort(port);
        smb.setMysqlCmd(String.valueOf(cmd));
        smb.setDatabaseName(databaseName);
        smb.setBackupsPath(filePath);
        smb.setBackupsName(String.valueOf(mysqlFileName));
        smb.setOperation(0);
        smb.setStatus(1);
        smb.setCreateTime(LocalDateTime.now());
        systemMysqlBackupsMapper.insert(smb);
        log.info("数据库备份命令为：{}", cmd);
        // 获取Runtime实例
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process.waitFor() == 0) {
                log.info("Mysql 数据库备份成功,备份文件名：{}", mysqlFileName);
            } else {
                throw new NetworkException("网络异常，数据库备份失败");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NetworkException("网络异常，数据库备份失败");
        }
        return smb;
    }

    // 恢复数据库
    @Override
    public String rollback(SystemMysqlBackups smb, String userName, String password) {
        // 获得备份路径文件名
        StringBuilder realFilePath = new StringBuilder().append(smb.getBackupsPath()).append(smb.getBackupsName());
        if (!FileUtil.exist(String.valueOf(realFilePath))) {
            throw new FileException("文件不存在，恢复失败，请查看目录内文件是否存在后重新尝试！");
        }

        // 获得恢复命令
        StringBuilder cmd = new StringBuilder()
                .append("mysql -h")
                .append(smb.getMysqlIp())
                .append(" -u")
                .append(userName)
                .append(" -p")
                .append(password)
                .append(" ")
                .append(smb.getDatabaseName())
                .append(" < ")
                .append(realFilePath);

        String[] command = new String[]{"cmd", "/c", String.valueOf(cmd)};
        log.info("数据库恢复命令为：{}", cmd);

        // 恢复指令写入到数据库
        smb.setMysqlBackCmd(String.valueOf(cmd));
        // 更新操作次数
        smb.setRecoveryTime(LocalDateTime.now());
        smb.setOperation(smb.getOperation() + 1);
        // 获取Runtime实例
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process.waitFor() == 0) log.info("Mysql 数据库恢复成功,恢复文件名：{}", realFilePath);
             else throw new NetworkException("网络异常，恢复失败，请稍后重新尝试！");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NetworkException("网络异常，恢复失败，请稍后重新尝试！");
        }

        systemMysqlBackupsMapper.update(smb);
        return smb.getBackupsName();
    }

}