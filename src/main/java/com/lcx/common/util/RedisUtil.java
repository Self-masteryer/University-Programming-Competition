package com.lcx.common.util;

public class RedisUtil {

    public static String getProcessKey(String group,String zone){
        return "process" + ":" + group + ":" + zone;
    }

    public static String getSeatDrawKey(String group,String zone){
        return "seat_draw" + ":" + group + ":" + zone;
    }

}
