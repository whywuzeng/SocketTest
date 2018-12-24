package com.scoket.wz1.lib.Server1;

import com.scoket.wz1.lib.Server1.handle.ClientHandler;
import com.scoket.wz1.lib.Server1.handle.CloseNotify;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018-12-21.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Serverp
 */
public class TCPServer {

    private final int mPort;
    private final ExecutorService executorService;
    private ClientListener clientListener = null;

    public TCPServer(int port) {
        this.mPort=port;
        executorService = Executors.newSingleThreadExecutor();
    }

    public boolean start(){
        //初始化 clientListener  线程
        try {
            clientListener = new ClientListener(mPort);
            clientListener.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized void send(String msg)
    {
        if (clientListener!=null)
        {
            List<ClientHandler> list = clientListener.getList();
            for (ClientHandler clientHandler : list) {
                clientHandler.send(msg);
            }
        }
    }

    public void stop(){
        if (clientListener!=null)
        {
            clientListener.exit();
            List<ClientHandler> list = clientListener.getList();
            synchronized (TCPServer.this) {
                for (ClientHandler clientHandler : list) {
                    clientHandler.exitbySelf();
                }
            }
            list.clear();
        }
        executorService.shutdown();
    }

    private class ClientListener extends Thread implements CloseNotify {

        private final int port;
        private final ServerSocket mServerSocket;
        private boolean done =false;
        private List<ClientHandler> handlerList=new ArrayList<>();

        public ClientListener(int port) throws IOException {
            this.port=port;
            mServerSocket = new ServerSocket(port);
        }

        @Override
        public void run() {
            super.run();
            System.out.println("服务器监听开始准备");
            Socket client;
            //无限循环
            while (!done){



                 try {
                     client= mServerSocket.accept();
                 }catch (IOException e)
                 {
                    continue;
                 }
                    //得到socket

                    //获取accept
                try {

                    //客户端 ClientHandler 线程
                    ClientHandler clientHandler = new ClientHandler(client, this);

                    //读取数据并打印
                    clientHandler.readtoPrint();
                    synchronized (TCPServer.this) {
                        handlerList.add(clientHandler);
                    }

                }catch (IOException e)
                {
                    System.out.println("服务端读写socket出现异常");
                }

            }

        }

        public void close(){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void exit(){
            done=true;
            close();
        }

        List<ClientHandler> getList(){
            return handlerList;
        }

        @Override
        public synchronized void onSelfClosed(ClientHandler handler) {
            handlerList.remove(handler);
        }

        @Override
        public void onNewMessageArrived(final ClientHandler handler, final String msg) {
            System.out.println("recevie"+handler.getClientInfo()+"信息为:"+msg);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (TCPServer.this)
                    {
                        for (ClientHandler clientHandler : handlerList) {
                            if (clientHandler.equals(handler))
                            {
                                continue;
                            }
                            clientHandler.send(msg);
                        }
                    }
                }
            });
        }
    }
}
