package com.lcx.service;

import com.lcx.pojo.DTO.SignUpTime;
import com.lcx.pojo.VO.ScoreVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    void createUserByExcel(MultipartFile file, HttpServletResponse response);

    void createSchoolByExcel(MultipartFile file, HttpServletResponse response);

    void setSignUpTime(SignUpTime signUpTime);

    void addStudentScore(String group, String zone);

    void addToNational(String group, String zone);

    void addPreScore(String group, String zone);

    void deleteScore(String group, String zone);
}
