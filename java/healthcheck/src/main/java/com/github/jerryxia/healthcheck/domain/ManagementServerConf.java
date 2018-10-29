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
public class ManagementServerConf {
    private String contextPath;
    private String queryWithTimestampParamName;
    private String header;
}
