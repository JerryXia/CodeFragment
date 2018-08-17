/**
 * 
 */
package com.github.jerryxia.healthcheck.model;

/**
 * @author guqk
 *
 */
public class CheckingInstanceNode extends InstanceNode{
    private boolean actived;

    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
    }
}
