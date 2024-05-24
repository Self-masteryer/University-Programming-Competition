package com.lcx.service;

import com.lcx.common.result.PageResult;
import com.lcx.pojo.DTO.StudentScorePageQuery;
import com.lcx.pojo.DTO.SignUpTime;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

    void addUserByExcel(MultipartFile file, HttpServletResponse response);

    void addSchoolByExcel(MultipartFile file, HttpServletResponse response);

    void setSignUpTime(SignUpTime signUpTime);

    void addStudentScore(String group, String zone);

    void deleteScore(String group, String zone);

    void setAsTourist(String group, String zone);

    PageResult pageQueryStudentScore(StudentScorePageQuery studentScorePageQuery);

    void updateRole(int uid, int rid);

    void startNationalCompetition();

}
