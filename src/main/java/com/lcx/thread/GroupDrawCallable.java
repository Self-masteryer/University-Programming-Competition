package com.lcx.thread;

import com.lcx.domain.Entity.Student;
import com.lcx.domain.VO.SignGroup;
import com.lcx.mapper.ContestantMapper;
import com.lcx.mapper.ScoreInfoMapper;

import java.util.*;
import java.util.concurrent.Callable;

public class GroupDrawCallable implements Callable<List<SignGroup>> {

    private String group;
    private String zone;
    private ContestantMapper contestantMapper;
    private ScoreInfoMapper scoreInfoMapper;

    public GroupDrawCallable(ContestantMapper contestantMapper, ScoreInfoMapper scoreInfoMapper, String group, String zone) {
        this.contestantMapper = contestantMapper;
        this.group = group;
        this.scoreInfoMapper = scoreInfoMapper;
        this.zone = zone;
    }

    @Override
    public List<SignGroup> call(){
        List<Student> students = contestantMapper.getStudentListByGroupAndZone(group, zone);
        // 分组信息
        List<SignGroup> signGroups = groupDraw(students);

        // 更新成绩信息
        for (SignGroup signGroup : signGroups) {
            scoreInfoMapper.updateSignNumByUid(signGroup.getA().getUid(), "A" + signGroup.getSignNum());
            scoreInfoMapper.updateSignNumByUid(signGroup.getB().getUid(), "B" + signGroup.getSignNum());
        }
        return signGroups;
    }

    public List<SignGroup> groupDraw(List<Student> students) {
        // 创建一个映射来跟踪每个学校的选手
        Map<String, List<Student>> schoolStudents = new HashMap<>();
        for (Student student : students) {
            schoolStudents.computeIfAbsent(student.getSchool(), k -> new ArrayList<>()).add(student);
        }
        int signNum = 1;
        Collections.shuffle(students);// 打乱选手顺序
        List<SignGroup> signGroups = new ArrayList<>();// 存放分组信息
        while (students.size() > 6) {
            Student A = students.remove(0);
            schoolStudents.get(A.getSchool()).remove(A);
            for (String school : new ArrayList<>(schoolStudents.keySet())) {
                if (!schoolStudents.get(school).isEmpty() && !Objects.equals(school, A.getSchool())) {
                    Student B = schoolStudents.get(school).remove(0);
                    students.remove(B);
                    signGroups.add(new SignGroup(signNum++, A, B));
                    break;
                }
            }
        }
        // 剩下6名选手随机分组可能发生同校同组的情况，需要回退
        Stack<Student> stack = new Stack<>();
        while (!students.isEmpty()) {
            Student A = students.remove(0);
            schoolStudents.get(A.getSchool()).remove(A);
            stack.push(A);
            boolean flag = true;
            for (String school : new ArrayList<>(schoolStudents.keySet())) {
                if (!schoolStudents.get(school).isEmpty() && !Objects.equals(school, A.getSchool())) {
                    Student B = schoolStudents.get(school).remove(0);
                    students.remove(B);
                    stack.push(B);
                    signGroups.add(new SignGroup(signNum++, A, B));
                    flag = false;
                    break;
                }
            }
            // 剩下两名选手来自同一学校，回退两步
            if (flag) {
                students.addAll(stack);
                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
                schoolStudents.get(stack.peek().getSchool()).add(stack.pop());
                signGroups.remove(signGroups.size() - 1);
                signGroups.remove(signGroups.size() - 1);
                signNum -= 2;
            }
        }
        return signGroups;
    }
}
