/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.web.response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class SimpleRes {
    private int                 code;
    private String              msg;
    private Map<String, Object> data;

    public SimpleRes() {
        this.code = SimpleResCode.Ok.getValue();
        this.msg = "";
        this.data = new HashMap<String, Object>(8);
    }

    public SimpleRes(int c, String message, Map<String, Object> d) {
        this.code = c;
        this.msg = message == null ? "" : message;
        this.data = d;
    }

    public SimpleRes fail() {
        this.code = SimpleResCode.Fail.getValue();
        return this;
    }

    public SimpleRes msg(String message) {
        this.msg = message == null ? "" : message;
        return this;
    }

    public SimpleRes failWithMsg(String message) {
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