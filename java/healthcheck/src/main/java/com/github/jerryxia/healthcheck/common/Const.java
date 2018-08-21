/**
 * 
 */
package com.github.jerryxia.healthcheck.common;

import java.io.File;

import com.fasterxml.jackson.databind.JavaType;

/**
 * @author Administrator
 *
 */
public class Const {

    public static final freemarker.template.Configuration FTL_Configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);

    public static File     CONF_SERVER_NODES_FILE;
    public static JavaType ServerNodeArrayListType;
}
