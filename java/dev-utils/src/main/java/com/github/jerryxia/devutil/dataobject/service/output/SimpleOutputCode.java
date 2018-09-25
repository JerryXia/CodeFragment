/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.service.output;

/**
 * @author Administrator
 *
 */
public enum SimpleOutputCode {

    /**
     * OK
     */
    Ok(0),

    /**
     * Fail
     */
    Fail(1);

    private int value;

    SimpleOutputCode(int v) {
        this.value = v;
    }

    public int getValue() { 
        return value; 
    }
}
