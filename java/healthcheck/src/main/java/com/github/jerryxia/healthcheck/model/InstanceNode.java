/**
 * 
 */
package com.github.jerryxia.healthcheck.model;

import lombok.Data;

@Data
public class InstanceNode {
    private String ip;
    private int port;

    private int weight;
}
