package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemMysqlBackups {

    private int id;

    private String mysqlIp;

    private String mysqlPort;

    private String databaseName;

    private String mysqlCmd;// MySQL备份指令

    private String mysqlBackCmd;// MySQL恢复指令

    private String backupsPath;// MySQL备份存储地址

    private String backupsName;// MySQL备份文件名称

    private Integer operation;// 操作次数

    private Integer status;

    private LocalDateTime recoveryTime;

    private LocalDateTime createTime;

}