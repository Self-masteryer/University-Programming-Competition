package com.lcx.service;

import com.lcx.pojo.VO.FinalSingleScore;
import com.lcx.pojo.VO.GroupScore;
import com.lcx.pojo.VO.SeatInfo;
import com.lcx.pojo.VO.SignGroup;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HostService {

    void startCompetition(String group ,String zone);

    String nextProcess(String group ,String zone);

    void postWrittenScoreByExcel(MultipartFile file);

    List<SeatInfo> seatDraw(String group ,String zone);

    List<com.lcx.pojo.Entity.SingleScore> scoreFilter(String group , String zone);

    List<SignGroup> groupDraw(String group ,String zone);

    void exportScoreToPdf(String group ,String zone,HttpServletResponse response);

    void insertScoreInfo(String group, String zone);

    GroupScore getGroupScore(int aUid, int bUid);

    FinalSingleScore getQAndAScore(int uid);
}
