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
public class CheckingInstanceNode {
    private String serverName;

    private String ip;
    private int port;
    private int weight;

    private String path;
    private String queryWithTimestampParamName;
    private String header;

    private boolean actived;

    private String vhostConfTplFilePath;
}
