package com.scoket.wz1.lib.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018-12-19.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.UDP
 */
public class UDPSearcher {

    private static final int LISTENER_PORT=30000;

    public static void main(String[] args) throws Exception{
        System.out.println("UDPSearcher is start");

        //进行监听
        Listener listener = listener();

        //发送广播
        sendBroadcast();

        System.in.read();

        List<Device> devicesList = listener.getDevicesList();

        for (Device device:devicesList) {
            System.out.println("接收到的设备是:"+device.toString());
        }

        System.out.println("UDPSearcher is finished");
    }

    //初始化listerner
    public static Listener listener() throws InterruptedException {
//         开启listerner
        System.out.println("UDPSearcher start listen.");
        //递减数初始化
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //开启
        Listener listener = new Listener(countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;
    }


    public static void sendBroadcast() {
        System.out.println("UDPSearcher sendBroadcast started");
        // 定义一个datagramSocket 端口分配
        try {
            DatagramSocket datagramSocket = new DatagramSocket();

            //构建请求数据
            String dataStr = MessageCreator.buildPort_Header(LISTENER_PORT);
            byte[] bytes = dataStr.getBytes();
            //构建packet
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            //20000 发送给 提供服务方
            packet.setAddress(InetAddress.getByName("255.255.255.255"));
            packet.setPort(20000);
            //发送
            datagramSocket.send(packet);
            //完成
            datagramSocket.close();

            System.out.println("UDPSeacher sendBroadcast is finished");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static class Device{
        //ip
        //sn
        //port
        public String ip;

        public String sn;

        public int port;

        public Device(String ip, String sn, int port) {
            this.ip = ip;
            this.sn = sn;
            this.port = port;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    ", port=" + port +
                    '}';
        }
    }

    public static class Listener extends Thread{

        private boolean done =false;
        CountDownLatch mDownLatch;
        private List<Device> devices =new ArrayList<>();

        public Listener(CountDownLatch mDownLatch) {
            this.mDownLatch = mDownLatch;
        }

        @Override
        public void run() {
            super.run();
            //通知已启动
            mDownLatch.countDown();
            System.out.println("listener thread 已经启动");
            //监听回送接口 30000  DatagramSocket
            DatagramSocket datagramSocket =null;
            try {
                 datagramSocket = new DatagramSocket(LISTENER_PORT);

                while (!done){
                    //构建buf 实体
                    byte[] buf=new byte[512];
                    //new DatagramPacket
                    DatagramPacket datagramPacket = new DatagramPacket(buf, 0, buf.length);
                    //接收
                     datagramSocket.receive(datagramPacket);
                    //打印接收的信息
                    String hostAddress = datagramPacket.getAddress().getHostAddress();
                    int port = datagramPacket.getPort();
                    byte[] data = datagramPacket.getData();
                    String dataStr = new String(data, 0, data.length, "utf-8");

                    String sn = MessageCreator.parseSN(dataStr);
                    if (sn!=null)
                    {
                        //构建device
                        Device device = new Device(hostAddress, sn, port);
                        devices.add(device);
                    }

                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
                System.out.println("UDPSearcher 出现错误!");
            }finally {
                if (datagramSocket != null) {
                    datagramSocket.close();
                    datagramSocket=null;
                }
            }

            System.out.println("UDPSearcher 完成");
        }

        List<Device> getDevicesList(){
            done=true;
            return devices;
        }
    }

}
