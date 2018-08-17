/**
 * 
 */
package com.github.jerryxia.healthcheck.model;

import lombok.Data;

@Data
public class HealthCheckConf {
    private String path;
    private String queryWithTimestampParamName;
    private String cookie;
    private String vhostConfTpl;
}
