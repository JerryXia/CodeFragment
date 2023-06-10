/**
 * 
 */
package com.github.jerryxia.actuator.ui.sample.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsyncTestService {

    @Async
    public void log() {
        log.info("this log is write from one unconfigured taskExecutor application.");
    }
}
