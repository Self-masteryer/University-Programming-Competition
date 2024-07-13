package com.lcx.mapper;

import com.github.pagehelper.Page;

import com.lcx.domain.DTO.StatusPageQuery;
import com.lcx.domain.Entity.AccountInfo;
import com.lcx.domain.Entity.User;
import com.lcx.domain.VO.StatusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

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

    @Update("update user set status=#{status} , online_time=#{onlineTime} where id=#{id}")
    void updateStatus(int id,int status, LocalDateTime onlineTime);

    Page<StatusVO> queryStatusVO(StatusPageQuery statusPageQuery);
}
