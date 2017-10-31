/*   
 * Copyright (c) 2017年10月31日 by XuanWu Wireless Technology Co.Ltd 
 *             All rights reserved  
 */
package com.demo.test.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:chengqiuping@wxchina.com ">qiuping.Cheng</a>
 * @version 1.0.0
 * @Description then client is the same with DataGamdChannelSendTest
 * @date 2017/10/31
 */
public class DataDramdChannelSelectorServer {
    public static void main(String[] args) throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.bind(new InetSocketAddress(1234));
        //这句话一定要写
        datagramChannel.configureBlocking(false);
        Selector selector = Selector.open();
        datagramChannel.register(selector, SelectionKey.OP_READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] b;

        while(true){
            int select = selector.select();
            if(select > 0){
                System.out.println("获取了select的个数大于0");
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    buffer.clear();
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isReadable()){
                        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
                        SocketAddress socketAddress = channel.receive(buffer);
                        int position = buffer.position();
                        b = new byte[position];
                        buffer.flip();
                        for(int i=0; i<position; ++i) {
                            b[i] = buffer.get();
                        }
                        System.out.println("receive remote " +  socketAddress.toString() + ":"  + new String(b, "UTF-8"));

                    }
                    iterator.remove();
                }
            }
        }
    }

}
