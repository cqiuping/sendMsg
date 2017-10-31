/*   
 * Copyright (c) 2017年10月31日 by XuanWu Wireless Technology Co.Ltd 
 *             All rights reserved  
 */
package com.demo.test.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author <a href="mailto:chengqiuping@wxchina.com ">qiuping.Cheng</a>
 * @version 1.0.0
 * @Description
 * @date 2017/10/31
 */
public class SocketChannelThread implements Runnable {
    private SocketChannel socketChannel;
    private String remoteName;

    public SocketChannelThread(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        try {
            this.remoteName = socketChannel.getRemoteAddress().toString();
            System.out.println("client:" + remoteName + "connect success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        StringBuilder sb = new StringBuilder();
        byte b[];
        while(true) {
            try {
                sizeBuffer.clear();
                int read = socketChannel.read(sizeBuffer);
                if (read != -1) {
                    sb.setLength(0);
                    sizeBuffer.flip();
                    int size = sizeBuffer.getInt();
                    int readCount = 0;
                    b = new byte[1024];
                    //读取已知长度消息内容
                    while (readCount < size) {
                        buffer.clear();
                        read = socketChannel.read(buffer);
                        if (read != -1) {
                            readCount += read;
                            buffer.flip();
                            int index = 0 ;
                            while(buffer.hasRemaining()) {
                                b[index++] = buffer.get();
                                if (index >= b.length) {
                                    index = 0;
                                    sb.append(new String(b,"UTF-8"));
                                }
                            }
                            if (index > 0) {
                                sb.append(new String(b,"UTF-8"));
                            }
                        }
                    }
                    System.out.println(remoteName +  ":" + sb.toString());
                }
            } catch (Exception e) {
                System.out.println(remoteName + " 断线了,连接关闭");
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                }
                break;
            }
        }
    }

}
