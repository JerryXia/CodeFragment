/**
 * 
 */
package com.github.jerryxia.actuator.ui.support.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jerryxia.actuator.ui.support.spring.SpringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

/**
 * @author guqk
 *
 */
public class RuntimeJsonComponentProviderFactory {
    private static final Logger log = LoggerFactory.getLogger(RuntimeJsonComponentProviderFactory.class);

    private static JsonComponentProvider target;

    public static JsonComponentProvider tryFindImplementation() {
        if (target == null) {
            synchronized (log) {
                if(target == null) {
                    ObjectMapper jacksonObjectMapper = null;
                    try {
                        jacksonObjectMapper = SpringTools.getBean("jacksonObjectMapper", ObjectMapper.class);
                    } catch (BeansException e1) {
                        log.info("can not find bean:jacksonObjectMapper, try to find bean inherit ObjectMapper");
                        try {
                            jacksonObjectMapper = SpringTools.getBean(ObjectMapper.class);
                        } catch (BeansException e2) {
                            log.info("can not find any bean object inherit ObjectMapper, try to new ObjectMapper()");
                            jacksonObjectMapper = new ObjectMapper();
                        }
                    }
                    log.info("final jacksonObjectMapper is: {}", jacksonObjectMapper.getClass().getName());
                    JsonComponentProvider provider = new JacksonProvider(jacksonObjectMapper);
                    target = provider;
                }
            }
        }
        return target;
    }

}
