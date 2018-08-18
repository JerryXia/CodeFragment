/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ServerNode {
    private String serverName;
    private ArrayList<InstanceNode> nodes;
    private HealthCheckConf hkConf;
    private String vhostConfTplFilePath;
}
