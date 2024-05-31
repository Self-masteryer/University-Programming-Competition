package com.lcx.service;

import com.lcx.pojo.DTO.*;
import com.lcx.pojo.Entity.AccountInfo;
import com.lcx.pojo.VO.SignUpVO;

public interface UserService {

    void login(UserLoginDTO userLoginDTO);

    SignUpVO signUp(SignUpDTO SignUpDTO);

    void resetUsernameAndPassword(ResetAccountDTO resetAccountDTO);

    void changePwd(ChangePwdDTO changePwdDTO);

    void updateInfo(AccountInfo accountInfo);

    AccountInfo getInfo();

}
