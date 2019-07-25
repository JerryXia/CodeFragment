/**
 * 
 */
package com.github.jerryxia.healthcheck.common;

import java.io.File;
import java.util.ArrayList;

import org.springside.modules.utils.mapper.JsonMapper;

import com.fasterxml.jackson.databind.JavaType;
import com.github.jerryxia.healthcheck.domain.conf.ServerNode;
import com.google.common.base.Splitter;

/**
 * @author Administrator
 *
 */
public class Const {
    public static final String   DEFAULT_USER_AGENT = "HealthChecker";
    public static final Splitter COLON_SPLITTER     = Splitter.on(':');

    public static final freemarker.template.Configuration FTL_Configuration         = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);
    public static final JavaType                          ARRAYLIST_SERVERNODE_TYPE = JsonMapper.INSTANCE.buildCollectionType(ArrayList.class, ServerNode.class);

    public static String CONF_DIR;
    public static File   CONF_SERVER_NODES_FILE;
}
