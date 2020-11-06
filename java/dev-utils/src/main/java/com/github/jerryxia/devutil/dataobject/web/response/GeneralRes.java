/**
 * 
 */
package com.github.jerryxia.devutil.dataobject.web.response;

/**
 * @author JerryXia
 *
 */
public class GeneralRes {
    private int    code;
    private String msg;
    private Object data;
    private Exception exception;

    public GeneralRes() {
        this.code = GeneralResCode.Ok.getValue();
        this.msg = "";
    }

    public GeneralRes(int c, String message) {
        this.code = c;
        this.msg = message == null ? "" : message;
    }

    public GeneralRes fail() {
        this.code = GeneralResCode.Fail.getValue();
        return this;
    }

    public GeneralRes msg(String message) {
        this.msg = message == null ? "" : message;
        return this;
    }

    public GeneralRes failWithMsg(String message) {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
