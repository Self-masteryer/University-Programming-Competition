package com.lcx.mapper;

import com.github.pagehelper.Page;
import com.lcx.pojo.DTO.PreScorePageQuery;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.Entity.PreScore;
import com.lcx.pojo.VO.PreScoreVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PreScoreMapper {

    @Insert("insert into  pre_score (uid, school,session,`group`,zone, seat_num, sign_num, written_score, practical_score, q_and_a_score, final_score, ranking) " +
            "value (#{uid},#{school},#{session},#{group},#{zone},#{seatNum},#{signNum},#{writtenScore},#{practicalScore},#{qAndAScore},#{finalScore},#{ranking})")
    void insert(PreScore preScore);

    Page<PreScoreVO> pageQuery(PreScorePageQuery preScorePageQuery);

    @Select("select u.name,p.`group`,p.zone,p.seat_num,p.sign_num,p.session,p.ranking,p.written_score,p.practical_score,p.q_and_a_score,p.final_score\n" +
            "from pre_score p,user_info u where p.uid=u.uid and p.uid=#{uid}")
    List<PreScoreVO> getListByUid(int uid);

    Contestant getContestant(String session, String group, String zone, String ranking);

    @Select("select uid from pre_score where ranking > 5")
    List<Integer> getUnqualUidList(String group, String zone);
}
