package com.lcx.service;

import com.lcx.pojo.DTO.SignUpTime;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

    void createUserByExcel(MultipartFile file, HttpServletResponse response);

    void createSchoolByExcel(MultipartFile file, HttpServletResponse response);

    void setSignUpTime(SignUpTime signUpTime);
}
