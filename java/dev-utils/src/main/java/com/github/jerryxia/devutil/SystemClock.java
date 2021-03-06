/**
 * 
 */
package com.github.jerryxia.devutil;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * It is an alternative to the system clock
 * 
 * @author guqk
 *
 */
public class SystemClock {
    private final long    period;
    private volatile long now;

    private static final SystemClock INSTANCE = new SystemClock(1);

    private SystemClock(long period) {
        this.period = period;
        this.now = System.currentTimeMillis();
        this.scheduleClockUpdating();
    }

    /**
     * @return currentTimeMillis
     */
    public static long now() {
        return INSTANCE.currentTimeMillis();
    }

    /**
     * @return current date time
     */
    public static Date nowDate() {
        return new Date(INSTANCE.currentTimeMillis());
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new CustomNameThreadFactory("SystemClock"));
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                now = System.currentTimeMillis();
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return this.now;
    }
}
