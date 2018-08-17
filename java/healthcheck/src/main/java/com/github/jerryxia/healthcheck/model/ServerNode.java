/**
 * 
 */
package com.github.jerryxia.healthcheck.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ServerNode {
    private String serverName;
    private ArrayList<InstanceNode> nodes;
    private HealthCheckConf hkConf;
}
