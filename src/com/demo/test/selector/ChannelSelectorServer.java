/*   
 * Copyright (c) 2017年10月31日 by XuanWu Wireless Technology Co.Ltd 
 *             All rights reserved  
 */
package com.demo.test.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:chengqiuping@wxchina.com ">qiuping.Cheng</a>
 * @version 1.0.0
 * @Description
 * @date 2017/10/31
 */
public class ChannelSelectorServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(1244));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int select = selector.select();
            if (select > 0) {
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();

                    //接收连接请求
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = channel.accept();
                        System.out
                                .println("接收到连接请求:" + socketChannel.getRemoteAddress().toString());
                        socketChannel.configureBlocking(false);
                        //也注册一个Read
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }else if(selectionKey.isReadable()){
                        receiveMessage(selectionKey);
                    }
                    iterator.remove();
                }
            }
        }
    }

    //这个和tcp的那个是一样的
    public static void receiveMessage(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        String remoteName = socketChannel.getRemoteAddress().toString();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        StringBuilder sb = new StringBuilder();
        byte b[];
        try {
            sizeBuffer.clear();
            int read = socketChannel.read(sizeBuffer);
            if (read != -1) {
                sb.setLength(0);
                sizeBuffer.flip();
                int size = sizeBuffer.getInt();
                int readCount = 0;
                b = new byte[1024];
                // 读取已知长度消息内容
                while (readCount < size) {
                    buffer.clear();
                    read = socketChannel.read(buffer);
                    if (read != -1) {
                        readCount += read;
                        buffer.flip();
                        int index = 0;
                        while (buffer.hasRemaining()) {
                            b[index++] = buffer.get();
                            if (index >= b.length) {
                                index = 0;
                                sb.append(new String(b, "UTF-8"));
                            }
                        }
                        if (index > 0) {
                            sb.append(new String(b, "UTF-8"));
                        }
                    }
                }
                System.out.println(remoteName + ":" + sb.toString());
            }
        } catch (Exception e) {
            System.out.println(remoteName + " 断线了,连接关闭");
            try {
                //取消这个通道的注册，关闭资源
                selectionKey.cancel();
                socketChannel.close();
            } catch (IOException ex) {
            }
        }
    }

}
