package com.lcx.service;


import com.lcx.pojo.Entity.SystemMysqlBackups;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SystemMysqlBackupsService{

    // 查询所有备份数据
    List<SystemMysqlBackups> selectBackupsList();

    // mysql备份接口
    Object mysqlBackups(String filePath, String url, String userName, String password,String databaseName);

    // 根据ID查询
    SystemMysqlBackups selectListId(int id);

    // 恢复数据库
    String rollback(SystemMysqlBackups smb, String userName, String password);

}
