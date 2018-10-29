/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import lombok.Data;

/**
 * @author guqk
 *
 */
@Data
public class ActuatorInstanceNode {
    private String serverName;

    private String ip;
    private int port;

    private String contextPath;
    private String queryWithTimestampParamName;
    private String header;
}
