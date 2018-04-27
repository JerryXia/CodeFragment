/**
 * 
 */
package com.github.jerryxia.healthcheck;

public class ServerNode {
    private String ip;
    private int port;

    private String path;
    private String queryParamName;
    
    
    private int weight;
    
    private boolean actived;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQueryParamName() {
        return queryParamName;
    }

    public void setQueryParamName(String queryParamName) {
        this.queryParamName = queryParamName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
    }
    
    
    
    
}
