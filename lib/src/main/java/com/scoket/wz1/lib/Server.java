package com.scoket.wz1.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2018-12-18.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib
 */
public class Server {

    public static void main(String[] args)throws Exception{

        ServerSocket serverSocket = new ServerSocket(2000);
        //准备服务器 socket

        //等待客户连接 死循环
        for (;;){
            //得到客户端
            Socket accept = serverSocket.accept();
            HandleThread handleThread = new HandleThread(true, accept);
            handleThread.start();
        }

        // 构建线程 启动线程
    }

    /**
     * 客户端专有线程
     */
    private static class HandleThread extends Thread {

        private boolean isflag=false;
        private Socket mSocket;

        public HandleThread(boolean isflag, Socket mSocket) {
            this.isflag = isflag;
            this.mSocket = mSocket;
        }
        //需要socket 实例  即客户端实例

        @Override
        public void run() {
            super.run();

            //客户端 信息
            System.out.println("新客户端连接:"+mSocket.getLocalAddress()+"P:"+mSocket.getPort());
            //打印流打印信息，

            PrintStream mSocketoutput=null;
            BufferedReader mSocketinput=null;
            try {
                 mSocketoutput = new PrintStream(mSocket.getOutputStream());

                //getinputstream 接收数据
                 mSocketinput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                do {
                    String str = mSocketinput.readLine();
                    if (str.equalsIgnoreCase("bye"))
                    {
                        isflag=false;
                        mSocketoutput.println("bye");
                    }else {
                        System.out.println(str);
                        mSocketoutput.println("回送"+str.length());
                    }

                }while (isflag);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("服务端连接出现异常");
            }

            //客户端信息表明
            mSocketoutput.close();
            try {
                mSocketinput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("客户端已退出IP:"+mSocket.getLocalAddress()+"端口号:"+mSocket.getPort());
        }
    }

}
