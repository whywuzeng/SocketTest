package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;
import com.scoket.wz1.lib.Channel.constants.UDPConstants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

import sun.java2d.SurfaceDataProxy;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class UDPSearcher {

    private static final int LISTEN_PORT= UDPConstants.PORT_CLIENT_RESPONSE;

    public static ServerInfo searchServer(int timeout){

        //UDPSearchServer is start;

        //成功收到回送的栅栏

        //listener 的线程

        //发送广播

        //开启栅栏

        //打印设备信息

    }

    private static void listener() throws InterruptedException {
        //开启listenner
        //开启一个栅栏 等待开启listener后释放
        CountDownLatch startDownLatch = new CountDownLatch(1);

        //开启一个线程listener 线程

        startDownLatch.await();
    }

    private static class Listener extends Thread{

        private int listport;
        private DatagramSocket datagramSocket;
        private boolean done =false;

        @Override
        public void run() {
            super.run();
            //进入到线程里
            //startDownLatch .countdown;

            try {
                datagramSocket = new DatagramSocket(listport);
                byte[] buf=new byte[512];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

                //开启线程循环进行获取
                while (!done) {
                    datagramSocket.receive(datagramPacket);

                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
}
