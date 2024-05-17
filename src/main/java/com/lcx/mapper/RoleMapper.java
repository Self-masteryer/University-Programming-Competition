package com.lcx.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<String> list(Object uid);
}
