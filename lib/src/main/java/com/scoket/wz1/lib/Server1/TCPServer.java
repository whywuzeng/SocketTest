package com.scoket.wz1.lib.Server1;

import com.scoket.wz1.lib.Server1.handle.ClientHandler;
import com.scoket.wz1.lib.Server1.handle.CloseNotify;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-12-21.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Serverp
 */
public class TCPServer {

    private ClientListener clientListener = null;

    public boolean start(int port){
        //初始化 clientListener  线程
        try {
            clientListener = new ClientListener(port);
            clientListener.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void send(String msg)
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
            for (ClientHandler clientHandler : list) {
                clientHandler.exitbySelf();
            }
        }
    }

    private class ClientListener extends Thread{

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
                    //得到socket

                    //获取accept

                    //客户端 ClientHandler 线程
                    ClientHandler clientHandler = new ClientHandler(client, new CloseNotify() {
                        @Override
                        public void onSelfClosed(ClientHandler handler) {
                            handler.exitbySelf();
                            handlerList.remove(handler);
                        }
                    });

                    //读取数据并打印
                    clientHandler.readtoPrint();
                    handlerList.add(clientHandler);
                } catch (IOException e) {
                    e.printStackTrace();
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
    }
}
