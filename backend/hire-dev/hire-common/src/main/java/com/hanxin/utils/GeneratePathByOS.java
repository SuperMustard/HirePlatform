package com.hanxin.utils;

import java.io.File;

public class GeneratePathByOS {
    public static String getRootPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String rootPath = "";
        if (os.contains("win")) {
            rootPath = "D:\\temp" + File.separator;
        } else {
            rootPath = "/temp" + File.separator;
        }

        return rootPath;
    }
}
