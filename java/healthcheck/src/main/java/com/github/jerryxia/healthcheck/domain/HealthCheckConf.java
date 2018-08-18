/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import lombok.Data;

@Data
public class HealthCheckConf {
    private String path;
    private String queryWithTimestampParamName;
    private String cookie;
}
