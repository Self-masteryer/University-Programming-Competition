package com.lcx.mapper;

import com.lcx.pojo.Entity.AccountInfo;
import com.lcx.pojo.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where username=#{username}")
    User getByUsername(String username);

    void insert(User user);

    @Update("update user set enabled=1 where id=#{uid}")
    void enable(int uid);

    @Select("select * from user where id=#{id}")
    User getById(int id);

    void update(User user);

    @Update("update user set rid=#{rid} where id=#{id}")
    void updateRole(int id, int rid);

    @Update("update user set password=#{password} where id=#{id}")
    void updatePwdById(int id, String password);

    @Select("select nickname,avatar from user where id=#{id}")
    AccountInfo getInfo(int id);
}
