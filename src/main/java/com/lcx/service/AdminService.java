package com.lcx.service;


import com.lcx.pojo.DTO.SignUpTime;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

    void addUserByExcel(MultipartFile file, HttpServletResponse response);

    void addSchoolByExcel(MultipartFile file, HttpServletResponse response);

    void setSignUpTime(SignUpTime signUpTime);

    void startNationalCompetition();

    void setAsTourist(String group, String zone);

}
