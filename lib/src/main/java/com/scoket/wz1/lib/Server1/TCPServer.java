package com.scoket.wz1.lib.Server1;

import com.scoket.wz1.lib.Server1.handle.ClientHandler;
import com.scoket.wz1.lib.Server1.handle.CloseNotify;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
    private ServerSocketChannel serverSocketChannel;
    private Selector open;

    public TCPServer(int port) {
        this.mPort=port;
        executorService = Executors.newSingleThreadExecutor();
    }

    public boolean start(){

        //初始化 clientListener  线程
        try {
            //seletor open
             open = Selector.open();

            //serversoketChannel.open
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);

            //绑定为本地端口
            serverSocketChannel.socket().bind(new InetSocketAddress(mPort));

            //注册客户端链接到监听
            serverSocketChannel.register(open, SelectionKey.OP_ACCEPT);

            this.serverSocketChannel=serverSocketChannel;

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

        CloseUtils.close(open);
        CloseUtils.close(serverSocketChannel);

        //释放
        executorService.shutdown();
    }

    private class ClientListener extends Thread implements CloseNotify {

        private final int port;
        private boolean done =false;
        private List<ClientHandler> handlerList=new ArrayList<>();

        public ClientListener(int port) throws IOException {
            this.port=port;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("服务器监听开始准备");
            //无限循环
            while (!done){
                try {
                    //用selector 得到客户端
                    //判断selector select 是否等于0
                    if (open.select() == 0)
                {
                    if (done)
                    {
                        break;
                    }
                    continue;
                }

                //用selectKeys 得到所有的key
                    Set<SelectionKey> selectionKeys = open.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext())
                    {
                        SelectionKey next = iterator.next();
                        iterator.remove();

                        //检查key是否到达状态
                        if (next.isAcceptable())
                        {
                            // /强转得到通道
                            ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                            //通道 非阻塞状态拿到 客户端
                            SocketChannel accept = channel.accept();

                            try {

                                //客户端 ClientHandler 线程
                                ClientHandler clientHandler = new ClientHandler(accept, this);

                                synchronized (TCPServer.this) {
                                    handlerList.add(clientHandler);
                                }

                            }catch (IOException e)
                            {
                                System.out.println("服务端读写socket出现异常");
                            }
                        }
                    }

                //然后再去构建客户端

                 }catch (IOException e)
                 {
                    continue;
                 }
                    //得到socket
                    //获取accept
            }

        }

        public void close(){
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
