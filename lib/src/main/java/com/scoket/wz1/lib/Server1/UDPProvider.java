package com.scoket.wz1.lib.Server1;

import com.scoket.wz1.lib.Channel.constants.UDPConstants;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by Administrator on 2018-12-21.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Server1
 */
public class UDPProvider {

    private static Provider INTANSE;

    public void start(int port) {
        stop();
        String sn = UUID.randomUUID().toString();
        INTANSE = new Provider(port, sn);
        INTANSE.start();
    }

    public void stop(){
        if (INTANSE!=null)
        {
            INTANSE.exit();
            INTANSE=null;
        }
    }

    class Provider extends Thread{

        private final int mPort;
        private final byte[] sn;
        private DatagramSocket datagramSocket;
        private DatagramPacket datagramPacket;
        private boolean done =false;
        private byte[] buffer=new byte[128];

        public Provider(int port,String sn) {
            mPort =port;
            this.sn=sn.getBytes();
        }

        @Override
        public void run() {
            super.run();
            while (!done) {
                try {
                    datagramSocket = new DatagramSocket(UDPConstants.PORT_SERVER);
                    datagramPacket = new DatagramPacket(buffer, buffer.length);

                    datagramSocket.receive(datagramPacket);

                    String hostAddress = datagramPacket.getAddress().getHostAddress();
                    int port = datagramPacket.getPort();
                    int length = datagramPacket.getLength();
                    byte[] clientData = datagramPacket.getData();

                    boolean isVaild = length >= UDPConstants.HEADER.length + 2 + 4 && ByteUtils.startsWith(clientData, UDPConstants.HEADER);

                    System.out.println("UDPProvider receive form ip:" + hostAddress
                            + "\tport:" + port + "\tdataValid:" + isVaild);

                    if (!isVaild) {
                        continue;
                    }

                    // 解析命令与回送端口
                    int index = UDPConstants.HEADER.length;
                    short cmd = (short) ((clientData[index++] << 8) | (clientData[index++] & 0xff));

                    int responsePort = (((clientData[index++]) << 24) |
                            ((clientData[index++] & 0xff) << 16) |
                            ((clientData[index++] & 0xff) << 8) |
                            ((clientData[index] & 0xff)));
                    //判断
                    if (cmd ==1 &&responsePort>0)
                    {
                        ByteBuffer wrap = ByteBuffer.wrap(buffer);
                        wrap.put(UDPConstants.HEADER);
                        wrap.putShort((short)2);
                        wrap.putInt(mPort);
                        wrap.put(sn);

                        int lenght = wrap.position();

                        DatagramPacket responsePacket = new DatagramPacket(buffer, length, this.datagramPacket.getAddress(), responsePort);

                        datagramSocket.send(responsePacket);

                        System.out.println("UDPProvider response to:" + hostAddress + "\tport:" + responsePort + "\tdataLen:" + lenght);
                    }else {
                        System.out.println("UDPProvider receive cmd nonsupport; cmd:" + cmd + "\tport:" + port);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {

                }

            }
        }

        private void close(){
            if (datagramSocket!=null)
            {
                datagramSocket.close();
                datagramSocket=null;
            }
        }

        void exit(){
            done=true;
            close();
        }
    }
}
