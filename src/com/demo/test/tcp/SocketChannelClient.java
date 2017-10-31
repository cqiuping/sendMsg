/*   
 * Copyright (c) 2017年10月31日 by XuanWu Wireless Technology Co.Ltd 
 *             All rights reserved  
 */
package com.demo.test.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @author <a href="mailto:chengqiuping@wxchina.com ">qiuping.Cheng</a>
 * @version 1.0.0
 * @Description
 * @date 2017/10/31
 */
public class SocketChannelClient {

    public static void main(String[] args) throws IOException {
        //打开连接，但是并未连接
        SocketChannel socketChannel = SocketChannel.open();
        //这里才开始建立连接
        socketChannel.connect(new InetSocketAddress(1244));
        while (true) {
            Scanner sc = new Scanner(System.in);
            String next = sc.next();
            sendMessage(socketChannel, next);
        }

    }

    /**
     * 写了size和实际内容
     * @param socketChannel
     * @param msg
     * @throws IOException
     */
    public static void sendMessage(SocketChannel socketChannel, String msg)
            throws IOException {
        if (msg == null || msg.isEmpty()) {
            return;
        }

        byte[] bytes = msg.getBytes("UTF-8");
        int size = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);

        sizeBuffer.putInt(size);
        buffer.put(bytes);

        buffer.flip();
        sizeBuffer.flip();
        ByteBuffer[] dest = {sizeBuffer, buffer};
        while(sizeBuffer.hasRemaining() || buffer.hasRemaining()){
            socketChannel.write(dest);//还可以写到数组buffer里面
        }
    }

}
