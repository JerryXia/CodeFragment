/**
 * 
 */
package com.github.jerryxia.healthcheck.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

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

    protected void tkd(ModelAndView mv, String title, String keywords, String description) {
        mv.addObject("title", title == null ? "" : title);
        mv.addObject("keywords", keywords == null ? "" : keywords);
        mv.addObject("description", description == null ? "" : description);
    }
}
