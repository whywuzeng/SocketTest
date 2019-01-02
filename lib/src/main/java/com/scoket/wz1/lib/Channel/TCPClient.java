package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Connector;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class TCPClient extends Connector{


    public TCPClient(SocketChannel channel) throws IOException {
        setup(channel);
    }

    public static TCPClient linkwith(ServerInfo info) throws IOException {
        //用socket channel 初始化。
        SocketChannel socket =SocketChannel.open();
        //超时
//        socket.setSoTimeout(2000);
        //连接本地端口 2000，超时时间3000
        socket.connect(new InetSocketAddress(info.address, info.port));
        System.out.println("已发起服务器连接，并进入后续流程～");

        //打印客户端 和 服务端信息
            System.out.println("客户端的信息是"+ socket.getLocalAddress());
            System.out.println("服务端的信息是"+ socket.getRemoteAddress());
          try {
              return new TCPClient(socket);
          }catch (Exception e)
          {
              System.out.println("连接异常");
              CloseUtils.close(socket);
          }
          return null;
    }

    

    @Override
    public void connectorClose(SocketChannel channel) {
        super.connectorClose(channel);
        System.out.println("客户端已退出");
    }

    //发送接收数据
    public static void write(TCPClient client){
        //构建键盘输入
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //得到socket 输出流，变成打印流
        try {
           do {
               //读一行
               String str = bufferedReader.readLine();
               //获得键盘输入 用打印流发送
               client.send(str);
               client.send(str);
               client.send(str);
               client.send(str);

               if ("00bye00".equalsIgnoreCase(str))
               {
                   break;
               }
           }while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //释放
    }

}
