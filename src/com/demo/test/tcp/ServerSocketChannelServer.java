/*   
 * Copyright (c) 2017年10月31日 by XuanWu Wireless Technology Co.Ltd 
 *             All rights reserved  
 */
package com.demo.test.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:chengqiuping@wxchina.com ">qiuping.Cheng</a>
 * @version 1.0.0
 * @Description
 * @date 2017/10/31
 */
public class ServerSocketChannelServer {

    public static void main(String[] args) {
        //运行线程最大是3，容许线程最大是10，1000毫秒是对于线程池中容忍空闲时间，超过这个时间就会被新的线程取代，blockingQueue是用来执行线程的队列
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 100, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(100));
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(1244));
            serverSocketChannel.configureBlocking(false);//设置非阻塞模式
            while(true){
                /*如果设置了非阻塞模式，那么会一直轮询并返回结果，所以这句话会无限执行
                *如果没有设置非阻塞模式，这句话只会执行一次，因为在accept()的地方阻塞了
                 */
//                System.out.println("阻塞了");
                /**
                 * Accepts a connection made to this channel's socket.
                 *
                 * <p> If this channel is in non-blocking mode then this method will
                 * immediately return <tt>null</tt> if there are no pending connections.
                 * Otherwise it will block indefinitely until a new connection is available
                 * or an I/O error occurs.
                 * */
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel != null){
                    executor.submit(new SocketChannelThread(socketChannel));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
