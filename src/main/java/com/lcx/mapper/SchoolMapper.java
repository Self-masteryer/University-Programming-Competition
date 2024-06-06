package com.lcx.mapper;

import com.lcx.pojo.Entity.School;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SchoolMapper {

    @Insert("insert into school (uid, name, num, `group`, zone) " +
            "value (#{uid},#{name},#{num},#{group},#{zone})")
    void insert(School school);

    @Select("select * from school where uid=#{uid}")
    School getByUId(int uid);

    @Update("update school set num=#{num} where id=#{id}")
    void updateNum(int id, int num);

    @Update("update school set num=0 where `group`=#{group} and zone=#{zone}")
    void resetNum(String group, String zone);
}
