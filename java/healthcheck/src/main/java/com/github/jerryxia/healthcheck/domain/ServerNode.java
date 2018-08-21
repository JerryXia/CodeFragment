/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.util.HashMap;

import lombok.Data;

@Data
public class ServerNode {
    private String serverName;
    private HashMap<String, InstanceNodeGroup> groups;
    private String vhostConfTplFilePath;
}
