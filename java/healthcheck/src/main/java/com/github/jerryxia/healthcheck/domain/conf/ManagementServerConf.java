/**
 * 
 */
package com.github.jerryxia.healthcheck.domain.conf;

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
