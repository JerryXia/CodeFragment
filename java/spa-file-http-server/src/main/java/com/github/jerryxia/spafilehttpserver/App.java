package com.github.jerryxia.spafilehttpserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

import com.github.jerryxia.spafilehttpserver.task.ShutdownTask;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Hello world!
 *
 */
public class App {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(App.class);

    public static final Properties APP_PROPS         = loadProps("app.properties");
    public static final String     SHUTDOWN_PID_FILE = APP_PROPS.getProperty("shutdownfile");
    public static final String     PID               = getProcessID();


    public static void main(String[] args) throws Exception {
        // 加载配置
        int port = Integer.parseInt(APP_PROPS.getProperty("port"));

        int bizWorkerCount = Integer.parseInt(APP_PROPS.getProperty("bizWorkerCount"));

        // 初始化
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup bizWorkerGroup = new DefaultEventExecutorGroup(bizWorkerCount);

        Thread shutdownProcessor = new Thread(new ShutdownTask(bossGroup, workerGroup, bizWorkerGroup), "shutdownProcessor");
        shutdownProcessor.start();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new SpaFileHttpServerInitializer(null, bizWorkerGroup))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync();
            // 阻塞处理，等待服务端链路关闭之后main函数才退出
            f.channel().closeFuture().sync();
            logger.info("channel ending......");
        } finally {
            logger.info("channel finally end");
            // workerGroup.shutdownGracefully();
            // bossGroup.shutdownGracefully();
            // MnsQueueUtil.shutdown();
        }
    }

    /**
     * 加载属性文件
     */
    private static Properties loadProps(String fileName) {
        Properties props = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException(fileName + " file is not found");
            }
            props = new Properties();
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    private static String getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName().split("@")[0];
    }
}
