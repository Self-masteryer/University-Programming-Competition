package com.lcx.common.util;

import java.security.SecureRandom;

public class RandomStringUtils {

    public static String length(int length) {
        String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String SPECIAL_CHARS = ".$@!%*?&";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length - 1; i++) {
            int index = random.nextInt(CHAR_LIST.length());
            sb.append(CHAR_LIST.charAt(index));
        }
        sb.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        return sb.toString();
    }
}
