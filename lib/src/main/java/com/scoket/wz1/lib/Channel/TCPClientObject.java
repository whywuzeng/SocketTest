package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class TCPClientObject {

    private  ReadHandle readHandle;
    private  Socket socket;
    private final PrintStream printStream;

    public TCPClientObject(ReadHandle readHandle,Socket socket) throws IOException {
        this.readHandle=readHandle;
        this.socket=socket;
        printStream = new PrintStream(socket.getOutputStream());
    }

    public static   TCPClientObject linkwith(ServerInfo info) throws IOException {

        //Socket 初始化

        Socket socket = new Socket();
        //超时
         socket.setSoTimeout(2000);
        //连接本地端口 2000，超时时间3000
        socket.connect(new InetSocketAddress(info.address, info.port),3000);
        System.out.println("已发起服务器连接，并进入后续流程～");

        //打印客户端 和 服务端信息
            System.out.println("客户端的信息是"+ socket.getLocalAddress()+ socket.getLocalPort());
            System.out.println("服务端的信息是"+ socket.getInetAddress().getHostAddress()+ socket.getPort());
        try {
        //接收数据，专门开个线程 干嘛
            ReadHandle  readHandle = new ReadHandle(socket.getInputStream());
            readHandle.start();

             return new TCPClientObject(readHandle,socket);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop() throws IOException {

        //退出操作
        this.readHandle.exit();

        this.socket.close();
        printStream.close();
        System.out.println("客户端已退出");
    }


    //发送接收数据
    public  void write(String msg){

               //读一行
               //获得键盘输入 用打印流发送
               if ("00bye00".equalsIgnoreCase(msg))
               {
                   return;
               }
               printStream.println(msg);

        //释放

    }

    //线程需要可停止
    static class ReadHandle extends Thread{

        private final InputStream mInputStream;
        private boolean done =false;

        public ReadHandle( InputStream inputStream) {
            this.mInputStream=inputStream;
        }

        @Override
        public void run() {
            super.run();
            //得到输入流
            BufferedReader bufferedReader=null;
            bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
            try {

                do {
                    String str;

                    try {
                        str  = bufferedReader.readLine();
                    }catch (SocketTimeoutException e)
                    {
                        continue;
                    }

                    //得到一行数据
                    if (str == null) {
                        System.out.println("客户端已无法读取数据！");
                        break;
                    }
                    //打印到屏幕
                    System.out.println(str);
                } while (!done);
            } catch (IOException e) {
                e.printStackTrace();
                if (!done)
                {
                    done=true;
                    exit();
                }
                System.out.println("客户端打印信息出现异常");
            } finally {
                CloseUtils.close(mInputStream);
            }

        }

        public void close(){
            CloseUtils.close(mInputStream);
        }

        void exit(){
          done=true;
            close();
        }
    }
}
