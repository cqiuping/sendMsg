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
import java.util.Scanner;

/**
 * @author <a href="mailto:chengqiuping@wxchina.com ">qiuping.Cheng</a>
 * @version 1.0.0
 * @Description
 * @date 2017/10/31
 */
public class DataGramdChannelSendTest {

    public static void main(String[] args) throws IOException {

        final DatagramChannel channel = DatagramChannel.open();
//        channel.configureBlocking(false);
        //接收消息线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                byte b[];
                while(true) {
                    System.out.println("阻塞了"); //设置了非阻塞模式无限输出
                    buffer.clear();
                    SocketAddress socketAddress = null;
                    try {
                        socketAddress = channel.receive(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (socketAddress != null) {
                        int position = buffer.position();
                        b = new byte[position];
                        buffer.flip();
                        for(int i=0; i<position; ++i) {
                            b[i] = buffer.get();
                        }
                        try {
                            System.out.println("receive remote " +  socketAddress.toString() + ":"  + new String(b, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

        //发送控制台输入消息
        while (true) {
            Scanner sc = new Scanner(System.in);
            String next = sc.next();
            try {
                sendMessage(channel, next);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendMessage(DatagramChannel channel, String mes) throws IOException {
        if (mes == null || mes.isEmpty()) {
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        buffer.put(mes.getBytes("UTF-8"));
        buffer.flip();
        System.out.println("send msg:" + mes);
        channel.send(buffer, new InetSocketAddress("localhost",1234));
    }
}
