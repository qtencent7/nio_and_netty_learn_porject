package org.example.nio_server.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class ChatServer {

    public static void main(String[] args) throws IOException {
        new ChatServer().startServer();
    }

    public void startServer() throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(8000));

        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        log.info("服务器启动成功");

        for(;;) {
            int channelNum = selector.select();
            if(channelNum == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();
                if(next.isAcceptable()) {
                    acceptOperator(serverSocketChannel, selector);
                }
                if(next.isReadable()) {
                    readOperator(selector, next);
                }
            }
        }

    }

    private void readOperator(Selector selector, SelectionKey next) throws IOException {

        SocketChannel channel = (SocketChannel) next.channel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        int len = channel.read(byteBuffer);
        String str = "";
        if(len > 0) {
            byteBuffer.flip();

            str = str + Charset.forName("UTF-8").decode(byteBuffer);
        }
        channel.register(selector, SelectionKey.OP_READ);

        if(str.length() > 0) {
            log.info(str);
            castOtherClient(str, selector, channel);
        }
    }

    private void castOtherClient(String str, Selector selector, SocketChannel channel) throws IOException {

        Set<SelectionKey> keys = selector.keys();

        for (SelectionKey key : keys) {
            SelectableChannel targetChannel = key.channel();
            if(targetChannel instanceof SocketChannel && targetChannel != channel) {
                ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(str));
            }
        }
    }

    private void acceptOperator(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        socketChannel.write(Charset.forName("UTF-8").encode("欢迎进入聊天室，请注意隐私安全。"));

    }
}
