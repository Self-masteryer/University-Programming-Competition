package com.lcx.service;


import com.lcx.common.result.PageResult;
import com.lcx.pojo.DTO.CompInfoDTO;
import com.lcx.pojo.DTO.SignUpTime;
import com.lcx.pojo.DTO.StatusPageQuery;
import com.lcx.pojo.VO.ProcessVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    void addUserByExcel(MultipartFile file, HttpServletResponse response);

    void addSchoolByExcel(MultipartFile file, HttpServletResponse response);

    void setSignUpTime(SignUpTime signUpTime);

    void startNationalCompetition();

    void setAsTourist(String group, String zone);

    List<ProcessVO> queryProcess(String group, String zone);

    PageResult queryStatus(StatusPageQuery statusPageQuery);
}
