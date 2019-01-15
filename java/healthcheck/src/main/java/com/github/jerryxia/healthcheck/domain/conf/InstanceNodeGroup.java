/**
 * 
 */
package com.github.jerryxia.healthcheck.domain.conf;

import java.util.ArrayList;

import lombok.Data;

@Data
public class InstanceNodeGroup {
    private ArrayList<InstanceNode> nodes;
    private HealthCheckConf hkConf;
    private ManagementServerConf msConf;
    private boolean autoHealthCheckMode;
}
