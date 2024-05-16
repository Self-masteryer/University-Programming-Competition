package com.lcx.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

    void createUserByExcel(MultipartFile file, HttpServletResponse response);
}
