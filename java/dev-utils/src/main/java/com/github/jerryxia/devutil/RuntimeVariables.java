/**
 * 
 */
package com.github.jerryxia.devutil;

import java.util.Properties;

/**
 * @author guqk
 *
 */
public final class RuntimeVariables {
    public static final ClassLoader CLASS_LOADER    = RuntimeVariables.class.getClassLoader();
    public static final String      LIB_GROUP_ID    = "com.github.jerryxia";
    public static final String      LIB_ARTIFACT_ID = "dev-utils";
    public static String            LIB_VERSION     = null;

    static {
        Properties prop = new Properties();
        try {
            String pom = String.format("META-INF/maven/%s/%s/pom.properties", LIB_GROUP_ID, LIB_ARTIFACT_ID);
            prop.load(CLASS_LOADER.getResourceAsStream(pom));
            // load Property
            RuntimeVariables.LIB_VERSION = prop.getProperty("version");
        } catch (Exception e) {
            e.printStackTrace();
            RuntimeVariables.LIB_VERSION = "";
        }
    }
}
