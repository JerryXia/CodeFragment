/**
 * 
 */
package com.github.jerryxia.spafilehttpserver;

import java.util.concurrent.TimeUnit;

import com.github.jerryxia.spafilehttpserver.handler.SpaFileHttpRequestHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class SpaFileHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final DefaultEventExecutorGroup bizWorkerGroup;
    private final SslContext                sslCtx;

    public SpaFileHttpServerInitializer(SslContext sslCtx, DefaultEventExecutorGroup bizWorkerGroup) {
        this.sslCtx = sslCtx;
        this.bizWorkerGroup = bizWorkerGroup;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast("idleState", new IdleStateHandler(0, 0, 90 * 1000, TimeUnit.MILLISECONDS))
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(65536))// 64 * 1024
                // 新增Chunked handler，主要作用是支持异步发送大的码流（例如大文件传输）
                // 但是不占用过多的内存，防止发生java内存溢出错误
                .addLast(new ChunkedWriteHandler())
                //.addLast(this.bizWorkerGroup, "spaFileHttpRequestHandler", new SpaFileHttpRequestHandler());
                .addLast("spaFileHttpRequestHandler", new SpaFileHttpRequestHandler());
    }
}