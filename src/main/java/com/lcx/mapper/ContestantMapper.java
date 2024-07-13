package com.lcx.mapper;

import com.lcx.domain.DAO.SignInfoDAO;
import com.lcx.domain.Entity.Contestant;
import com.lcx.domain.Entity.Student;
import com.lcx.domain.VO.GradeVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContestantMapper {

    @Select("select * from contestant where id_card=#{idCard}")
    Contestant getByIDCard(String idCard);

    @Insert("insert into contestant (uid, school, name, id_card, `group`, zone)" +
            "value (#{uid},#{school},#{name},#{idCard},#{group},#{zone})")
    void insert(Contestant contestant);

    int getCountByGroupAndZone(String group, String zone);

    @Select("select * from contestant where `group`=#{group} and zone=#{zone}")
    List<Contestant> getListByGroupAndZone(String group, String zone);

    @Delete("delete from contestant where uid=#{uid}")
    void deleteByUid(int uid);

    List<Student> getStudentListByGroupAndZone(String group, String zone);

    List<SignInfoDAO> getSignInfoListByGroupAndZone(String group, String zone);

    @Select("select uid from contestant where `group`=#{group} and zone=#{zone}")
    List<Integer> getUidListByGroupAndZone(String group, String zone);

    List<GradeVO> getScoreVoListByGroupAndZone(String group, String zone);

    void deleteByUidAndZone(Integer uid, String zone);

    @Select("select * from contestant where uid=#{uid}")
    Contestant getByUid(int uid);

    @Select("select school from contestant where uid=#{uid}")
    String getSchoolByUid(int uid);

    @Update("update contestant set zone = 'N' ")
    void setToNational();

    @Select("select uid from contestant")
    List<Integer> getUidList();

    @Delete("delete from contestant where `group`=#{group} and zone=#{zone}")
    void deleteByGroupAndZone(String group, String zone);
}
