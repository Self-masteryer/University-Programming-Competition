package com.lcx.mapper;

import com.lcx.pojo.DAO.ScoreDAO;
import com.lcx.pojo.Entity.ScoreInfo;
import com.lcx.pojo.Entity.SingleScore;
import com.lcx.pojo.VO.CommonScore;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScoreInfoMapper {

    @Insert("insert into score_info (uid,`group`,zone,session) value (#{uid},#{group},#{zone},#{session});")
    void insert(ScoreInfo scoreInfo);

    @Select("select * from score_info where uid = #{uid}")
    ScoreInfo getByUid(int uid);

    @Update("update score_info set written_score=#{writtenScore} where id =#{id}")
    void updateWrittenScore(ScoreInfo scoreInfo);

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

    @Update("update score_info set final_score=#{finalScore} where uid=#{uid}")
    void updateFinalScore(int uid,float finalScore);

    @Select("select seat_num from score_info where uid=#{uid}")
    String getSeatNum(int uid);

    @Select("select sign_num from score_info where uid=#{uid}")
    String getSignNum(int uid);

    @Update("update score_info set seat_num=#{seatNum} where uid=#{uid}")
    void updateSeatNum(int uid, String seatNum);

    List<SingleScore> getWrittenScoreList(String group, String zone);

    CommonScore getPracticalScoreByUid(int uid);

    CommonScore getQAndAScoreByUid(int uid);

    @Select("select id from score_info where uid=#{uid}")
    int getId(int uid);

    @Select("select uid,written_score,practical_score,q_and_a_score from score_info where `group`=#{grou} and zone=#{zone}")
    List<ScoreDAO> getScoreDAOByUid(String group, String zone);
}
