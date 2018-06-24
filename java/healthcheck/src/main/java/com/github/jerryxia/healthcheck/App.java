package com.github.jerryxia.healthcheck;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;

public class App {
    private static final Log log               = LogFactory.getLog(App.class);
    private static String    CURRENT_DIR       = getCurrJarDir();
    private static String    SHUTDOWN_PID_FILE = CURRENT_DIR + "/healthcheck.pid";
    private static String    PID               = getProcessID();

    public static void main(String[] args) throws IOException {
        String jsonConfigFile = CURRENT_DIR + "/nodes.json";
        if (args.length > 0) {
            jsonConfigFile = args[0];
        }

        String jsonConfigFileContent = FileUtils.readFileToString(new File(jsonConfigFile));
        List<InstanceNode> serverNodes = JSON.parseArray(jsonConfigFileContent, InstanceNode.class);
        initCheckWorkers(serverNodes);

        File shutDownFile = new File(SHUTDOWN_PID_FILE);
        FileUtils.writeStringToFile(shutDownFile, PID);
        // watch for file deleted then shutdown
        while (true) {
            try {
                if (shutDownFile.exists()) {
                    Thread.sleep(1000);
                } else {
                    log.info("shutdown......");
                    System.exit(0);
                }
            } catch (InterruptedException e) {
                log.warn("shutdown-watcher-thread Interrupted", e);
            }
        }
    }

    private static String getCurrJarDir() {
        String filePath = null;
        URL url = App.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码，支持中文
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            // 可执行jar包运行的结果里包含".jar"
            // 获取jar包所在目录
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);
        filePath = file.getAbsolutePath();// 得到windows下的正确路径
        return filePath;
    }

    private static String getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName().split("@")[0];
    }

    private static void initCheckWorkers(List<InstanceNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            InstanceNode node = nodes.get(i);
            CheckTask task = new CheckTask(node);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }
}
