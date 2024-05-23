package com.lcx.mapper;

import com.lcx.pojo.Entity.PreScore;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PreScoreMapper {

    @Insert("insert into  pre_score (uid, session, zone, seat_num, sign_num, written_score, practical_score, q_and_a_score, final_score, ranking) " +
            "value (#{uid},#{session},#{zone},#{seatNum},#{signNum},#{writtenScore},#{practicalScore},#{qAndAScore},#{finalScore},#{ranking})")
    void insert(PreScore preScore);
}
