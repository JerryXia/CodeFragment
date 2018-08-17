/**
 * 
 */
package com.github.jerryxia.healthcheck.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.access.AccessConstants;
import ch.qos.logback.core.BasicStatusManager;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;

/**
 * @author guqk
 *
 */
public class RecordLogViewStatusMessagesServlet extends ch.qos.logback.access.ViewStatusMessagesServlet {
    private static final long serialVersionUID = 443878494348593337L;

    public static StatusManager CURRENT_STATUS_MANAGER;

    @Override
    protected StatusManager getStatusManager(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext sc = getServletContext();
        Object statusManagerObj = sc.getAttribute(AccessConstants.LOGBACK_STATUS_MANAGER_KEY);
        if (statusManagerObj == null) {
            CURRENT_STATUS_MANAGER = new BasicStatusManager();
            sc.setAttribute(AccessConstants.LOGBACK_STATUS_MANAGER_KEY, CURRENT_STATUS_MANAGER);
        } else {
            if (CURRENT_STATUS_MANAGER == null) {
                CURRENT_STATUS_MANAGER = (StatusManager) statusManagerObj;
            }
        }
        return CURRENT_STATUS_MANAGER;
    }

    @Override
    protected String getPageTitle(HttpServletRequest req, HttpServletResponse resp) {
        return "<h2>日志记录</h2>\r\n";
    }

    public static void info(String msg, Object origin) {
        CURRENT_STATUS_MANAGER.add(new InfoStatus(msg, origin));
    }
}
