/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.web.response;

/**
 * @author JerryXia
 *
 */
public enum GeneralResCode {

    /**
     * OK
     */
    Ok(0),

    /**
     * Fail
     */
    Fail(1);

    private int value;

    GeneralResCode(int v) {
        this.value = v;
    }

    public int getValue() { 
        return value; 
    }
}
