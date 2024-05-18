package com.lcx.mapper;

import com.lcx.pojo.Entity.Contestant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ContestantMapper {

    @Select("select * from contestant where id_card=#{idCard}")
    Contestant getByIDCard(String idCard);

    @Insert("insert into contestant (uid, sid, name, id_card, `group`, zone)" +
            "value (#{uid},#{sid},#{name},#{idCard},#{group},#{zone})")
    void insert(Contestant contestant);

    void update(Contestant contestant);

    @Select("select count(id) from contestant where `group`=#{group} and zone=#{zone}")
    int getCountByGroupAndZone(String group, String zone);

    @Select("select * from contestant where `group`=#{group} and zone=#{zone}")
    List<Contestant> getListByGroupAndZone(String group, String zone);

    @Delete("delete from contestant where uid=#{uid}")
    void deleteByUid(int uid);

}
