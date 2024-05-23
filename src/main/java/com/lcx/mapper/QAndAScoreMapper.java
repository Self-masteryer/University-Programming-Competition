package com.lcx.mapper;

import com.lcx.pojo.Entity.Score;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QAndAScoreMapper {

    @Insert("insert into q_and_a_score (uid, sid, jid, score)" +
            "value (#{uid},#{sid},#{jid},#{score})")
    void insert(Score score);

    @Select("select count(id) from q_and_a_score where uid=#{uid}")
    int getCountByUid(int uid);

    @Select("select score from q_and_a_score where uid=#{uid}")
    List<Float> getScoresByUid(int uid);

    @Select("select count(id) from q_and_a_score where uid=#{uid} and jid=#{jid}")
    int checkTime(int uid, int jid);

    int getCountByUidList(List<Integer> uidList);

    @Delete("delete from q_and_a_score where uid=#{uid}")
    void deleteByUid(Integer uid);
}
