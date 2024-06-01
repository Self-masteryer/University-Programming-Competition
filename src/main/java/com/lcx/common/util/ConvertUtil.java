package com.lcx.common.util;

import com.lcx.common.constant.*;
import com.lcx.common.constant.Process;
import com.lcx.common.exception.BaseException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ConvertUtil {

    public static int parseRoleNum(String role) {
        return switch (role) {
            case Role.A -> 1;
            case Role.H -> 2;
            case Role.J -> 3;
            case Role.S -> 4;
            case Role.C -> 5;
            case Role.T -> 6;
            default -> 0;// 没有这个角色
        };
    }

    public static String parseRoleStr(int rid) {
        return switch (rid) {
            case 1 -> Role.A;
            case 2 -> Role.H;
            case 3 -> Role.J;
            case 4 -> Role.S;
            case 5 -> Role.C;
            case 6 -> Role.T;
            default -> null;
        };
    }

    public static String parseRoleStr(String rid) {
        return switch (rid) {
            case "1" -> Role.A;
            case "2" -> Role.H;
            case "3" -> Role.J;
            case "4" -> Role.S;
            case "5" -> Role.C;
            case "6" -> Role.T;
            default -> null;
        };
    }

    public static String parseZoneSimStr(String zone) {
        return switch (zone) {
            case Zone.NORTH_WEST -> "NW";
            case Zone.SOUTH_WEST -> "SW";
            case Zone.NORTH_EASE -> "NE";
            case Zone.SOUTH_EAST -> "SE";
            case Zone.CENTRAL -> "C";
            case Zone.EAST -> "E";
            case Zone.NATIONAL -> "N";
            default -> null;
        };
    }

    public static String parseZoneStr(String zone) {
        return switch (zone) {
            case Zone.NW -> Zone.NORTH_WEST;
            case Zone.SW -> Zone.SOUTH_WEST;
            case Zone.NE -> Zone.NORTH_EASE;
            case Zone.SE -> Zone.SOUTH_EAST;
            case Zone.E -> Zone.EAST;
            case Zone.C -> Zone.CENTRAL;
            case Zone.N -> Zone.NATIONAL;
            default -> null;
        };
    }

    public static String parseGroupSimStr(String group) {
        return switch (group) {
            case Group.BK -> "BK";
            case Group.GZ -> "GZ";
            default -> null;
        };
    }

    public static String parseGroupStr(String group) {
        return switch (group) {
            case "BK" -> Group.BK;
            case "GZ" -> Group.GZ;
            default -> null;
        };
    }

    public static String parseProcessStr(String process) {
        return switch (process) {
            case Process.WRITTEN -> Process.W;
            case Process.PRACTICE -> Process.P;
            case Process.Q_AND_A -> Process.Q;
            case Process.FINAL -> Process.F;
            default -> "尚未开启";
        };
    }

    public static String parseStepStr(String step) {
        return switch (step) {
            case Step.SEAT_DRAW -> Step.SD;
            case Step.POST_WRITTEN_SCORE -> Process.P;
            case Step.SCORE_FILTER -> Step.SF;
            case Step.GROUP_DRAW -> Step.GD;
            case Step.RATE -> Step.R;
            case Step.SCORE_EXPORT -> Step.SE;
            case Step.NEXT -> Step.N;
            default -> null;
        };
    }

    public static LocalDateTime parseDate(String instantStr) {
        // 将字符串转换为长整型
        long instantLong = Long.parseLong(instantStr);
        // 使用Instant和ZoneId将时间戳转换为ZonedDateTime
        Instant instant = Instant.ofEpochMilli(instantLong);
        // 使用系统默认时区，或者指定其他时区，如ZoneId.of("Asia/Shanghai")
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        // 从ZonedDateTime中提取LocalDateTime（注意这会丢失时区信息）
        return zonedDateTime.toLocalDateTime();
    }

    public static String parseDateStr(LocalDateTime localDateTime) {
        // 转换为ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return String.valueOf(zonedDateTime.toInstant().toEpochMilli());
    }

    public static String parseStatusStr(String status) {
        return switch (status){
            case "0" -> "离线";
            case "1" -> "在线";
            default -> null;
        };
    }

    public static int parseSignNumInt(String signNumStr) {
        int num=2*Integer.parseInt(signNumStr.substring(1));
        char c=signNumStr.charAt(0);
        return switch (c){
            case 'A'->num-1;
            case 'B'->num;
            default -> throw new BaseException();// 异常
        };
    }
}
