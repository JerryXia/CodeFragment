package com.github.jerryxia.mavenPomVersionFind;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springside.modules.utils.io.FilePathUtil;
import org.springside.modules.utils.io.FileUtil;

public class App {
    public static void main(String[] args) throws IOException {
        String projectRootPath = args[0];
        String projectDeployName = args[1];
        String buildShellDir = args[2];

        String pomFilePath = FilePathUtil.contact(projectRootPath, "pom.xml");
        File pomFile = new File(pomFilePath);
        String pomFileContent = FileUtil.toString(pomFile);

        Matcher match = Pattern.compile("<version>(.+)</version>").matcher(pomFileContent);
        if(match.find()) {
            String version = match.group(1);
            
            String fabFilePath = FilePathUtil.contact(buildShellDir, String.format("fab_%s.py", projectDeployName));
            File fabFile = new File(fabFilePath);
            String fabFileContent = FileUtil.toString(fabFile);

            Matcher matches = Pattern.compile(projectDeployName + "-(.+).jar").matcher(fabFileContent);
            for(int i = 1; i < matches.groupCount(); i++)
            {
                String oldVersion = matches.group(i);
                fabFileContent = StringUtils.replace(fabFileContent, oldVersion, version);
            }
            FileUtil.write(fabFileContent, fabFile);

            String serviceFilePath = FilePathUtil.contact(buildShellDir, projectDeployName + "_service");
            File serviceFile = new File(serviceFilePath);
            String serviceFileContent = FileUtil.toString(serviceFile);
            Matcher serviceFileMatches = Pattern.compile(projectDeployName + "-(.+).jar").matcher(serviceFileContent);
            for (int i = 1; i < serviceFileMatches.groupCount(); i++)
            {
                String oldVersion = serviceFileMatches.group(i);
                serviceFileContent = StringUtils.replace(serviceFileContent, oldVersion, version);
            }
            FileUtil.write(serviceFileContent, serviceFile);

            String deployFilePath = FilePathUtil.contact(buildShellDir, projectDeployName + "_deploy.bat");
            File deployFile = new File(deployFilePath);
            if (FileUtil.isFileExists(deployFile))
            {
                FileUtil.deleteFile(deployFile);
            }
            FileUtil.write(String.format("fab -f \"%sfab_%s.py\" deploy_app", buildShellDir, projectDeployName), deployFile);

            String restartFilePath = FilePathUtil.contact(buildShellDir, projectDeployName + "_restart.bat");
            File restartFile = new File(restartFilePath);
            if (FileUtil.isFileExists(restartFile))
            {
                FileUtil.deleteFile(restartFile);
            }
            FileUtil.write(String.format("fab -f \"%sfab_%s.py\" restart_app", buildShellDir, projectDeployName), restartFile);
        } else {
            System.out.println("regex match fail");
        }
    }
}