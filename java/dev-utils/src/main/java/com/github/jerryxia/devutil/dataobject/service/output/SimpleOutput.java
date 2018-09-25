/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.service.output;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class SimpleOutput {
    private int                 code;
    private String              msg;
    private Map<String, Object> data;

    public SimpleOutput() {
        this.code = SimpleOutputCode.Ok.getValue();
        this.msg = "";
        this.data = new HashMap<String, Object>(8);
    }

    public SimpleOutput(int c, String message, Map<String, Object> d) {
        this.code = c;
        this.msg = message;
        this.data = d;
    }

    public SimpleOutput fail() {
        this.code = SimpleOutputCode.Fail.getValue();
        return this;
    }

    public SimpleOutput msg(String message) {
        this.msg = message == null ? "" : message;
        return this;
    }

    public SimpleOutput failWithMsg(String message) {
        return fail().msg(message == null ? "" : message);
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
        this.msg = msg == null ? "" : msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
