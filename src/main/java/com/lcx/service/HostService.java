package com.lcx.service;

import com.lcx.pojo.VO.WrittenScore;
import com.lcx.pojo.VO.SeatInfo;
import com.lcx.pojo.VO.SignGroup;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HostService {

    void startCompetition();

    String nextProcess();

    void postWrittenScoreByExcel(MultipartFile file);

    List<SeatInfo> seatDraw();

    List<WrittenScore> scoreFilter();

    List<SignGroup> groupDraw();

    void exportScoreToPdf(HttpServletResponse response);
}
