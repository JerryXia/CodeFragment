/**
 * 
 */
package com.github.jerryxia.healthcheck;

import java.util.ArrayList;

public class InstanceNode {
    private String serverName;
    private ArrayList<ServerNode> nodes;
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public ArrayList<ServerNode> getNodes() {
        return nodes;
    }
    public void setNodes(ArrayList<ServerNode> nodes) {
        this.nodes = nodes;
    }
    
    
    
    
}
