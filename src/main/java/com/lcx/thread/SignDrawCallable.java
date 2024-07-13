package com.lcx.thread;

import com.lcx.domain.Entity.Contestant;
import com.lcx.domain.VO.SeatInfo;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.ScoreInfoMapper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

public class SignDrawCallable implements Callable<List<SeatInfo>> {

    private String group;
    private String zone;
    private ContestantMapper contestantMapper;
    private ScoreInfoMapper scoreInfoMapper;

    public SignDrawCallable(ContestantMapper contestantMapper, ScoreInfoMapper scoreInfoMapper, String group, String zone) {
        this.contestantMapper = contestantMapper;
        this.group = group;
        this.scoreInfoMapper = scoreInfoMapper;
        this.zone = zone;
    }

    @Override
    public List<SeatInfo>call() throws Exception {
        int count = contestantMapper.getCountByGroupAndZone(group, zone); // 组别赛区选手总数
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= count; i++) nums.add(i);
        Collections.shuffle(nums);

        List<Contestant> list = contestantMapper.getListByGroupAndZone(group,zone);
        List<SeatInfo> seatTable = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Contestant contestant = list.get(i);
            String seatNum = group + ":" + zone + ":" + nums.get(i);
            scoreInfoMapper.updateSeatNum(contestant.getUid(), seatNum);
            //座位信息

            SeatInfo seatInfo = SeatInfo.builder().name(contestant.getName()).seatNum(seatNum).build();
            seatTable.add(seatInfo);
        }
        //按座位号升序排序
        seatTable.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getSeatNum().substring(o.getSeatNum().lastIndexOf(":")+1))));

        return seatTable;
    }
}
