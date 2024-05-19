package com.lcx.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.constant.Process;
import com.lcx.common.constant.Role;
import com.lcx.common.constant.Time;
import com.lcx.common.exception.RepeatedDrawingException;
import com.lcx.common.exception.process.NoSuchProcessException;
import com.lcx.common.exception.process.ProcessSequenceException;
import com.lcx.common.exception.time.StartTimeException;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.DistrictScoreMapper;
import com.lcx.mapper.UserInfoMapper;
import com.lcx.mapper.UserMapper;
import com.lcx.pojo.Entity.Contestant;
import com.lcx.pojo.Entity.DistrictScore;
import com.lcx.pojo.Entity.UserInfo;
import com.lcx.pojo.VO.DistrictScoreVO;
import com.lcx.pojo.VO.SeatInfo;
import com.lcx.service.HostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class HostServiceImpl implements HostService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ContestantMapper contestantMapper;
    @Resource
    private DistrictScoreMapper districtScoreMapper;
    @Resource
    private UserMapper userMapper;

    // 开启比赛
    @Override
    @Transactional
    public void startCompetition() {
        // 判断报名是否已结束
        String instantStr = stringRedisTemplate.opsForValue().get(Time.SIGN_UP_END_TIME);
        if (instantStr == null) throw new StartTimeException(ErrorMessageConstant.START_TIME_ERROR);
        long end = Long.parseLong(instantStr);
        long now = System.currentTimeMillis();
        if (now < end) throw new StartTimeException(ErrorMessageConstant.START_TIME_ERROR);

        // 获得组别、赛区信息
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        String group = userInfo.getGroup();
        String zone = userInfo.getZone();
        log.info("{}:{} 比赛开始", group, zone);
        // 将比赛进程存进redis
        String key = RedisUtil.getProcessKey(group,zone);
        stringRedisTemplate.opsForValue().set(key, Process.WRITTEN);

        //届数加一
        int session = Integer.parseInt(stringRedisTemplate.opsForValue().get("session"));
        stringRedisTemplate.opsForValue().set("session", String.valueOf(session + 1));
    }

    // 推进下一流程
    @Override
    @Transactional
    public void nextProcess(String process) {
        // 获取前状态
        String preProcess = switch (process) {
            case Process.PRACTICE -> Process.WRITTEN;
            case Process.Q_AND_A -> Process.PRACTICE;
            case Process.FINAL -> Process.Q_AND_A;
            // 没有这样的进程异常
            default -> throw new NoSuchProcessException(ErrorMessageConstant.NO_SUCH_PROCESS_EXCEPTION);
        };
        // 获取组别赛区信息
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        String key = "process" + ":" + userInfo.getGroup() + ":" + userInfo.getZone();
        String reidsProcess = stringRedisTemplate.opsForValue().get(key);
        //进程顺序错误
        if (reidsProcess == null) throw new StartTimeException(ErrorMessageConstant.START_TIME_ERROR);
        else if (!reidsProcess.equals(preProcess))
            throw new ProcessSequenceException(ErrorMessageConstant.PROCESS_SEQUENCE_ERROR);

        stringRedisTemplate.opsForValue().set(key, process);
    }

    // 通过excel上传笔试成绩
    @Override
    @Transactional
    public void postWrittenScoreByExcel(MultipartFile file) {
        try {
            InputStream in = file.getInputStream();
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                //获得区赛成绩表
                String idCard = row.getCell(1).getStringCellValue();
                Contestant contestant = contestantMapper.getByIDCard(idCard);
                DistrictScore districtScore = districtScoreMapper.getByUid(contestant.getUid());
                //更新笔试成绩
                int writtenScore = (int) row.getCell(5).getNumericCellValue();
                districtScore.setWrittenScore(writtenScore);

                districtScoreMapper.updateWrittenScore(districtScore);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 座位号抽签
    @Override
    @Transactional
    public List<SeatInfo> seatDraw() {
        // 获取组别、赛区信息
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        String group = userInfo.getGroup();
        String zone = userInfo.getZone();

        //判断是否已座位号抽签
        String key = RedisUtil.getSeatDrawKey(group,zone);
        String flag = stringRedisTemplate.opsForValue().get(key);
        if (flag != null) throw new RepeatedDrawingException(ErrorMessageConstant.REPEATED_DRAWING_ERROR);

        // 座位号抽签
        int count = contestantMapper.getCountByGroupAndZone(group, zone); // 组别赛区选手总数
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= count; i++) nums.add(i);
        Collections.shuffle(nums);

        List<Contestant> list = contestantMapper.getListByGroupAndZone(group, zone);
        List<SeatInfo> seatTable = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Contestant contestant = list.get(i);
            int session=Integer.parseInt(stringRedisTemplate.opsForValue().get("session"));
            DistrictScore districtScore = DistrictScore.builder()
                    .uid(contestant.getUid()).session(session).build();
            String seatNum = group + ":" + zone + ":" + nums.get(i);
            districtScore.setSeatNum(seatNum);
            districtScoreMapper.insert(districtScore);
            //座位信息
            SeatInfo seatInfo = SeatInfo.builder().name(contestant.getName()).seatNum(seatNum).build();
            seatTable.add(seatInfo);
        }
        //按座位号排序
        seatTable.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getSeatNum().substring(6))));

        stringRedisTemplate.opsForValue().set(key, "yes");

        return seatTable;
    }

    // 按笔试成绩筛选
    @Override
    @Transactional
    public List<DistrictScoreVO> scoreFilter() {
        //获得用户信息
        UserInfo userInfo = userInfoMapper.getByUid(StpUtil.getLoginIdAsInt());
        // 查询成绩单
        List<DistrictScoreVO> scores = districtScoreMapper
                .getVOListByGroupAndZone(userInfo.getGroup(), userInfo.getZone());
        // 按笔试成绩降序排序
        scores.sort(Comparator.comparingInt(DistrictScoreVO::getWrittenScore).reversed());
        //人数不满30人，直接返回
        if (scores.size() <= 30) return scores;
        // 将淘汰选手的账号身份设置为游客，只能查询往年成绩
        for (int i = 30; i < scores.size(); i++) {
            DistrictScoreVO districtScoreVO = scores.get(i);
            DistrictScore districtScore = districtScoreMapper.getBySeatNum(districtScoreVO.getSeatNum());

            userMapper.updateRole(districtScore.getUid(), Role.TOURIST);// 设置成游客身份
            contestantMapper.deleteByUid(districtScore.getUid());// 删除选手
            districtScoreMapper.deleteByUid(districtScore.getUid());// 删除成绩
        }
        // 返回晋级选手成绩单
        return scores;
    }

}