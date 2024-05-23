package com.lcx.service.Impl;

import cn.dev33.satoken.secure.BCrypt;
import com.lcx.common.constant.Prize;
import com.lcx.common.constant.Role;
import com.lcx.common.constant.Time;
import com.lcx.common.constant.Zone;
import com.lcx.common.util.ConvertUtil;
import com.lcx.common.util.RandomStringUtils;
import com.lcx.mapper.*;
import com.lcx.pojo.DTO.SignUpTime;
import com.lcx.pojo.Entity.*;
import com.lcx.pojo.VO.ScoreVo;
import com.lcx.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private SchoolMapper schoolMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private StudentScoreMapper studentScoreMapper;
    @Resource
    private ScoreInfoMapper scoreInfoMapper;
    @Resource
    private PreScoreMapper preScoreMapper;
    @Resource
    private QAndAScoreMapper qAndAScoreMapper;
    @Resource
    private PracticalScoreMapper practicalScoreMapper;

    @Override
    @Transactional
    public void createUserByExcel(MultipartFile file, HttpServletResponse response) {
        try {
            InputStream in = file.getInputStream();
            XSSFWorkbook inExcel = new XSSFWorkbook(in);
            XSSFWorkbook outExcel = new XSSFWorkbook();
            //sheet
            for (int i = 0; i < inExcel.getNumberOfSheets(); i++) {
                //创建数据输入sheet
                XSSFSheet inSheet = inExcel.getSheetAt(i);
                String r = inSheet.getSheetName();
                //创建账号输出sheet
                XSSFSheet outSheet = outExcel.createSheet(r);
                XSSFRow outRow1 = outSheet.createRow(0);
                outRow1.createCell(0).setCellValue("姓名");
                outRow1.createCell(1).setCellValue("用户名");
                outRow1.createCell(2).setCellValue("密码");

                int rid = ConvertUtil.parseRoleNum(r);
                for (int j = 1; j <= inSheet.getLastRowNum(); j++) {
                    XSSFRow inRow = inSheet.getRow(j);
                    XSSFRow outRow = outSheet.createRow(j);
                    String IDCard = inRow.getCell(1).getStringCellValue();
                    UserInfo userInfo = userInfoMapper.getByIDCard(IDCard);
                    //账号不存在
                    if (userInfo == null) {
                        //身份信息表
                        userInfo = new UserInfo();
                        userInfo.setIdCard(IDCard);//身份证
                        userInfo.setName(inRow.getCell(0).getStringCellValue());//姓名
                        userInfo.setGroup(ConvertUtil.parseGroupSimStr(inRow.getCell(2).getStringCellValue()));//组别
                        userInfo.setZone(ConvertUtil.parseZoneSimStr(inRow.getCell(3).getStringCellValue()));//赛区
                        //用户
                        User user = new User();
                        user.setUsername(RandomStringUtils.length(8));
                        String password = RandomStringUtils.length(8);
                        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                        user.setName(userInfo.getName());
                        user.setRid(rid);
                        user.setEnabled(1);
                        userMapper.insert(user);

                        userInfo.setUid(user.getId());
                        userInfoMapper.insert(userInfo);

                        //输出账号数据
                        outRow.createCell(0).setCellValue(user.getName());
                        outRow.createCell(1).setCellValue(user.getUsername());
                        outRow.createCell(2).setCellValue(password);
                    } else {
                        //账号存在，更新身份信息
                        userInfo.setZone(ConvertUtil.parseZoneSimStr(inRow.getCell(3).getStringCellValue()));
                        userInfo.setGroup(ConvertUtil.parseGroupSimStr(inRow.getCell(2).getStringCellValue()));
                        userInfoMapper.update(userInfo);
                        //更新用户信息
                        User user = userMapper.getById(userInfo.getUid());
                        user.setEnabled(1);//启用
                        String password = RandomStringUtils.length(8);
                        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));//修改密码
                        userMapper.update(user);

                        //输出账号数据
                        outRow.createCell(0).setCellValue(user.getName());
                        outRow.createCell(1).setCellValue(user.getUsername());
                        outRow.createCell(2).setCellValue(password);
                    }
                }
            }
            //传回账号数据
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=userAccount.xls");
            ServletOutputStream out = response.getOutputStream();
            outExcel.write(out);
            //关闭资源
            inExcel.close();
            outExcel.close();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void createSchoolByExcel(MultipartFile file, HttpServletResponse response) {
        try {
            InputStream in = file.getInputStream();
            XSSFWorkbook inExcel = new XSSFWorkbook(in);
            XSSFWorkbook outExcel = new XSSFWorkbook();
            //创建 输入sheet
            XSSFSheet inSheet = inExcel.getSheetAt(0);
            //创建 输出sheet
            XSSFSheet outSheet = outExcel.createSheet("学校");
            XSSFRow outRow1 = outSheet.createRow(0);
            outRow1.createCell(0).setCellValue("校名");
            outRow1.createCell(1).setCellValue("用户名");
            outRow1.createCell(2).setCellValue("密码");

            int role = Role.SCHOOL;
            for (int i = 1; i <= inSheet.getLastRowNum(); i++) {
                XSSFRow inRow = inSheet.getRow(i);
                XSSFRow outRow = outSheet.createRow(i);

                //学校信息表
                School school = new School();
                school.setName(inRow.getCell(0).getStringCellValue());
                school.setNum(0);
                school.setGroup(ConvertUtil.parseGroupSimStr(inRow.getCell(1).getStringCellValue()));//组别
                school.setZone(ConvertUtil.parseZoneSimStr(inRow.getCell(2).getStringCellValue()));//赛区
                //用户
                User user = new User();
                user.setUsername(RandomStringUtils.length(8));//账号
                String password = RandomStringUtils.length(8);
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));//密码
                user.setName(school.getName());//名称
                user.setRid(role);
                user.setEnabled(1);
                //插入用户表,自动设置Id
                userMapper.insert(user);

                school.setUid(user.getId());
                schoolMapper.insert(school);

                //输出账号数据
                outRow.createCell(0).setCellValue(user.getName());
                outRow.createCell(1).setCellValue(user.getUsername());
                outRow.createCell(2).setCellValue(password);

            }
            //传回账号数据
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=schoolAccount.xls");
            ServletOutputStream out = response.getOutputStream();
            outExcel.write(out);
            //关闭资源
            inExcel.close();
            outExcel.close();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setSignUpTime(SignUpTime signUpTime) {
        //设置报名时间
        String begin = ConvertUtil.parseDateStr(signUpTime.getBegin());
        String end = ConvertUtil.parseDateStr(signUpTime.getEnd());
        stringRedisTemplate.opsForValue().set(Time.SIGN_UP_BEGIN_TIME, begin);
        stringRedisTemplate.opsForValue().set(Time.SIGN_UP_END_TIME, end);
    }

    public void setToTourist() {
    }

    @Override
    public void addStudentScore(String group, String zone) {
        List<ScoreVo> scoreInfoList = contestantMapper
                .getScoreVoListByGroupAndZone(group, zone);
        scoreInfoList.sort(Comparator.comparingDouble(ScoreVo::getFinalScore).reversed());

        int session = Integer.parseInt(stringRedisTemplate.opsForValue().get("session"));
        for (int i = 0; i < 5; i++) {
            ScoreVo scoreVo = scoreInfoList.get(i);
            StudentScore studentScore = StudentScore.builder().name(scoreVo.getName()).idCard(scoreVo.getIdCard())
                    .school(scoreVo.getSchool()).session(session).score(scoreVo.getFinalScore()).build();
            if (i < 2) studentScore.setPrize(Prize.PROVINCIAL_FIRST_PRIZE);
            else studentScore.setPrize(Prize.PROVINCIAL_SECOND_PRIZE);

            studentScoreMapper.insert(studentScore);
        }
    }

    @Override
    public void addToNational(String group, String zone) {
        List<ScoreVo> scoreInfoList = contestantMapper
                .getScoreVoListByGroupAndZone(group, zone);
        scoreInfoList.sort(Comparator.comparingDouble(ScoreVo::getFinalScore).reversed());

        for (int i = 0; i < 5; i++) {
            ScoreVo scoreVo = scoreInfoList.get(i);
            Contestant contestant = Contestant.builder().uid(userInfoMapper.getUidByIDCard(scoreVo.getIdCard()))
                    .name(scoreVo.getName()).school(scoreVo.getSchool()).idCard(scoreVo.getIdCard())
                    .group(scoreVo.getGroup()).zone(Zone.N).build();

            contestantMapper.insert(contestant);
        }
    }

    @Override
    public void addPreScore(String group, String zone) {
        List<Integer> uidList = contestantMapper.getUidListByGroupAndZone(group, zone);
        List<ScoreInfo> scoreInfoList = new ArrayList<>();
        for (Integer uid : uidList) scoreInfoList.add(scoreInfoMapper.getByUid(uid));
        scoreInfoList.sort(Comparator.comparingDouble(ScoreInfo::getFinalScore).reversed());
        for (int i = 0; i < scoreInfoList.size(); i++) {
            ScoreInfo scoreInfo = scoreInfoList.get(i);
            PreScore preScore = new PreScore();
            BeanUtils.copyProperties(scoreInfo, preScore);
            preScore.setRanking(i + 1);

            preScoreMapper.insert(preScore);
        }
    }

    @Override
    public void deleteScore(String group, String zone) {
        List<Integer> uidList = contestantMapper.getUidListByGroupAndZone(group, zone);
        for(Integer uid : uidList) {
            scoreInfoMapper.deleteByUid(uid);
            practicalScoreMapper.deleteByUid(uid);
            qAndAScoreMapper.deleteByUid(uid);
            contestantMapper.deleteByUidAndZone(uid,zone);
        }
    }
}
