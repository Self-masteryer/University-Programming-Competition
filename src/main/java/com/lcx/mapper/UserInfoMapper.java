package com.lcx.mapper;


import com.lcx.domain.Entity.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    @Select("select * from user_info where id_card=#{idCard}")
    UserInfo getByIDCard(String idCard);

    @Insert("insert into user_info (uid, name, id_card, `group`, zone) " +
            "value (#{uid},#{name},#{idCard},#{group},#{zone})")
    void insert(UserInfo userInfo);

    void update(UserInfo userInfo);

    @Select("select * from user_info where uid=#{uid}")
    UserInfo getByUid(int uid);

    @Select("select uid from user_info where id_card=#{idCard}")
    int getUidByIDCard(String idCard);

    @Select("select uid from user_info where `group`=#{group} and zone=#{zone}")
    List<Integer> getUidListByGroupAndZone(String group, String zone);

    @Select("select name from user_info where uid=#{uid}")
    String getNameByUid(int uid);

    @Update("update user_info set zone='N' where uid=#{uid}")
    void setToNational(Integer uid);
}
