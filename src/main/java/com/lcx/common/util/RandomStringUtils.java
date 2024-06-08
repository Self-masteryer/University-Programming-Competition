package com.lcx.common.util;

import java.security.SecureRandom;

public class RandomStringUtils {

    // 获得长度为n包含大小写英文、数字及".$@!%*?&"的随机字符串（特殊字符在末尾一位）
    public static String length(int length) {
        String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String SPECIAL_CHARS = ".$@!%*?&";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_LIST.length());
            sb.append(CHAR_LIST.charAt(index));
        }
        //sb.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        return sb.toString();
    }
}
