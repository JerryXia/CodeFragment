/**
 * 
 */
package com.github.jerryxia.healthcheck.domain.conf;

import java.util.HashMap;

import lombok.Data;

@Data
public class ServerNode {
    private String serverName;
    private HashMap<String, InstanceNodeGroup> groups;
    /**
     * 默认会读取conf目录中的${serverName}.ftl
     */
    private String vhostConfTplFilePath;
}
