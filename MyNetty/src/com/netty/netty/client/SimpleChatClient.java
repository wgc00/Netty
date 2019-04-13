package com.netty.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleChatClient {
    // 获取ip
    private final String host;
    // 获取端口号
    private final int port;

    public SimpleChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void run() throws Exception {
        // 创建线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建通信通道
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    // 配置
                    .handler(new SimpleChatClientInitializer());
            // 连接服务器的，并创建一个通信渠道
            Channel channel = bootstrap.connect(host, port).sync().channel();
            // 创建一个输入流
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                // 显示到控制上
                channel.writeAndFlush(in.readLine().concat("\r\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 跳出事件的循环
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        new SimpleChatClient("localhost", 8080).run();
    }
}

