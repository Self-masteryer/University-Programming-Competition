package com.lcx.service.Impl;

import cn.dev33.satoken.secure.BCrypt;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.exception.process.ProcessStatusException;
import com.lcx.common.result.PageResult;
import com.lcx.common.util.ConvertUtil;
import com.lcx.common.util.RandomStringUtils;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.*;
import com.lcx.pojo.DTO.ScoreInfoQuery;
import com.lcx.pojo.DTO.ScoreQuery;
import com.lcx.pojo.DTO.TimePeriod;
import com.lcx.pojo.DTO.StatusPageQuery;
import com.lcx.pojo.Entity.*;
import com.lcx.pojo.VO.FinalSingleScore;
import com.lcx.pojo.VO.ProcessVO;
import com.lcx.pojo.VO.SingeScoreInfo;
import com.lcx.pojo.VO.StatusVO;
import com.lcx.service.AdminService;
import com.lcx.service.HostService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private HostService hostService;
    @Resource
    private PracticalScoreMapper practicalScoreMapper;
    @Resource
    private QAndAScoreMapper qAndAScoreMapper;
    @Resource
    private ScoreInfoMapper scoreInfoMapper;

    @Override
    @Transactional
    public void addUserByExcel(MultipartFile file, HttpServletResponse response) {
        try {
            InputStream in = file.getInputStream();
            XSSFWorkbook inExcel = new XSSFWorkbook(in);
            XSSFWorkbook outExcel = new XSSFWorkbook();
            // sheet
            for (int i = 0; i < inExcel.getNumberOfSheets(); i++) {
                // 创建数据输入sheet
                XSSFSheet inSheet = inExcel.getSheetAt(i);
                if(inSheet==null) break;
                String r = inSheet.getSheetName();
                // 创建账号输出sheet
                XSSFSheet outSheet = outExcel.createSheet(r);
                XSSFRow outRow1 = outSheet.createRow(0);
                outRow1.createCell(0).setCellValue("姓名");
                outRow1.createCell(1).setCellValue("用户名");
                outRow1.createCell(2).setCellValue("密码");

                int rid = ConvertUtil.parseRoleNum(r);
                for (int j = 1; j <= inSheet.getLastRowNum(); j++) {
                    XSSFRow inRow = inSheet.getRow(j);
                    // 缺少必填数据
                    if(inRow.getLastCellNum()<4) continue;
                    XSSFRow outRow = outSheet.createRow(j);
                    String IDCard = inRow.getCell(1).getStringCellValue();
                    UserInfo userInfo = userInfoMapper.getByIDCard(IDCard);

                    // 账号不存在
                    if (userInfo == null) {
                        // 身份信息
                        userInfo = new UserInfo();
                        userInfo.setIdCard(IDCard);//身份证
                        userInfo.setName(inRow.getCell(0).getStringCellValue());//姓名
                        userInfo.setGroup(ConvertUtil.parseGroupSimStr(inRow.getCell(2).getStringCellValue()));//组别
                        userInfo.setZone(ConvertUtil.parseZoneSimStr(inRow.getCell(3).getStringCellValue()));//赛区

                        //账号
                        User user = new User();
                        user.setUsername(RandomStringUtils.length(8));
                        String password = RandomStringUtils.length(8);
                        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                        user.setNickname(userInfo.getName());
                        user.setRid(rid);
                        user.setEnabled(1);
                        user.setUsernameModifiable(1);
                        userMapper.insert(user);

                        userInfo.setUid(user.getId());
                        userInfoMapper.insert(userInfo);

                        //输出账号数据
                        outRow.createCell(0).setCellValue(userInfo.getName());
                        outRow.createCell(1).setCellValue(user.getUsername());
                        outRow.createCell(2).setCellValue(password);
                    } else {
                        //账号存在，更新身份信息
                        userInfo.setGroup(ConvertUtil.parseGroupSimStr(inRow.getCell(2).getStringCellValue()));
                        userInfo.setZone(ConvertUtil.parseZoneSimStr(inRow.getCell(3).getStringCellValue()));
                        userInfoMapper.update(userInfo);
                        //更新用户信息
                        User user = userMapper.getById(userInfo.getUid());
                        user.setEnabled(1);//启用
                        // 再次参与，可修改一次用户名
                        user.setUsernameModifiable(1);
                        // 设置角色
                        user.setRid(rid);
                        // 重置密码（有填写才进程判断）
                        if (inRow.getLastCellNum()==5&&Objects.equals(inRow.getCell(4).getStringCellValue(),"是")) {
                            String password = RandomStringUtils.length(8);
                            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                            outRow.createCell(2).setCellValue(password);
                        }
                        userMapper.update(user);

                        //输出账号数据
                        outRow.createCell(0).setCellValue(userInfo.getName());
                        outRow.createCell(1).setCellValue(user.getUsername());
                    }
                }
            }
            //传回账号数据
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=userAccount.xlsx");
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
    public void addSchoolByExcel(MultipartFile file, HttpServletResponse response) {
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
                if(inRow.getLastCellNum()!=3) break;
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
                user.setNickname(school.getName());//名称
                user.setRid(role);
                user.setEnabled(1);
                user.setUsernameModifiable(1);
                //插入用户表,自动设置Id
                userMapper.insert(user);

                school.setUid(user.getId());
                schoolMapper.insert(school);

                //输出账号数据
                outRow.createCell(0).setCellValue(user.getNickname());
                outRow.createCell(1).setCellValue(user.getUsername());
                outRow.createCell(2).setCellValue(password);

            }
            //传回账号数据
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=schoolAccount.xlsx");
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
    public void setSignUpTime(TimePeriod timePeriod) {
        // 设置报名时间
        String begin = ConvertUtil.parseDateTimeStr(timePeriod.getBegin());
        String end = ConvertUtil.parseDateTimeStr(timePeriod.getEnd());
        stringRedisTemplate.opsForValue().set(Time.SIGN_UP_BEGIN_TIME, begin);
        stringRedisTemplate.opsForValue().set(Time.SIGN_UP_END_TIME, end);

        // 将比赛设置为区赛
        stringRedisTemplate.opsForValue().set("competition", Process.DISTRICT);

        //届数加一
        String session = RedisUtil.stringNumAddOne(stringRedisTemplate.opsForValue().get("session"));
        stringRedisTemplate.opsForValue().set("session", session);
    }

    @Override
    @Transactional
    public void setWaiverNatQualTime(TimePeriod timePeriod) {
        String begin = ConvertUtil.parseDateTimeStr(timePeriod.getBegin());
        String end = ConvertUtil.parseDateTimeStr(timePeriod.getEnd());
        stringRedisTemplate.opsForValue().set(Time.WAIVER_NAT_QUAL_BEGIN_TIME, begin);
        stringRedisTemplate.opsForValue().set(Time.WAIVER_NAT_QUAL_END_TIME, end);
    }

    // 开启国赛
    @Override
    @Transactional
    public void startNationalCompetition() {

        // 判断区赛是否全部结束
        String num = stringRedisTemplate.opsForValue().get(RedisUtil.FINISH_COMPETITION_NUM);
        if(!Objects.equals(num,"12"))
            throw new ProcessStatusException("未满足国赛开启条件");

        // 清除redis中的进程信息
        String[] group = Group.GROUPS;
        String[] zone = Zone.DISTRICT_ZONES;
        for (String string : group) {
            for (String s : zone) {
                String key = RedisUtil.getProcessKey(string, s);
                stringRedisTemplate.delete(key);
            }
        }
        // 清除redis中的报名时间信息
        stringRedisTemplate.delete(Time.SIGN_UP_BEGIN_TIME);
        stringRedisTemplate.delete(Time.SIGN_UP_END_TIME);

        // 清除redis中的弃赛时间信息
        stringRedisTemplate.delete(Time.WAIVER_NAT_QUAL_BEGIN_TIME);
        stringRedisTemplate.delete(Time.WAIVER_NAT_QUAL_END_TIME);

        // 将比赛设置为国赛
        stringRedisTemplate.opsForValue().set("competition", Process.NATIONAL);
        String key = RedisUtil.getProcessKey("BK", Zone.N);
        String value = RedisUtil.getProcessValue(Process.PRACTICE, Step.GROUP_DRAW);
        stringRedisTemplate.opsForValue().set(key, value);
        key = RedisUtil.getProcessKey("GZ", Zone.N);
        stringRedisTemplate.opsForValue().set(key, value);

        // 将选手设置为国赛选手
        contestantMapper.setToNational();
        List<Integer> uidList = contestantMapper.getUidList();
        for (Integer uid : uidList)
            userInfoMapper.setToNational(uid);

        // 插入选手成绩信息
        hostService.insertScoreInfo("BK", Zone.N);
        hostService.insertScoreInfo("GZ", Zone.N);

        //删除完赛数量
        stringRedisTemplate.delete(RedisUtil.FINISH_COMPETITION_NUM);
    }

    // 设置为游客身份
    @Override
    @Transactional
    public void setAsTourist(String group, String zone) {
        // 搜索赛区主持人、全部选手、评委
        List<Integer> uidList = userInfoMapper.getUidListByGroupAndZone(group, zone);
        List<Integer> contestantUidList=contestantMapper.getUidListByGroupAndZone(group,zone);
        // 移除晋级选手
        uidList.removeAll(contestantUidList);
        for (Integer uid : uidList) {
            userMapper.updateRole(uid, Role.TOURIST);
            UserInfo userInfo = UserInfo.builder().uid(uid).group("").zone("").build();
            userInfoMapper.update(userInfo);
        }
    }

    // 查询比赛进程
    @Override
    public List<ProcessVO> queryProcess(String group, String zone) {
        List<ProcessVO> processVOList = new ArrayList<>();

        // 查询全部区赛
        if (group == null && zone == null) {
            String[] groups = Group.GROUPS;
            String[] zones = Zone.ZONES;
            for (String g : groups) {
                for (String z : zones) {
                    ProcessVO processVO = getProcessVO(g, z);
                    processVOList.add(processVO);
                }
            }
        }// 按组别赛区查询
        else if (group != null && zone != null) {
            ProcessVO processVO = getProcessVO(group, zone);
            processVOList.add(processVO);
        }// 按组别查询
        else if (zone == null) {
            String[] zones = Zone.ZONES;
            for (String z : zones) {
                ProcessVO processVO = getProcessVO(group, z);
                processVOList.add(processVO);
            }
        }// 按赛区查询
        else {
            String[] groups = Group.GROUPS;
            for (String g : groups) {
                ProcessVO processVO = getProcessVO(g, zone);
                processVOList.add(processVO);
            }
        }
        return processVOList;
    }

    // 查询用户状态
    @Override
    public PageResult queryStatus(StatusPageQuery statusPageQuery) {
        PageHelper.startPage(statusPageQuery.getPageNo(), statusPageQuery.getPageSize());
        Page<StatusVO> page = userMapper.queryStatusVO(statusPageQuery);
        List<StatusVO> statusVOList = page.getResult();
        for (StatusVO statusVO : statusVOList) {
            statusVO.setRole(ConvertUtil.parseRoleStr(statusVO.getRole()));
            statusVO.setGroup(ConvertUtil.parseGroupStr(statusVO.getGroup()));
            statusVO.setZone(ConvertUtil.parseZoneStr(statusVO.getZone()));
            statusVO.setStatus(ConvertUtil.parseStatusStr(statusVO.getStatus()));
        }
        return new PageResult(page.getTotal(), statusVOList);
    }

    // 查询实战环节评委打分情况、选手得分情况
    @Override
    public List<SingeScoreInfo> queryPracticalScoreInfo(ScoreInfoQuery scoreInfoQuery) {
        List<SingeScoreInfo> singeScoreInfoList = practicalScoreMapper.getScoreInfoList(scoreInfoQuery);
        for (SingeScoreInfo singeScoreInfo : singeScoreInfoList) {
            singeScoreInfo.setGroup(ConvertUtil.parseGroupStr(singeScoreInfo.getGroup()));
            singeScoreInfo.setZone(ConvertUtil.parseZoneStr(singeScoreInfo.getZone()));
        }
        return singeScoreInfoList;
    }

    // 查询实战环节选手最终得分情况
    @Override
    public List<FinalSingleScore> queryPracticalScore(ScoreQuery scoreQuery) {
        return scoreInfoMapper.getPracticalScores(scoreQuery);
    }

    // 查询快问快答环节评委打分情况、选手得分情况
    @Override
    public List<SingeScoreInfo> queryqAndAScoreInfo(ScoreInfoQuery scoreInfoQuery) {
        List<SingeScoreInfo> singeScoreInfoList = qAndAScoreMapper.getScoreInfoList(scoreInfoQuery);
        for (SingeScoreInfo singeScoreInfo : singeScoreInfoList) {
            singeScoreInfo.setGroup(ConvertUtil.parseGroupStr(singeScoreInfo.getGroup()));
            singeScoreInfo.setZone(ConvertUtil.parseZoneStr(singeScoreInfo.getZone()));
        }
        return singeScoreInfoList;
    }

    // 查询快问快答环节选手最终得分情况
    @Override
    public List<FinalSingleScore> queryqAndAScore(ScoreQuery scoreQuery) {
        return scoreInfoMapper.getQAndAScores(scoreQuery);
    }

    private ProcessVO getProcessVO(String group, String zone) {
        String key = RedisUtil.getProcessKey(group, zone);
        String value = stringRedisTemplate.opsForValue().get(key);

        ProcessVO processVO;
        if (value == null) {
            processVO = ProcessVO.builder().group(ConvertUtil.parseGroupStr(group))
                    .zone(ConvertUtil.parseZoneStr(zone))
                    .process("尚未开启比赛").build();
        } else {
            String[] values = value.split(":");
            processVO = ProcessVO.builder().group(ConvertUtil.parseGroupStr(group))
                    .zone(ConvertUtil.parseZoneStr(zone))
                    .process(ConvertUtil.parseProcessStr(values[0]))
                    .step(ConvertUtil.parseStepStr(values[1])).build();
        }
        return processVO;
    }
}