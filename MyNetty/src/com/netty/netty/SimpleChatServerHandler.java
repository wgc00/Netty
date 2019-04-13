package com.netty.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

// 定义一个简单聊天室的类
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义 ChannelGroup 数组，记录用户
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 每当从服务端收到新的客户端连接时，客户端的频道存入频道组列表中，并通知列表中的其他客户端频道
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 获取一个通道对象
        Channel incoming = ctx.channel();
        // 循环看是否有多个用户
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - ".concat(String.valueOf(incoming.remoteAddress())).concat("加入\n"));
        }
        // 添加一个通道对象
        channels.add(ctx.channel());
    }

    // 每当从服务端收到客户端断开时，客户端的频道移除频道组列表中，并通知列表中的其他客户端频道
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - ".concat(String.valueOf(incoming.remoteAddress())).concat("离开\n"));
        }
        // 删除一个通道（可以说是一个用户的连接）
        channels.remove(ctx.channel());
    }

    //  每当从服务端读到客户端写入信息时，将信息转发给其他客户端的频道。
    //  其中如果你使用的是Netty 5.x版本时，需要把channelRead0 （）重命名为messageReceived（）
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {

            if (channel != incoming) {
                // 其他通信连接发送的信息格式
                channel.writeAndFlush("["
                        .concat(String.valueOf(incoming.remoteAddress()))
                        .concat("]")
                        .concat(msg)
                        .concat("\n"));
            } else {
                // 自己连接发送的信息
                channel.writeAndFlush("[You]：".concat(msg).concat("\n"));
            }
        }
    }
    // 服务端监听到客户端活动
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 有人进入连接时，在一个提醒报告
        System.out.println("SimpleChatClient:"
                .concat(String.valueOf(incoming.remoteAddress()))
                .concat("在线"));
    }
    // 服务端监听到客户端不活动
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        //
        System.out.println("SimpleChatClient:"
                    .concat(String.valueOf(incoming.remoteAddress()))
                    .concat("掉线"));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:"
                    .concat(String.valueOf(incoming.remoteAddress()))
                    .concat("异常"));
        cause.printStackTrace();
        ctx.close();
    }

}
