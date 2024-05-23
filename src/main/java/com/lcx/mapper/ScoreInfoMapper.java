package com.lcx.mapper;

import com.lcx.pojo.DAO.ScoreDAO;
import com.lcx.pojo.Entity.ScoreInfo;
import com.lcx.pojo.VO.WrittenScore;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScoreInfoMapper {

    @Insert("insert into score_info (uid,session,seat_num) value (#{uid},#{session},#{seatNum});")
    void insert(ScoreInfo scoreInfo);

    @Select("select * from score_info where uid = #{uid}")
    ScoreInfo getByUid(int uid);

    @Update("update score_info set written_score=#{writtenScore} where id =#{id}")
    void updateWrittenScore(ScoreInfo scoreInfo);

    List<WrittenScore> getVOListByGroupAndZone(String group, String zone);

    @Select("select * from score_info where seat_num=#{seatNum}")
    ScoreInfo getBySeatNum(String seatNum);

    @Delete("delete from score_info where uid=#{uid}")
    void deleteByUid(int uid);

    @Update("update score_info set sign_num=#{signNum} where uid=#{uid}")
    void updateSignNumByUid(int uid, String signNum);

    @Update("update score_info set practical_score=#{practicalScore} where uid=#{uid}")
    void updatePracticalScoreByUid(int uid, float practicalScore);

    @Update("update score_info set q_and_a_score=#{qAndAScore} where uid=#{uid}")
    void updateQAndAScoreByUid(int uid, float qAndAScore);

    @Select("select written_score,practical_score,q_and_a_score from score_info where uid=#{uid}")
    ScoreDAO getScoreDAOByUid(int uid);

    @Update("update score_info set final_score=#{finalScore} where uid=#{uid} and zone=#{zone}")
    void updateFinalScore(int uid, String zone, float finalScore);

}
