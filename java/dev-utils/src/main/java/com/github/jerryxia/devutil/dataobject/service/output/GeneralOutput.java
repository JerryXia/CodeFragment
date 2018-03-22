/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.service.output;

import java.util.Map;

/**
 * @author Administrator
 *
 */
public class GeneralOutput {
    private int                 code;
    private String              msg;
    private Map<String, Object> data;

    public GeneralOutput(int c, String message, Map<String, Object> d) {
        this.code = c;
        this.msg = message;
        this.data = d;
    }

    public GeneralOutput fail() {
        this.code = GeneralOutputCode.Fail.getValue();
        return this;
    }

    public GeneralOutput msg(String message) {
        this.msg = message;
        return this;
    }

    public GeneralOutput failWithMsg(String withMessage) {
        return fail().msg(withMessage);
    }

    public GeneralOutput failWithSysError() {
        return failWithMsg(GeneralOutputMsg.SYS_ERROR);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
