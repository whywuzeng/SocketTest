package com.scoket.wz1.lib.Server1.handle;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Connector;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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
    private final SocketChannel socket;
    private final Connector connector;

    public String getClientInfo() {
        return clientInfo;
    }

    private final String clientInfo;

    public ClientHandler(SocketChannel socket, final CloseNotify closeNotify) throws IOException {

        //拿到 socketChannel 客户端   上级服务端是 serversocketChannel通道
        this.socket=socket;
        //socket非阻塞通道

         connector = new Connector() {
            @Override
            public boolean receiveNewMessage(String s) {
                closeNotify.onNewMessageArrived(ClientHandler.this,s);
                return super.receiveNewMessage(s);
            }

            @Override
            public void connectorClose(SocketChannel channel) {
                super.connectorClose(channel);
                exitbySelf();
            }
        };
        connector.setup(socket);

        //构建selector 设置状态
        Selector write = Selector.open();
        socket.register(write, SelectionKey.OP_WRITE);

        //反馈线程
        this.closeNotify = closeNotify;

        //新用户信息
//        this.clientInfo="A[" + socket.getInetAddress().getHostAddress()
//                + "] P[" + socket.getPort() + "]";

         this.clientInfo=socket.getRemoteAddress().toString();
    }

    public void exit(){
        CloseUtils.close(connector);
        //socket退出
        if (socket!=null) {
            CloseUtils.close(socket);
        }
    }

    public void send(String str){
        connector.send(str);
    }

    public void exitbySelf(){
        exit();
        closeNotify.onSelfClosed(this);
    }

}
