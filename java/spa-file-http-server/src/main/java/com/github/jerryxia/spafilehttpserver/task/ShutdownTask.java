/**
 * 
 */
package com.github.jerryxia.spafilehttpserver.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.github.jerryxia.spafilehttpserver.App;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ShutdownTask implements Runnable {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ShutdownTask.class);

    private final NioEventLoopGroup         bossGroup;
    private final NioEventLoopGroup         workerGroup;
    private final DefaultEventExecutorGroup bizWorkerGroup;

    public ShutdownTask(NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, DefaultEventExecutorGroup bizWorkerGroup) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.bizWorkerGroup = bizWorkerGroup;
    }

    @Override
    public void run() {
        File shutDownFile = new File(App.SHUTDOWN_PID_FILE);
        if (!shutDownFile.exists()) {
            try {
                if (shutDownFile.createNewFile()) {
                    writePidFile(App.SHUTDOWN_PID_FILE, App.PID);
                }
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            writePidFile(App.SHUTDOWN_PID_FILE, App.PID);
        }
        // watch for file deleted then shutdown
        while (true) {
            try {
                if (shutDownFile.exists()) {
                    Thread.sleep(1000);
                } else {
                    // QueryProductServer.serving = false;
                    logger.info("shutdown starting......");
                    bizWorkerGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                    logger.info("shutdown end.");
                    System.exit(0);
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    private void writePidFile(String shutdownfilePath, String pid) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(shutdownfilePath));
            out.write(pid);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
