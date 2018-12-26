package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-12-25.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class ClientTest {

    private static boolean done =false;

    public static void main(String args[]) {
        //搜索到服务器 info
        ServerInfo serverInfo = null;
        try {
             serverInfo = UDPSearcher.searchServer(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serverInfo==null)
        {
            return;
        }
        System.out.println(serverInfo.toString());

        int size=0;
        final List<TCPClientObject> tcpClients=new ArrayList<>();
        for (int i=0;i<10;i++)
        {
            try {
                TCPClientObject linkwith = TCPClientObject.linkwith(serverInfo);
                if (linkwith==null)
                {
                    System.out.println("链接异常");
                    continue;
                }

                tcpClients.add(linkwith);

                System.out.println("链接成功"+size++);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("链接异常");
            }

        }

        //睡眠20
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //键盘停顿
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //线程发送消息

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                while (!done)
                {
                    for (TCPClientObject tcpClient : tcpClients) {
                        tcpClient.write("hello !!!");
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        //等待完成
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //关闭while循环
        done=true;

        //join线程
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //退出 clienthandle
        for (TCPClientObject tcpClient : tcpClients) {
            try {
                tcpClient.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
