package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;
import com.scoket.wz1.lib.Channel.constants.UDPConstants;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class UDPSearcher {

    private static final int LISTEN_PORT= UDPConstants.PORT_CLIENT_RESPONSE;

    public static ServerInfo searchServer(int timeout) throws InterruptedException, IOException {

        //UDPSearchServer is start;
        System.out.println("UDPSearchServer is start");

        //成功收到回送的栅栏
        CountDownLatch recevieLatch = new CountDownLatch(1);
        //listener 的线程
        Listener listener = listener(LISTEN_PORT, recevieLatch);
        //发送广播
        sendBroadcast();
        //开启栅栏
        recevieLatch.await(timeout, TimeUnit.SECONDS);
        //打印设备信息
        List<ServerInfo> listAndClose = listener.getListAndClose();
        for (ServerInfo serverInfo : listAndClose) {
            System.out.println(serverInfo.toString());
        }
        return listAndClose.get(0);
    }

    private static Listener listener(int listenPort, CountDownLatch recevieLatch) throws InterruptedException {
        //开启listenner
        //开启一个栅栏 等待开启listener后释放
        CountDownLatch startDownLatch = new CountDownLatch(1);

        //开启一个线程listener 线程
        Listener listener = new Listener(startDownLatch, recevieLatch, UDPConstants.PORT_CLIENT_RESPONSE, listenPort);
        listener.start();
        startDownLatch.await();
        return listener;
    }

    private static void sendBroadcast() throws IOException {

        DatagramSocket datagramSocket = new DatagramSocket();

        ByteBuffer allocate = ByteBuffer.allocate(128);
        allocate.put(UDPConstants.HEADER);
        allocate.putShort((short)1);
        allocate.putInt(LISTEN_PORT);

        DatagramPacket datagramPacket=new DatagramPacket(allocate.array(),allocate.position()+1);

        datagramPacket.setAddress(InetAddress.getByName("255.255.255.255"));

        datagramPacket.setPort(UDPConstants.PORT_SERVER);
        datagramSocket.send(datagramPacket);

    }

    private static class Listener extends Thread{

        private final CountDownLatch countDownLatch;
        private final CountDownLatch mRecevieLatch;
        private final int listenPort;
        private int listport;
        private  final   byte[] buf=new byte[512];
        private DatagramSocket datagramSocket;
        private boolean done =false;
        private final List<ServerInfo> mServerInfo=new ArrayList<>();
        private int minLen= UDPConstants.HEADER.length + 2 + 4;

        public Listener(CountDownLatch startDownLatch, CountDownLatch recevieLatch, int listport1, int listenPort) {
            countDownLatch =startDownLatch;
            this.mRecevieLatch=recevieLatch;
            this.listport=listport1;
            this.listenPort=listenPort;
        }

        @Override
        public void run() {
            super.run();
            //进入到线程里
            countDownLatch.countDown();

            try {
                datagramSocket = new DatagramSocket(listport);
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

                //开启线程循环进行获取
                while (!done) {
                    datagramSocket.receive(datagramPacket);

                    //打印出信息
                    String serverip = datagramPacket.getAddress().getHostAddress();
                    int serverPort = datagramPacket.getPort();
                    int length = datagramPacket.getLength();
                    byte[] serverData = datagramPacket.getData();
                    //判断是否有效
                    boolean isVaild = length >= UDPConstants.HEADER.length + 2 + 4 && ByteUtils.startsWith(serverData, UDPConstants.HEADER);

                    System.out.println("UDPSearcher receive form ip:" + serverip
                            + "\tport:" + serverPort + "\tdataValid:" + isVaild);

                    if (!isVaild)
                    {
                        continue;
                    }

                    ByteBuffer byteBuffer = ByteBuffer.wrap(buf, UDPConstants.HEADER.length, length);
                    final short cmd = byteBuffer.getShort();
                    final int serverPort1 = byteBuffer.getInt();
                    if (cmd != 2 || serverPort <= 0) {
                        System.out.println("UDPSearcher receive cmd:" + cmd + "\tserverPort:" + serverPort);
                        continue;
                    }

                    //ByteBuffer
//                    wrap包裹buffer

//                    list.add()元素
                    String sn = new String(buf, minLen, length - minLen);
                    //搜索到服务器的信息
                    ServerInfo info = new ServerInfo(serverPort1, serverip, sn);
                    mServerInfo.add(info);

                    //成功的接收到一份
                    mRecevieLatch.countDown();
                }

            } catch (java.io.IOException e) {
                done=true;
            }finally {
                close();
            }
        }

        public void close(){
            if (datagramSocket!=null)
            {
                datagramSocket.close();
                datagramSocket=null;
            }
        }

        List<ServerInfo> getListAndClose(){
            done=true;
            close();
            return mServerInfo;
        }
    }
}
