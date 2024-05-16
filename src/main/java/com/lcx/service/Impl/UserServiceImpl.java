package com.lcx.service.Impl;

import cn.dev33.satoken.secure.BCrypt;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.exception.AccountNotFoundException;
import com.lcx.common.exception.PasswordErrorException;
import com.lcx.mapper.UserMapper;
import com.lcx.pojo.DTO.UserLoginDTO;
import com.lcx.pojo.Entity.User;
import com.lcx.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public int login(UserLoginDTO userLoginDTO) {
        //通过用户名查询用户
        User user=userMapper.getByUsername(userLoginDTO.getUsername());
        //用户不存在
        if(user==null) throw new AccountNotFoundException(ErrorMessageConstant.ACCOUNT_NOT_FIND);
        //校验密码
        if(!BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword()))
            throw new PasswordErrorException(ErrorMessageConstant.PASSWORD_ERROR);
        //校验正确，返回id
        return user.getId();
    }


}
