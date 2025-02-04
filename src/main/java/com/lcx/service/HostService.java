package com.lcx.service;


import com.lcx.domain.Entity.SingleScore;
import com.lcx.domain.VO.FinalSingleScore;
import com.lcx.domain.VO.GroupScore;
import com.lcx.domain.VO.SeatInfo;
import com.lcx.domain.VO.SignGroup;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HostService {

    void startCompetition(String group ,String zone);

    String nextProcess(String group ,String zone);

    void postWrittenScoreByExcel(MultipartFile file);

    List<SeatInfo> seatDraw(String group , String zone);

    List<SingleScore> scoreFilter(String group , String zone);

    List<SignGroup> groupDraw(String group , String zone);

    void exportScoreToPdf(String group ,String zone,HttpServletResponse response);

    void insertScoreInfo(String group, String zone);

    GroupScore getGroupScore(int aUid, int bUid);

    FinalSingleScore getQAndAScore(int uid);

    void getExcelTemplate(HttpServletResponse response) throws IOException;
}
