package com.lcx.service.Impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.*;
import com.lcx.common.exception.account.AccountException;
import com.lcx.common.exception.ContestantNumIsFullException;
import com.lcx.common.exception.account.PasswordException;
import com.lcx.common.exception.account.UsernameExistsException;
import com.lcx.common.exception.account.UsernameModifiableException;
import com.lcx.common.exception.time.TimePeriodErrorException;
import com.lcx.common.utils.RandomStringUtils;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.SchoolMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.mapper.UserMapper;
import com.lcx.pojo.DTO.*;
import com.lcx.pojo.Entity.*;
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
    public void login(UserLoginDTO userLoginDTO) {
        // 通过用户名查询账号
        User user = userMapper.getByUsername(userLoginDTO.getUsername());
        // 账号不存在或账号未启用
        if (user == null) throw new AccountException(ErrorMessage.ACCOUNT_NOT_FIND);
        else if (user.getEnabled() == 0) throw new AccountException(ErrorMessage.ACCOUNT_NOT_ENABLED);
        // 校验密码
        if (!BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword()))
            throw new PasswordException(ErrorMessage.PASSWORD_ERROR);

        // 登录
        StpUtil.login(user.getId());

        // 认证登录完成，添加session:主持人、评委、选手、学校
        if (user.getRid() == Role.HOST || user.getRid() == Role.JUDGEMENT || user.getRid() == Role.CONTESTANT) {
            UserInfo userInfo = userInfoMapper.getByUid(user.getId());
            StpUtil.getSession().set(Group.GROUP, userInfo.getGroup());
            StpUtil.getSession().set(Zone.ZONE, userInfo.getZone());
        } else if(user.getRid() == Role.SCHOOL){
            School school = schoolMapper.getByUId(user.getId());
            StpUtil.getSession().set(Group.GROUP, school.getGroup());
            StpUtil.getSession().set(Zone.ZONE, school.getZone());
        }
        // 添加角色id
        StpUtil.getSession().set(Role.ROLE, user.getRid());
    }

    @Override
    @Transactional
    public SignUpVO signUp(SignUpDTO signUpDTO) {
        String beginTime = stringRedisTemplate.opsForValue().get(Time.SIGN_UP_BEGIN_TIME);
        if (beginTime == null)
            throw new TimePeriodErrorException(ErrorMessage.SIGN_UP_TIME_ERROR);
        long begin = Long.parseLong(Objects.requireNonNull(beginTime));
        long end = Long.parseLong(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(Time.SIGN_UP_END_TIME)));
        long now = System.currentTimeMillis();
        // 报名时间异常
        if (now < begin || now > end)
            throw new TimePeriodErrorException(ErrorMessage.SIGN_UP_TIME_ERROR);

        // 判断选手数量是否已满
        int uId = StpUtil.getLoginIdAsInt();
        School school = schoolMapper.getByUId(uId);
        if (school.getNum() == 3)
            throw new ContestantNumIsFullException(ErrorMessage.CONTESTANT_NUM_IS_FULL);

        // 未满额，判断选手账号是否存在
        UserInfo userInfo = userInfoMapper.getByIDCard(signUpDTO.getIdCard());
        SignUpVO signUpVO = new SignUpVO();
        User user;
        // 不存在，创建
        if (userInfo == null) {
            // 创建用户
            String password = RandomStringUtils.length(8);
            user = User.builder().username(RandomStringUtils.length(8))
                    .password(BCrypt.hashpw(password, BCrypt.gensalt())).usernameModifiable(1)
                    .nickname(signUpDTO.getName()).rid(Role.CONTESTANT).enabled(1).build();
            userMapper.insert(user);

            // 创建用户信息
            userInfo = UserInfo.builder().uid(user.getId()).name(signUpDTO.getName())
                    .idCard(signUpDTO.getIdCard()).group(school.getGroup()).zone(school.getZone()).build();
            userInfoMapper.insert(userInfo);

            signUpVO.setPassword(password);
        }
        // 账号已存在
        else {
            user = userMapper.getById(userInfo.getUid());
            // 重置密码
            if (signUpDTO.getResetPwd() == 1) {
                String password = RandomStringUtils.length(8);
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                signUpVO.setPassword(password);
            }
            // 将账号身份设置为选手
            user.setRid(Role.CONTESTANT);
            // 再次参赛，可修改一次用户名
            user.setUsernameModifiable(1);
            userMapper.update(user);
        }

        // 创建选手
        Contestant contestant = Contestant.builder().uid(user.getId()).school(school.getName())
                .name(signUpDTO.getName()).idCard(signUpDTO.getIdCard())
                .group(school.getGroup()).zone(school.getZone()).build();
        contestantMapper.insert(contestant);

        signUpVO.setName(signUpDTO.getName());
        signUpVO.setUsername(user.getUsername());

        // 学校选手数量加一
        schoolMapper.updateNum(school.getId(), school.getNum() + 1);
        log.info("选手:{} 报名成功", signUpDTO.getName());
        return signUpVO;
    }

    // 重置用户名和密码
    @Override
    public void resetUsernameAndPassword(ResetAccountDTO resetAccountDTO) {

        String username = resetAccountDTO.getUsername();
        String password = resetAccountDTO.getPassword();
        String confirmPassword = resetAccountDTO.getConfirmPassword();

        // 检查能否修改用户名
        User user = userMapper.getById(StpUtil.getLoginIdAsInt());
        if (user.getUsernameModifiable() == 0)
            throw new UsernameModifiableException(ErrorMessage.USERNAME_HAS_BEEN_MODIFIED_ONCE);

        user = userMapper.getByUsername(username);
        // 检查用户名是否已存在
        if (user != null)
            throw new UsernameExistsException(ErrorMessage.USERNAME_ALREADY_EXISTS);

        // 检查两次密码是否一致
        if (!confirmPassword.equals(password))
            throw new PasswordException(ErrorMessage.PASSWORD_INCONSISTENCY);

        user = User.builder().id(StpUtil.getLoginIdAsInt()).username(username)
                .password(BCrypt.hashpw(password, BCrypt.gensalt())).usernameModifiable(0).build();
        userMapper.update(user);

        // 强制退出，重新登录
        StpUtil.logout(StpUtil.getLoginId());
    }

    @Override
    public void changePwd(ChangePwdDTO changePwdDTO) {
        // 检验旧密码是否正确
        User user = userMapper.getById(StpUtil.getLoginIdAsInt());
        String candidatedPassword = changePwdDTO.getOldPassword();
        if (!BCrypt.checkpw(candidatedPassword, user.getPassword()))
            throw new PasswordException(ErrorMessage.PASSWORD_ERROR);

        // 检验新密码是否一致
        String newPassword = changePwdDTO.getNewPassword();
        String confirmPassword = changePwdDTO.getConfirmPassword();
        if (!confirmPassword.equals(newPassword))
            throw new PasswordException(ErrorMessage.PASSWORD_INCONSISTENCY);

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userMapper.updatePwdById(StpUtil.getLoginIdAsInt(), hashedPassword);

        // 强制退出，重新登录
        StpUtil.logout(StpUtil.getLoginId());
    }

    @Override
    public AccountInfo getInfo() {
        return userMapper.getInfo(StpUtil.getLoginIdAsInt());
    }

    @Override
    public void updateInfo(AccountInfo accountInfo) {
        User user = User.builder().id(StpUtil.getLoginIdAsInt()).nickname(accountInfo.getNickname())
                .avatar(accountInfo.getAvatar()).build();
        userMapper.update(user);
    }

}
