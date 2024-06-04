package com.lcx.mapper;

import com.lcx.pojo.DAO.BackupsFilePath;
import com.lcx.pojo.Entity.SystemMysqlBackups;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SystemMysqlBackupsMapper {

    @Select("select * from mysql_backups where status != 0 order by create_time desc")
    List<SystemMysqlBackups> selectBackupsList();

    @Select("select * from mysql_backups where status != 0 and id = #{id}")
    SystemMysqlBackups selectListId(int id);

    void insert(SystemMysqlBackups smb);

    @Update("update mysql_backups " +
            "set mysql_back_cmd=#{mysqlBackCmd},operation=#{operation},recovery_time=#{recoveryTime}" +
            " where id=#{id}")
    void update(SystemMysqlBackups smb);

    @Select("select backups_path as path,backups_name as fileName from mysql_backups " +
            "where date(create_time) <= date_sub(curdate(),interval #{day} day)")
    List<BackupsFilePath> getExpiredData(int day);

    @Delete("delete from mysql_backups where backups_name=#{fileName}")
    void deleteByFileName(String fileName);
}
