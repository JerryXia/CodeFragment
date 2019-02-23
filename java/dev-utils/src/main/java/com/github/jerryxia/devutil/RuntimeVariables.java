/**
 * 
 */
package com.github.jerryxia.devutil;

import java.io.IOException;
import java.util.Properties;

/**
 * @author guqk
 *
 */
public final class RuntimeVariables {
    public static final ClassLoader CLASS_LOADER = RuntimeVariables.class.getClassLoader();

    /** lib的相关状态信息 **/
    public static String VERSION = null;

    static {
        Properties prop = new Properties();
        try {
            prop.load(CLASS_LOADER.getResourceAsStream("META-INF/maven/com.github.jerryxia/dev-utils/pom.properties"));
            // load Property
            RuntimeVariables.VERSION = prop.getProperty("version");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
