package com.lcx.mapper;

import com.lcx.pojo.Entity.DistrictScore;
import com.lcx.pojo.VO.DistrictScoreVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DistrictScoreMapper {

    @Insert("insert into district_score (uid, seat_num) value (#{uid},#{seatNum});")
    void insert(DistrictScore districtScore);

    @Select("select * from district_score where uid = #{uid}")
    DistrictScore getByUid(int uid);

    @Update("update district_score set written_score=#{writtenScore} where id =#{id}")
    void updateWrittenScore(DistrictScore districtScore);

    List<DistrictScoreVO> getVOListByGroupAndZone(String group, String zone);

    @Select("select * from district_score where seat_num=#{seatNum}")
    DistrictScore getBySeatNum(String seatNum);

    @Delete("delete from district_score where uid=#{uid}")
    void deleteByUid(int uid);
}
