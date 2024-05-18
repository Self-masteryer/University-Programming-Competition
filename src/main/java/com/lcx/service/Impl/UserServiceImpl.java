package com.lcx.service.Impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.constant.Role;
import com.lcx.common.constant.Time;
import com.lcx.common.exception.account.AccountNotEnabledException;
import com.lcx.common.exception.account.AccountNotFoundException;
import com.lcx.common.exception.ContestantNumIsFullException;
import com.lcx.common.exception.account.PasswordErrorException;
import com.lcx.common.exception.time.SignUpTimeErrorException;
import com.lcx.common.util.RandomStringUtils;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.SchoolMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.mapper.UserMapper;
import com.lcx.pojo.DTO.SignUpDTO;
import com.lcx.pojo.DTO.UserLoginDTO;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.Entity.School;
import com.lcx.pojo.Entity.User;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.VO.SignUpVO;
import com.lcx.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private SchoolMapper schoolMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public int login(UserLoginDTO userLoginDTO) {
        //通过用户名查询账号
        User user = userMapper.getByUsername(userLoginDTO.getUsername());
        // 账号不存在或账号未启用
        if (user == null) throw new AccountNotFoundException(ErrorMessageConstant.ACCOUNT_NOT_FIND);
        else if (user.getEnabled()==0) throw new AccountNotEnabledException(ErrorMessageConstant.ACCOUNT_NOT_ENABLED);
        //校验密码
        if (!BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword()))
            throw new PasswordErrorException(ErrorMessageConstant.PASSWORD_ERROR);
        //校验正确，返回id
        return user.getId();
    }

    @Override
    @Transactional
    public SignUpVO signUp(SignUpDTO signUpDTO) {
        String beginTime=stringRedisTemplate.opsForValue().get(Time.SIGN_UP_BEGIN_TIME);
        // 管理员未设置报名时间，未开启报名
        if(beginTime==null)
            throw new SignUpTimeErrorException(ErrorMessageConstant.SIGN_UP_TIME_ERROR);
        long begin = Long.parseLong(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(Time.SIGN_UP_BEGIN_TIME)));
        long end = Long.parseLong(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(Time.SIGN_UP_END_TIME)));
        long now = System.currentTimeMillis();
        // 报名时间异常
        if(now<begin||now>end)
            throw new SignUpTimeErrorException(ErrorMessageConstant.SIGN_UP_TIME_ERROR);

        // 判断选手数量是否已满
        int uId = StpUtil.getLoginIdAsInt();
        School school = schoolMapper.getByUId(uId);
        if (school.getNum() == 3)
            throw new ContestantNumIsFullException(ErrorMessageConstant.CONTESTANT_NUM_IS_FULL);

        // 未满额，判断选手账号是否存在
        UserInfo userInfo = userInfoMapper.getByIDCard(signUpDTO.getIdCard());
        SignUpVO signUpVO = new SignUpVO();
        // 不存在，创建
        if (userInfo == null) {
            // 创建用户
            String password = RandomStringUtils.length(8);
            User user = User.builder().username(RandomStringUtils.length(8))
                    .password(BCrypt.hashpw(password,BCrypt.gensalt()))
                    .name(signUpDTO.getName()).rid(Role.CONTESTANT).enabled(1).build();
            userMapper.insert(user);
            // 创建选手
            Contestant contestant=Contestant.builder().uid(user.getId()).sid(school.getId())
                    .name(signUpDTO.getName()).idCard(signUpDTO.getIdCard())
                    .group(school.getGroup()).zone(school.getZone()).build();
            contestantMapper.insert(contestant);
            // 创建用户信息
            userInfo = UserInfo.builder().uid(user.getId()).name(signUpDTO.getName())
                    .idCard(signUpDTO.getIdCard()).group(school.getGroup()).zone(school.getZone()).build();
            userInfoMapper.insert(userInfo);

            signUpVO.setName(signUpDTO.getName());
            signUpVO.setUsername(user.getUsername());
            signUpVO.setPassword(password);
        }
        // 账号已存在
        else {
            // 重置账号密码
            User user = userMapper.getById(userInfo.getUid());
            // 将账号身份设置为选手
            user.setRid(Role.CONTESTANT);
            String password = RandomStringUtils.length(8);
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            userMapper.update(user);
            // 创建选手
            Contestant contestant=Contestant.builder().uid(user.getId()).sid(school.getId()).name(signUpDTO.getName())
                    .idCard(signUpDTO.getIdCard()).group(school.getGroup()).zone(school.getZone()).build();
            contestantMapper.update(contestant);

            signUpVO.setName(signUpDTO.getName());
            signUpVO.setUsername(user.getUsername());
            signUpVO.setPassword(password);
        }
        // 学校选手数量加一
        schoolMapper.updateNum(school.getId(),school.getNum()+1);
        log.info("选手:{} 报名成功",signUpDTO.getName());
        return signUpVO;
    }

}
