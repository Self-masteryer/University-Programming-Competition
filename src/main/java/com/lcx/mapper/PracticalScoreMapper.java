package com.lcx.mapper;

import com.lcx.pojo.Entity.PracticalScore;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PracticalScoreMapper {

    @Insert("insert into practical_score (uid, sid, jid, score) " +
            "value (#{uid},#{sid},#{jid},#{score})")
    void insert(PracticalScore practicalScore);

}
