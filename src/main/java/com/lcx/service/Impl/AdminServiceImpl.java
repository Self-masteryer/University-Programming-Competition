package com.lcx.service.Impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.SheetName;
import com.lcx.common.constant.Zone;
import com.lcx.common.exception.PasswordErrorException;
import com.lcx.common.util.ConvertUtil;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.mapper.UserMapper;
import com.lcx.pojo.Entity.User;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserMapper userMapper;

    @Override
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

                int role = ConvertUtil.getRoleNum(r);
                for (int j = 1; j <= inSheet.getLastRowNum(); j++) {
                    XSSFRow inRow = inSheet.getRow(j);
                    XSSFRow outRow = outSheet.createRow(j);
                    String IDCard = inRow.getCell(1).getStringCellValue();
                    UserInfo userInfo = userInfoMapper.getByIDCard(IDCard);
                    //账号不存在
                    if (userInfo == null) {
                        //身份信息表
                        userInfo = new UserInfo();
                        userInfo.setIDCard(IDCard);//身份证
                        userInfo.setRole(role);//角色
                        userInfo.setName(inRow.getCell(0).getStringCellValue());//姓名
                        userInfo.setGroup(inRow.getCell(2).getStringCellValue());//组别
                        userInfo.setZone(ConvertUtil.getZoneNum(inRow.getCell(3).getStringCellValue()));//赛区
                        //用户
                        User user = new User();
                        user.setUsername(UUID.randomUUID().toString().substring(0, 8));
                        String password = randomStringGenerator(8);
                        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                        user.setName(userInfo.getName());
                        user.setRid(role);
                        user.setEnabled(1);
                        userMapper.insertUser(user);

                        userInfo.setUid(user.getId());
                        userInfoMapper.insert(userInfo);

                        //输出账号数据
                        outRow.createCell(0).setCellValue(user.getName());
                        outRow.createCell(1).setCellValue(user.getUsername());
                        outRow.createCell(2).setCellValue(password);
                    } else {
                        //账号存在，更改身份信息
                        userInfo.setZone(ConvertUtil.getZoneNum(inRow.getCell(3).getStringCellValue()));
                        userInfo.setGroup(inRow.getCell(2).getStringCellValue());
                        userInfo.setRole(role);
                        userInfoMapper.update(userInfo);

                        User user = userMapper.getById(userInfo.getUid());
                        user.setEnabled(1);//启用
                        String password = randomStringGenerator(8);
                        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));//修改密码

                        //输出账号数据
                        outRow.createCell(0).setCellValue(user.getName());
                        outRow.createCell(1).setCellValue(user.getUsername());
                        outRow.createCell(2).setCellValue(password);
                    }
                }
            }
            //传回账号数据
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=account.xls");
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

    public String randomStringGenerator(int length) {
        String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_LIST.length());
            sb.append(CHAR_LIST.charAt(index));
        }
        return sb.toString();
    }
}
