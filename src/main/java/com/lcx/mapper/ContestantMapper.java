package com.lcx.mapper;

import com.lcx.pojo.DAO.SignInfoDAO;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.Entity.Student;
import com.lcx.pojo.VO.GrageVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    List<GrageVO> getScoreVoListByGroupAndZone(String group, String zone);

    void deleteByUidAndZone(Integer uid, String zone);
}
