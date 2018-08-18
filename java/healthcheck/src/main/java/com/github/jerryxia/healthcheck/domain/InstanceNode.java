/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import lombok.Data;

@Data
public class InstanceNode {
    private String ip;
    private int port;

    private int weight;
    private boolean actived;
}
