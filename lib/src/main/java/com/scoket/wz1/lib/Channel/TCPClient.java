package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class TCPClient {

    public static void linkwith(ServerInfo info){

        //Socket 初始化
        try {
            Socket socket = new Socket();
        //超时
         socket.setSoTimeout(2000);
        //连接本地端口 2000，超时时间3000
        socket.connect(new InetSocketAddress(info.address, info.port),3000);
        System.out.println("已发起服务器连接，并进入后续流程～");

        //打印客户端 和 服务端信息
            System.out.println("客户端的信息是"+socket.getLocalAddress()+socket.getLocalPort());
            System.out.println("服务端的信息是"+socket.getInetAddress().getHostAddress()+socket.getPort());

        //接收数据，专门开个线程 干嘛
            ReadHandle readHandle = new ReadHandle(socket);
            readHandle.start();

            //发送接收数据
           write(socket);

        //退出操作
        readHandle.exit();
        //释放资源

          socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //发送接收数据
    public static void write(Socket socket){
        //构建键盘输入
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //得到socket 输出流，变成打印流
        PrintStream printStream=null;
        try {
            OutputStream outputStream = socket.getOutputStream();
             printStream = new PrintStream(outputStream);

            //读一行
            String str = bufferedReader.readLine();
            //获得键盘输入 用打印流发送
            printStream.println(str);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (printStream != null) {
                printStream.close();
            }
        }
        //释放

    }

    //线程需要可停止
    static class ReadHandle extends Thread{

        private final Socket mSocket;
        private boolean done =false;

        public ReadHandle(Socket socket) {
            this.mSocket=socket;
        }

        @Override
        public void run() {
            super.run();
            do {

                //得到输入流
                BufferedReader bufferedReader=null;
                try {
                     bufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                    String str = bufferedReader.readLine();

                    //得到一行数据

                    //打印到屏幕
                    System.out.println(str);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("TCPClient 读取数据失败"+mSocket.getLocalAddress());
                }finally {
                    try {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        done=true;
                    }
                }

            }while (!done);
        }

        public void close(){
        }

        void exit(){
          done=true;
            close();
        }
    }
}
