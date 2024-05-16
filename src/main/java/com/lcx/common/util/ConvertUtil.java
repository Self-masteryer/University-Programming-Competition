package com.lcx.common.util;

import com.lcx.common.constant.SheetName;
import com.lcx.common.constant.Zone;

public class ConvertUtil {

    public static int getRoleNum(String role) {
        return switch (role) {
            case SheetName.HOST -> 2;
            case SheetName.JUDGEMENT -> 3;
            case SheetName.SCHOOL -> 4;
            case SheetName.CONTESTANT -> 5;
            default -> 0;
        };
    }

    public static int getZoneNum(String zone) {
        return switch (zone) {
            case Zone.NORTH_WEST -> 1;
            case Zone.SOUTH_WEST -> 2;
            case Zone.NORTH_EASE -> 3;
            case Zone.SOUTH_EAST -> 4;
            case Zone.CENTRAL -> 5;
            case Zone.EAST -> 6;
            case Zone.NATIONAL -> 7;
            default -> 0;
        };
    }
}
