/**
 * 
 */
package com.github.jerryxia.devutil;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrator
 *
 */
public class CustomNameThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private static final AtomicInteger poolNumber = new AtomicInteger(0);
    private final String namePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private final boolean daemon;

    public CustomNameThreadFactory(String threadNamePrefix) {
        this("dev-utils", threadNamePrefix, true);
    }
    
    public CustomNameThreadFactory(String poolNamePrefix, String threadNamePrefix) {
        this(poolNamePrefix, threadNamePrefix, true);
    }

    public CustomNameThreadFactory(String poolNamePrefix, String threadNamePrefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = String.format("%s-%d-%s-", poolNamePrefix, poolNumber.getAndIncrement(), threadNamePrefix);
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if(thread.isDaemon() != this.daemon) {
            thread.setDaemon(daemon);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
