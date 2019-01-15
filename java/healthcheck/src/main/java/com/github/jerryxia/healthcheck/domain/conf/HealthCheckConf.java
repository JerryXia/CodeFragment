/**
 * 
 */
package com.github.jerryxia.healthcheck.domain.conf;

import lombok.Data;

@Data
public class HealthCheckConf {
    private String path;
    private String queryWithTimestampParamName;
    private String header;
}
