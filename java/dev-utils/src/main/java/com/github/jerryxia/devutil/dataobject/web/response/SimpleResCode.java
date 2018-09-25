/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.web.response;

/**
 * @author Administrator
 *
 */
public enum SimpleResCode {

    /**
     * OK
     */
    Ok(0),

    /**
     * Fail
     */
    Fail(1);

    private int value;

    SimpleResCode(int v) {
        this.value = v;
    }

    public int getValue() { 
        return value; 
    }
}