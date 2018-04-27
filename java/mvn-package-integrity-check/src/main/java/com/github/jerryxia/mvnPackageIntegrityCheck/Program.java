package com.github.jerryxia.mvnPackageIntegrityCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import lombok.val;

/**
 * <p>
 * This programe is designed to avoid invalid LOC header (bad signature).
 * </p>
 * 
 * @author Administrator
 *
 */
public class Program {
    private static String mode = null;

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            mode = "clean";
        }
        if (args.length == 1) {
            mode = "check".equalsIgnoreCase(args[0]) ? "check" : "clean";
        }

        String localRepositoryFilePath = StrSubstitutor.replaceSystemProperties(Const.LOCAL_REPOSITORY_FILE_PATH);
        scanMvnDir(localRepositoryFilePath);
    }

    private static void scanMvnDir(String targetDir) {
        val mvnLocalRepositoryDir = new File(targetDir);
        if (!mvnLocalRepositoryDir.exists()) {
            Util.write("dir not exists: %s", targetDir);
            return;
        }
        String mvnLocalRepositoryPath = mvnLocalRepositoryDir.getAbsolutePath();
        Util.write("maven local repository: %s", mvnLocalRepositoryPath);

        long startTime = System.currentTimeMillis();
        ArrayList<File> allMvnPackageDirs = new ArrayList<File>();
        HashMap<String, File> allMvnPackageDirFiles = new HashMap<String, File>();
        if (mvnLocalRepositoryDir.isFile()) {
            Util.write("dir invalid: %s", targetDir);
            return;
        } else {
            scanAllMvnPackageDir(allMvnPackageDirs, allMvnPackageDirFiles, mvnLocalRepositoryDir);
        }
        long endTime = System.currentTimeMillis();
        Util.write("scan end: %d ms", endTime - startTime);

        startTime = System.currentTimeMillis();
        // parse all packages
        for (File mvnPkgVersionDir : allMvnPackageDirs) {
            String mvnPkgVersionDirPath = mvnPkgVersionDir.getAbsolutePath();
            String groupAndArtifactAndVersionPath = mvnPkgVersionDirPath.substring(mvnLocalRepositoryPath.length() + 1);
            String[] separators = StringUtils.split(groupAndArtifactAndVersionPath, File.separatorChar);
            String[] groupIds = new String[separators.length - 2];
            System.arraycopy(separators, 0, groupIds, 0, separators.length - 2);
            String groupId = StringUtils.join(groupIds, '.');
            String artifactId = separators[separators.length - 2];
            String version = separators[separators.length - 1];

            String jarFileName = String.format("%s-%s.jar", artifactId, version);
            String pomFileName = String.format("%s-%s.pom", artifactId, version);
            String javadocFileName = String.format("%s-%s-javadoc.jar", artifactId, version);
            String sourcesFileName = String.format("%s-%s-sources.jar", artifactId, version);

            deleteIfInvalid(checkSha1IsInvalidByFileName(allMvnPackageDirFiles, mvnPkgVersionDirPath, jarFileName));
            deleteIfInvalid(checkSha1IsInvalidByFileName(allMvnPackageDirFiles, mvnPkgVersionDirPath, pomFileName));
            deleteIfInvalid(checkSha1IsInvalidByFileName(allMvnPackageDirFiles, mvnPkgVersionDirPath, javadocFileName));
            deleteIfInvalid(checkSha1IsInvalidByFileName(allMvnPackageDirFiles, mvnPkgVersionDirPath, sourcesFileName));
        }
        endTime = System.currentTimeMillis();
        Util.write("clean end: %d ms", endTime - startTime);
    }

    private static void deleteIfInvalid(File[] inValidFiles) {
        if (inValidFiles != null) {
            if("clean".equalsIgnoreCase(mode)) {
                if (inValidFiles[0].delete()) {
                    Util.write("Invalid File: %s deleted", inValidFiles[0].getAbsolutePath());
                } else {
                    Util.write("File: %s delete fail", inValidFiles[0].getAbsolutePath());
                }

                if (inValidFiles[1].delete()) {
                    Util.write("Invalid File: %s deleted", inValidFiles[1].getAbsolutePath());
                } else {
                    Util.write("File: %s delete fail", inValidFiles[1].getAbsolutePath());
                }
            } else {
                Util.write("Invalid File: %s", inValidFiles[0].getAbsolutePath());
            }
        }
    }

    private static void scanAllMvnPackageDir(ArrayList<File> allMvnPackageDirs,
            HashMap<String, File> allMvnPackageDirFiles, File currDir) {
        val childFiles = currDir.listFiles();
        if (childFiles.length > 0) {
            for (val childFile : childFiles) {
                if (childFile.isFile()) {
                    if (allMvnPackageDirs.indexOf(currDir) == -1) {
                        allMvnPackageDirs.add(currDir);
                    }
                    allMvnPackageDirFiles.put(childFile.getAbsolutePath(), childFile);
                } else {
                    scanAllMvnPackageDir(allMvnPackageDirs, allMvnPackageDirFiles, childFile);
                }
            }
        } else {
            // empty dir
        }
    }

    private static File[] checkSha1IsInvalidByFileName(HashMap<String, File> allMvnPackageDirFiles,
            String mvnPkgVersionDirPath, String fileName) {
        File file = allMvnPackageDirFiles
                .get(String.format("%s%s%s", mvnPkgVersionDirPath, File.separatorChar, fileName));
        if (file != null) {
            if (file.exists()) {
                File file_sha1 = allMvnPackageDirFiles
                        .get(String.format("%s%s%s.sha1", mvnPkgVersionDirPath, File.separatorChar, fileName));
                if (file_sha1 != null && file_sha1.exists()) {
                    // 60b2b206af3df735765e8e284396bcfdbced5665
                    String fileSha1Result = null;
                    try {
                        fileSha1Result = Util.readFile(file_sha1).substring(0, 40);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    if (fileSha1Result != null) {
                        try {
                            String sha1 = Util.getSha1(file);
                            //Util.write("download file sha1: %s, calc sha1: %s", fileSha1Result, sha1);
                            if (fileSha1Result.equals(sha1)) {
                                return null;
                            } else {
                                File[] files = { file, file_sha1 };
                                return files;
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
