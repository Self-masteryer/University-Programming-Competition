package com.lcx.mapper;

import com.lcx.pojo.Entity.DistrictScore;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DistrictScoreMapper {

    @Insert("insert into district_score (uid, seat_num) value (#{uid},#{seatNum});")
    void insert(DistrictScore districtScore);
}
