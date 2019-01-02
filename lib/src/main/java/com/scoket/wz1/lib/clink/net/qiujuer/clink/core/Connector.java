package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.IoSelectAdapter;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.async.AsyncRecevieDispatcher;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.async.AsyncSendDispatcher;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.box.StringReceviePacket;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.box.StringSendPacket;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public class Connector implements IoSelectAdapter.ConnectorChannel,Closeable {

    private UUID key=UUID.randomUUID();
    private IoSelectAdapter ioSelectAdapter;
    private SendDispatcher mSendDispatcher;
    private RecevieDispatcher mRecevieDispatcher;


    //配置启动 channel
    public void setup(SocketChannel channel) throws IOException {
        IoContext build = IoContext.getInstance();

        ioSelectAdapter = new IoSelectAdapter(this,build.getIoProvider(),channel);
        channel.configureBlocking(false);

        mSendDispatcher=new AsyncSendDispatcher(ioSelectAdapter);
        mRecevieDispatcher=new AsyncRecevieDispatcher(callBack,ioSelectAdapter);
        mRecevieDispatcher.start();
    }

    protected boolean receiveNewMessage(String data) {

        return false;
    }

    public void send(String msg){
        SendPacket sendPacket = new StringSendPacket(msg);
        //启动调度了
        mSendDispatcher.start(sendPacket);
    }

    @Override
    public void connectorClose(SocketChannel channel) {

    }

    @Override
    public void close() throws IOException {
        ioSelectAdapter.close();
    }

    private  RecevieDispatcher.receviePacketCallBack callBack=new RecevieDispatcher.receviePacketCallBack() {
        @Override
        public void onReceviePacketCompleted(ReceviePacket packet) {
            if (packet instanceof StringReceviePacket)
            {
                String data = ((StringReceviePacket) packet).string();
                System.out.println("连接唯一UID="+key+"-------接收到的信息:"+data);
                receiveNewMessage(data);
            }
        }
    };

}
