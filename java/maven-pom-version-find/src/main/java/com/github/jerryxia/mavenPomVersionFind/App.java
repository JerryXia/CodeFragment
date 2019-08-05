package com.github.jerryxia.mavenPomVersionFind;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springside.modules.utils.io.FilePathUtil;
import org.springside.modules.utils.io.FileUtil;
import org.springside.modules.utils.time.DateFormatUtil;

public class App {
    public static void main(String[] args) throws IOException {
        String projectRootPath = args[0];
        String projectDeployName = args[1];
        String buildShellDir = args[2];

        String pomFilePath = FilePathUtil.contact(projectRootPath, "pom.xml");
        File pomFile = new File(pomFilePath);
        String pomFileContent = FileUtil.toString(pomFile);
        // replace version with date and time
        Matcher match = Pattern.compile("<version>(.+)</version>").matcher(pomFileContent);
        if (match.find()) {
            String version = match.group(1);
            String versionWithDate = version + DateFormatUtil.formatDate("-yyyyMMddHHmmss", new Date());
            String searchString = "<version>" + version + "</version>";
            String replacement = "<version>" + versionWithDate + "</version>";
            String newPomFileContent = StringUtils.replaceOnce(pomFileContent, searchString, replacement);
            FileUtil.write(newPomFileContent, pomFile);
        }
        pomFileContent = FileUtil.toString(pomFile);
        match = Pattern.compile("<version>(.+)</version>").matcher(pomFileContent);
        if (match.find()) {
            String version = match.group(1);

            String fabFilePath = FilePathUtil.contact(buildShellDir, String.format("fab_%s.py", projectDeployName));
            File fabFile = new File(fabFilePath);
            String fabFileContent = FileUtil.toString(fabFile);

            Matcher matches = Pattern.compile(projectDeployName + "-(.+)\\.jar").matcher(fabFileContent);
            if (matches.find()) {
                String oldVersion = matches.group();
                fabFileContent = StringUtils.replace(fabFileContent, oldVersion, String.format("%s-%s.jar", projectDeployName, version));
            }
            FileUtil.write(fabFileContent, fabFile);

            String serviceFilePath = FilePathUtil.contact(buildShellDir, projectDeployName + "_service");
            File serviceFile = new File(serviceFilePath);
            String serviceFileContent = FileUtil.toString(serviceFile);
            Matcher serviceFileMatches = Pattern.compile(projectDeployName + "-(.+)\\.jar").matcher(serviceFileContent);
            if (serviceFileMatches.find()) {
                String oldVersion = serviceFileMatches.group();
                serviceFileContent = StringUtils.replace(serviceFileContent, oldVersion, String.format("%s-%s.jar", projectDeployName, version));
            }
            FileUtil.write(serviceFileContent, serviceFile);

            String deployFilePath = FilePathUtil.contact(buildShellDir, projectDeployName + "_deploy.bat");
            File deployFile = new File(deployFilePath);
            if (FileUtil.isFileExists(deployFile)) {
                FileUtil.deleteFile(deployFile);
            }
            FileUtil.write(String.format("fab -f \"%sfab_%s.py\" deploy_app", buildShellDir, projectDeployName), deployFile);

            String restartFilePath = FilePathUtil.contact(buildShellDir, projectDeployName + "_restart.bat");
            File restartFile = new File(restartFilePath);
            if (FileUtil.isFileExists(restartFile)) {
                FileUtil.deleteFile(restartFile);
            }
            FileUtil.write(String.format("fab -f \"%sfab_%s.py\" restart_app", buildShellDir, projectDeployName), restartFile);
        } else {
            System.out.println("regex match fail");
        }
    }
}
