package com.netty.serivce;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

    public static void main(String[] args) throws InterruptedException {


       // int port = Integer.parseInt(args[1]);
        // 处理请求和处理服务器响应的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建一个引导对象
            Bootstrap b = new Bootstrap();
            // 绑定一个线程组
            b.group(workerGroup);
            // 创建一个引导
            b.channel(NioSocketChannel.class);

            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeServerHandler());
                }
            });

            // 启动客户端
            ChannelFuture future = b.connect("127.0.0.1",8888).sync();
            //ChannelFuture future = b.connect("127.0.0.1", 8000).sync();
            // 等待连接关闭
            future.channel().closeFuture().sync();
        } finally {
                workerGroup.shutdownGracefully();
        }
    }
}
