package org.example.websocket_html_demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 通道消息读取
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("收到消息:" + msg.text());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("收到消息 " + msg.text()));
    }

    /**
     * 连接初始化
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("客户端连接:通道唯一id为 => " + ctx.channel().id().asLongText());
        System.out.println("客户端连接:通道不唯一id为 => " + ctx.channel().id().asShortText());
    }

    /**
     * 退出连接
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("客户端断开" + ctx.channel().id().asLongText());
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("客户端异常 " + cause.getMessage());
        //关闭连接
        ctx.close();
    }
}
