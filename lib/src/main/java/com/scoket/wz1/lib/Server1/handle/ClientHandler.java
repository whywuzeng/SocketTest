package com.scoket.wz1.lib.Server1.handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2018-12-21.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Serverp.handle
 */
public class ClientHandler {

    private final CloseNotify closeNotify;
    private final ClientWriteHandle writeHandle;
    private final ClientReadHandler clientReadHandler;
    private final Socket socket;
    private final InetAddress localAddress;
    private final int port;

    public ClientHandler(Socket socket, CloseNotify closeNotify) throws IOException {

        //socket 读流程
        clientReadHandler = new ClientReadHandler(socket);
        //socket写流程
        writeHandle = new ClientWriteHandle(socket.getOutputStream());
        //反馈线程
        this.closeNotify = closeNotify;
        this.socket =socket;

        localAddress = socket.getLocalAddress();
        port = socket.getPort();
        //新用户信息
        System.out.println("新客户端"+socket.getLocalAddress()+socket.getPort()+"进行接入了");

    }

    public void exit(){
        //线程退出
        clientReadHandler.exit();
        writeHandle.exit();
        //socket退出
        if (socket!=null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //客户端退出
        System.out.println("客户端"+localAddress+":"+port+"退出了");
    }

    public void send(String str){
        writeHandle.send(str);
    }

    public void readtoPrint(){
        clientReadHandler.start();
    }

    public void exitbySelf(){
        exit();
        closeNotify.onSelfClosed(this);
    }

    class ClientReadHandler extends Thread{

        private final InputStream inputStream;
        private final Socket socket;
        private boolean done =false;
        private BufferedReader bufferedReader;

        public ClientReadHandler(Socket socket) throws IOException {
            inputStream = socket.getInputStream();
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            do {
                //得到输入流  客户端拿到一条数据 接收数据
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                //接收一行数据
                String str = null;
                try {
                    str = bufferedReader.readLine();

                    if (str==null)
                    {
                        System.out.println("客户端已无法读取数据！");
                        ClientHandler.this.exitbySelf();
                        break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("客户端"+socket.getLocalAddress()+"出现问题..");
                }

                //打印到屏幕
                System.out.println("打印到屏幕"+str);
            }while (!done);

        }

        void exit(){
            done=true;
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ClientWriteHandle {
        private final PrintStream printStream;
        private final ExecutorService executorService;
        private boolean isdone =false;
        //单线程池
        //打印流 发送str


        public ClientWriteHandle(OutputStream outputStream) {
            printStream = new PrintStream(outputStream);
            executorService = Executors.newSingleThreadExecutor();
        }

        void exit(){
            printStream.close();
            executorService.shutdown();
        }

        void send(String msg){
            WriteRunable writeRunable = new WriteRunable(msg);
            executorService.execute(writeRunable);
        }

        class WriteRunable implements Runnable {

            private final String msg;

            public WriteRunable(String msg) {
                this.msg =msg;
            }

            @Override
            public void run() {
                if (ClientWriteHandle.this.isdone)
                {
                    return;
                }

                try {
                    ClientWriteHandle.this.printStream.println(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
