package com.lcx.service;

import com.lcx.pojo.DTO.SignUpDTO;
import com.lcx.pojo.DTO.UserLoginDTO;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.VO.SignUpVO;

public interface UserService {

    int login(UserLoginDTO userLoginDTO);

    SignUpVO signUp(SignUpDTO SignUpDTO);
}
