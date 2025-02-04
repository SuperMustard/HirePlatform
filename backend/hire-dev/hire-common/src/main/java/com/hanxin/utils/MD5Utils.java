package com.hanxin.utils;

import org.springframework.util.DigestUtils;

public class MD5Utils {
    public static String encrypt(String data, String slat) {
        String base = data + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    public static void main(String[] args) {
        String md5Str = MD5Utils.encrypt("123456", "8111");
        System.out.println(md5Str);
    }
}
