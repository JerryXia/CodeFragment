/**
 * 
 */
package com.github.jerryxia.healthcheck.web;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import com.github.jerryxia.devutil.dataobject.web.response.GeneralResponse;
import com.github.jerryxia.devutil.dataobject.web.response.GeneralResponseCode;

import lombok.val;

public class BaseController {
    protected ModelAndView getMvNotFound(String msg) {
        ModelAndView source = new ModelAndView("error");
        source.setStatus(HttpStatus.NOT_FOUND);
        source.addObject("ret", 0);
        source.addObject("msg", msg);
        source.addObject("exception", msg);
        return source;
    }

    protected ModelAndView getMvOk(String viewName) {
        ModelAndView source = new ModelAndView(viewName);
        source.addObject("ret", 1);
        // source.addObject("msg", msg);
        return source;
    }

    protected ModelAndView getMvRedirect(String jumpurl, String msg) {
        ModelAndView mv = new ModelAndView("redirect");
        mv.addObject("jumpurl", jumpurl);
        mv.addObject("msg", msg);
        return mv;
    }

    /**
     * 兼容lulu-ui的前端默认判断
     * 
     * @return
     */
    protected GeneralResponse okResponse() {
        val map = new HashMap<String, Object>(4);
        val res = new GeneralResponse(GeneralResponseCode.Fail.getValue(), StringUtils.EMPTY, map);
        return res;
    }

    /**
     * 兼容lulu-ui的前端默认判断
     * 
     * @param res
     * @param msg
     */
    protected void failResponse(GeneralResponse res, String msg) {
        res.setCode(1);
        res.setMsg(msg);
    }
}
