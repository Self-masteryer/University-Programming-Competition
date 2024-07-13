package com.lcx.service;


import com.lcx.domain.DTO.ChangePwdDTO;
import com.lcx.domain.DTO.ResetAccountDTO;
import com.lcx.domain.DTO.SignUpDTO;
import com.lcx.domain.DTO.UserLoginDTO;
import com.lcx.domain.Entity.AccountInfo;
import com.lcx.domain.VO.SignUpVO;

public interface UserService {

    void login(UserLoginDTO userLoginDTO);

    SignUpVO signUp(SignUpDTO SignUpDTO);

    void resetUsernameAndPassword(ResetAccountDTO resetAccountDTO);

    void changePwd(ChangePwdDTO changePwdDTO);

    void updateInfo(AccountInfo accountInfo);

    AccountInfo getInfo();

}
