/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.web.response;

/**
 * @author Administrator
 *
 */
public enum GeneralResponseCode {

    /**
     * OK
     */
    Ok(1),

    /**
     * Fail
     */
    Fail(0);

    private int value;

    GeneralResponseCode(int v) {
        this.value = v;
    }

    public int getValue() { 
        return value; 
    }
}