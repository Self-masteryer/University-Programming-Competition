package com.lcx.mapper;

import com.lcx.pojo.Entity.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserInfoMapper {
    @Select("select * from user_info where id_card=#{idCard}")
    UserInfo getByIDCard(String idCard);

    @Insert("insert into user_info (uid, name, id_card, `group`, zone, role) " +
            "value (#{uid},#{name},#{idCard},#{group},#{zone},#{role})")
    void insert(UserInfo userInfo);

    void update(UserInfo userInfo);

    @Select("select * from user_info where uid=#{uid}")
    UserInfo getByUid(int uid);
}
