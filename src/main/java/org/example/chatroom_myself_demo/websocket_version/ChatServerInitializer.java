package org.example.chatroom_myself_demo.websocket_version;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 将字节解码为HttpRequest、HttpContent和LastHttpContent。并将HttpRequest、HttpContent和LastHttpContent编码为字节
        pipeline.addLast(new HttpServerCodec());
        // 写入一个文件的内容
        pipeline.addLast(new ChunkedWriteHandler());
        // 将一个HttpMessage和跟随它的多个HttpContent聚合为单个FullHttpRequest或FullHttpResponse。安装这个之后，ChannelPipeLine中的下一个ChannelHandle只会收到完整的Http请求或响应
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        //处理HTTP请求，WEBSOCKET握手之前
        pipeline.addLast(new HTTPRequestHandler("/ws"));
        //按照WEBSOCKET规范要求，处理WEBSOCKET升级握手、PingWebSocketFrame、PongWebSocketFrame、CloseWebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //处理TextWebSocketFrame和握手完成事件
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }

}