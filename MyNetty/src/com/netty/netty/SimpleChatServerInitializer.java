package com.netty.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {
    /* 配置渠道的处理方式*/
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline p = ch.pipeline();
        // 读取的数据，最大长度设置为 8192 过了，这个长度，只能等待下一次的读取
        p.addLast("framer",  new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // 解码器
        p.addLast("decoder", new StringDecoder());
        // 编码器
        p.addLast("encoder", new StringEncoder());
        // 处理器
        p.addLast("handler", new SimpleChatServerHandler());

        System.out.println("SimpleChatClient:"
                .concat(String.valueOf(ch.remoteAddress()))
                .concat("连接上"));
    }
}
