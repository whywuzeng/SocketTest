package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;

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

        //超时

        //连接本地端口 2000，超时时间3000

//        System.out.println("已发起服务器连接，并进入后续流程～");

        //打印客户端 和 服务端信息

        //接收数据，专门开个线程 干嘛

        //发送接收数据

        //退出操作

        //释放资源

    }


    //发送接收数据
    public static void write(Socket socket){
        //构建键盘输入

        //得到socket 输出流，变成打印流

        //获得键盘输入 用打印流发送

        //读一行

        //释放
    }

    //线程需要可停止
    static class ReadHandle extends Thread{

        private boolean done =false;

        @Override
        public void run() {
            super.run();
            do {

                //得到输入流

                //得到一行数据

                //打印到屏幕

            }while (!done);
        }

        void exit(){

        }
    }
}
