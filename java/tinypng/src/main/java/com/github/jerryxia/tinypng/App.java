package com.github.jerryxia.tinypng;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger log = LoggerFactory.getLogger(TinyPngClient.class);

    public static void main(String[] args) {
        if (args != null && args.length >= 2 && StringUtils.isNotBlank(args[0]) && StringUtils.isNotBlank(args[1])) {
            TinyPngClient tinypngClient = new TinyPngClient();
            File originFile = new File(args[0]);
            File outputFile = new File(args[1]);
            if (originFile.exists() && originFile.isFile()) {
                tinypngClient.min(originFile, outputFile);
            } else {
                log.info("原始文件不存在");
            }
        } else {
            log.info("invalid args: {}", args);
        }
    }
}
