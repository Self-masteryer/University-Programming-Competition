package com.lcx.mapper;


import com.lcx.domain.DTO.ScoreInfoQuery;
import com.lcx.domain.Entity.Score;
import com.lcx.domain.VO.SingeScoreInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QAndAScoreMapper {

    @Insert("insert into q_and_a_score(sign_num,uid,contestant_name,sid,jid,judgement_name,score) " +
            "value (#{signNum},#{uid},#{contestantName},#{sid},#{jid},#{judgementName},#{score})")
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

    List<SingeScoreInfo> getScoreInfoList(ScoreInfoQuery scoreInfoQuery);
}
