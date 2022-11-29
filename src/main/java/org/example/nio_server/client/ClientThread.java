package org.example.nio_server.server.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;


@Slf4j
public class ClientThread implements Runnable{

    private Selector selector;
    public ClientThread(Selector selector) {
        this.selector = selector;
    }
    @Override
    public void run() {
        try {
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
                    if(next.isReadable()) {
                        readOperator(selector, next);
                    }
                }
            }
        } catch(Exception e) {

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
        }
    }
}
