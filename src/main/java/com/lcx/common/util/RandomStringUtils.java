package com.lcx.common.util;

import java.security.SecureRandom;

public class RandomStringUtils {

    public static String length(int length) {
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
