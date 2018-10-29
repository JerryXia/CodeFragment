/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.util.ArrayList;

import lombok.Data;

@Data
public class InstanceNodeGroup {
    private ArrayList<InstanceNode> nodes;
    private HealthCheckConf hkConf;
    private ManagementServerConf msConf;
}
