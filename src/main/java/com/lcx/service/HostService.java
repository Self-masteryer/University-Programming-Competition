package com.lcx.service;

import com.lcx.pojo.VO.DistrictScoreVO;
import com.lcx.pojo.VO.SeatInfo;
import com.lcx.pojo.VO.SignGroup;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HostService {

    void startCompetition();

    void nextProcess(String process);

    void postWrittenScoreByExcel(MultipartFile file);

    List<SeatInfo> seatDraw();

    List<DistrictScoreVO> scoreFilter();

    List<SignGroup> groupDraw();

}
