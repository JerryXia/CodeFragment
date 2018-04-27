/*
 * Copyright (C) 2016 alchemystar, Inc. All Rights Reserved.
 */
package alchemystar.runner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.alibaba.druid.util.StringUtils;

/**
 * @Author lizhuyang
 */
public class ShellRunner {
    public static void main(String[] args) {
        Properties properties = loadFromFile("./config.properties");
        Syncer.sync(properties);
    }

    public static Properties loadFromFile(String file) {
        if (StringUtils.isEmpty(file)) {
            throw new IllegalArgumentException("file parameter can't be blank.");
        }
        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            try {
                fis = new FileInputStream(file);
                p.load(fis);
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return p;
    }
}
