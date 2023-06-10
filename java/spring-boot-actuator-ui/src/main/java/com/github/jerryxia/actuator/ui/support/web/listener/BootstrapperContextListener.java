package com.github.jerryxia.actuator.ui.support.web.listener;

import com.github.jerryxia.actuator.ui.Bootstrapper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Administrator
 *
 */
public final class BootstrapperContextListener implements ServletContextListener {
    private Bootstrapper bootstrapper;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        bootstrapper = new Bootstrapper();
        bootstrapper.init();
        event.getServletContext().log("spring-boot-actuator-ui BootstrapperContextListener contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (this.bootstrapper != null) {
            this.bootstrapper.shutdown();
            this.bootstrapper = null;
        }
        event.getServletContext().log("spring-boot-actuator-ui BootstrapperContextListener contextDestroyed");
    }

}