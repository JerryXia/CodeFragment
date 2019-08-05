/**
 * 
 */
package com.github.jerryxia.devutil.springblock.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class ApplicationContextClosedListener implements ApplicationListener<ContextClosedEvent> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationContextClosedListener.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Application:{} Closed", event.getApplicationContext().getId());
    }
}
