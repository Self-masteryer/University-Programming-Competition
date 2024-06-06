package com.lcx.mapper;

import com.lcx.pojo.Entity.SingleScore;
import com.lcx.pojo.VO.SingleScoreVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WrittenScoreMapper {


    @Insert("insert into written_score (uid,name, `group`, zone, seat_num, score, ranking)" +
            "value (#{uid},#{name},#{group},#{zone},#{seatNum},#{score},#{ranking})")
    void insert(SingleScore singleScore);

    @Select("select name,`group`,zone,seat_num,score,ranking from written_score where uid=#{uid}")
    SingleScoreVO getVOByUid(int uid);

    @Delete("delete from written_score where `group`=#{group} and zone=#{zone}")
    void deleteByGroupAndZone(String group, String zone);
}
