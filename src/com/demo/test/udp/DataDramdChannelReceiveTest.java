/*   
 * Copyright (c) 2017年10月31日 by XuanWu Wireless Technology Co.Ltd 
 *             All rights reserved  
 */
package com.demo.test.udp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * @author <a href="mailto:chengqiuping@wxchina.com ">qiuping.Cheng</a>
 * @version 1.0.0
 * @Description
 * @date 2017/10/31
 */
public class DataDramdChannelReceiveTest {

    public static void main(String[] args) throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(1234));

        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        datagramChannel.configureBlocking(false);
        byte[] b;
        while (true) {
            buffer.clear();
            System.out.println("阻塞了");//设置了非阻塞模式，这个无限输出
            SocketAddress socketAddress = datagramChannel.receive(buffer);
            if (socketAddress != null) {
                int position = buffer.position();
                b = new byte[position];
                buffer.flip();
                for (int i = 0; i < position; ++i) {
                    b[i] = buffer.get();
                }
                System.out.println(
                        "receive remote" + socketAddress.toString() + ":" + new String(b, "UTF-8"));
                sendReback(socketAddress, datagramChannel);
            }
        }
    }

    public static void sendReback(SocketAddress socketAddress, DatagramChannel datagramChannel)
            throws IOException {
        String message = "I have receive your message";
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(message.getBytes("UTF-8"));
        buffer.flip();
        datagramChannel.send(buffer, socketAddress);
    }

}
