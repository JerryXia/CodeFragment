/**
 * 
 */
package com.github.jerryxia.devutil.springblock.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.github.jerryxia.devutil.RuntimeVariables;

public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationReadyListener.class);

    /**
     * <p>Object bean = event.getApplicationContext().getBean("webRequestLoggingFilter");</p>
     * <p>if(bean != null && bean instanceof WebRequestTraceFilter) {<p>
     * <p>WebRequestTraceFilter webRequestTraceFilter = (WebRequestTraceFilter)bean;</p>
     * <p>webRequestTraceFilter.setDumpRequests(true);</p>
     * <p>}</p>
     * 
     * @param event 
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("{} find Application:'{}' is Ready", RuntimeVariables.LIB_ARTIFACT_ID, event.getApplicationContext().getId());
    }
}