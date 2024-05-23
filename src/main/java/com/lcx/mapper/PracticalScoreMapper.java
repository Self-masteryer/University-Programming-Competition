package com.lcx.mapper;

import com.lcx.pojo.Entity.Score;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PracticalScoreMapper {

    @Insert("insert into practical_score (uid, sid, jid, score) " +
            "value (#{uid},#{sid},#{jid},#{score})")
    void insert(Score score);

    int getCountByUidList(List<Integer> uidList);

    @Select("select score from practical_score where uid=#{uid}")
    List<Float> getScoresByUid(int uid);

    @Select("select count(id) from practical_score where uid=#{uid} and jid=#{jid}")
    int checkTime(int uid, int jid);

    @Delete("delete from practical_score where uid=#{uid}")
    void deleteByUid(Integer uid);

}
