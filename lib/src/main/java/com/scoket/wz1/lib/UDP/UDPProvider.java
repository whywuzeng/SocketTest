package com.scoket.wz1.lib.UDP;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * Created by Administrator on 2018-12-19.
 * <p>
 * by author wz
 * 服务提供者
 * <p>
 * com.scoket.wz1.lib.UDP
 */
public class UDPProvider {

    public static void main(String[] args) throws Exception{

        System.out.println("UDPProvider Started");

        String uuids = UUID.randomUUID().toString();

        //可以供搜索，所有需要实时有效 和 可以停止 或者 重启

        //新建一个线程
        Provider provider = new Provider(uuids);
        provider.start();

        System.in.read();

        provider.exit();

    }

    private static class Provider extends Thread{

        private String sn;
        private DatagramSocket datagramSocket;
        private boolean done =false;

        public Provider(String sn) {
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();

            //监听服务器端的端口，20000 DatagramSocket
            try {
                datagramSocket = new DatagramSocket(20000);

            //一个while循环
                while (!done)
                {
                    byte[] buf = new byte[512];
                    //new DatagramPacket 接收数据buf
                    DatagramPacket datagramPacket = new DatagramPacket(buf, 0, buf.length);
                    //接收
                    datagramSocket.receive(datagramPacket);

                    //打印接收者的信息
                    String hostName = datagramPacket.getAddress().getHostName();
                    int port = datagramPacket.getPort();
                    byte[] data = datagramPacket.getData();
                    java.lang.String dataStr = new java.lang.String(data, 0, data.length);

                    System.out.println("接收到host为："+hostName+"端口号为:"+port);
                    System.out.println("数据流为"+dataStr);

                    //解析端口号
                    int port1 = MessageCreator.parsePort(dataStr);

                    if (port1!=-1) {
                        //构建发送数据
                        String sendData = MessageCreator.buildSN_header(sn);

                        byte[] bytes = sendData.getBytes();

                        //直接根据发送者  回送一份数据
                        DatagramPacket datagramPacket1 = new DatagramPacket(bytes,bytes.length,datagramPacket.getAddress(),port1);

                        datagramSocket.send(datagramPacket1);
                    }
                }


            } catch (java.io.IOException e) {
                e.printStackTrace();
            }finally {
                close();
            }

            System.out.println("UDPProvider is finished");

        }

        //关闭函数
        private void close(){
            if (datagramSocket!=null)
            {
                datagramSocket.close();
                datagramSocket=null;
            }
        }


        /**
         * 提供结束
         */
        void exit(){
            done =true;
            close();
        }
    }
}
