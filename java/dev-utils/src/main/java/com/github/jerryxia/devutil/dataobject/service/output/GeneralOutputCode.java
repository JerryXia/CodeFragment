/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.service.output;

/**
 * @author Administrator
 *
 */
public enum GeneralOutputCode {

    /**
     * OK
     */
    Ok(1),

    /**
     * Fail
     */
    Fail(0);

    private int value;

    GeneralOutputCode(int v) {
        this.value = v;
    }

    public int getValue() { 
        return value; 
    }
}
