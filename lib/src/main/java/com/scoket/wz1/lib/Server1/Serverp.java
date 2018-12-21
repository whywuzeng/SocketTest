package com.scoket.wz1.lib.Server1;

import com.scoket.wz1.lib.Channel.constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2018-12-21.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Serverp.handle
 */
public class Serverp {

    public static void main(String[] args )
    {
        //启动TCPserver
        TCPServer tcpServer = new TCPServer();
        //自己server 的端口
        boolean start = tcpServer.start(TCPConstants.TCP_PORT_SERVER);
        //是否启动成功
        if (start)
        {
            System.out.println("服务器启动");
        }else {
            System.out.println("服务器失败");
            return;
        }
        //启动监听  UDPProvider
        UDPProvider udpProvider = new UDPProvider();
        udpProvider.start(TCPConstants.TCP_PORT_SERVER);

        //得到键盘内容发送广播
        boolean done=false;
        String Str=null;
        InputStream in = System.in;
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        do {
            try {
                Str = bufferedReader.readLine();
                tcpServer.send(Str);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }while (!"00bye00".equalsIgnoreCase(Str));

        // do  while  获取键盘内容 ，发送广播
        udpProvider.stop();
        tcpServer.stop();
    }
}
