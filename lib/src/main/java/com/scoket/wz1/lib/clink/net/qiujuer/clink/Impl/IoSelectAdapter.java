package com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.IoArgs;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.IoProvider;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Receiver;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Send;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018-12-27.
 * <p>
 * by author wz
 * 要进行发送和接受
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl
 */
public class IoSelectAdapter implements Send,Receiver,Closeable{

    private final IoProvider provider;
    private final SocketChannel channel;
    private final AtomicBoolean isClose =new AtomicBoolean(false);
    private final IoSelectAdapter.ConnectorChannel listener;

    public IoSelectAdapter(IoSelectAdapter.ConnectorChannel listener, IoProvider provider, SocketChannel channel) {
        this.provider=provider;
        this.channel=channel;
        this.listener=listener;
    }

    @Override
    public void close() throws IOException {
        isClose.compareAndSet(false,true);
        provider.unregisterInput(channel);
        provider.unregisterOutput(channel);
         CloseUtils.close(provider);
        CloseUtils.close(channel);
        listener.connectorClose(channel);

    }

    @Override
    public boolean receiveAsync(IoArgs ioArgs,IoArgs.IoArgsEventListener listener) throws Exception {
        if (isClose.get())
        {
            throw new Exception("channel is close");
        }
        receiveHandleInputCallback callback = new receiveHandleInputCallback(channel, listener);
        callback.setAttach(ioArgs);

        return provider.inputRegister(channel,callback);
    }

    @Override
    public boolean sendAsync(IoArgs ioArgs, IoArgs.IoArgsEventListener listener) throws Exception {
        if (isClose.get())
        {
            throw new Exception("channel is close");
        }
        sendHandleOutputBack mhandleOutput = new sendHandleOutputBack(channel, listener);
        mhandleOutput.setAttach(ioArgs);

        return provider.outputRegister(channel,mhandleOutput);
    }

    static class receiveHandleInputCallback extends IoProvider.handleInputCallback{

        private final SocketChannel channel;
        private final IoArgs.IoArgsEventListener listener;

        public receiveHandleInputCallback(SocketChannel channel, IoArgs.IoArgsEventListener listener) {
            this.channel=channel;
            this.listener=listener;
        }

        @Override
        protected void canProviderInput(Object args) {

            try {
            IoArgs ioArgs= getAttach();
            if (listener!=null)
            {
                listener.onStarted(ioArgs);
            }
            int i = ioArgs.readFrom(channel);
            if (i<0)
            {
                listener.onError(new Exception("readByte < 0,channel 传送链接失败"));
                return;
            }else {
                if (listener!=null)
                {
                    listener.onCompleted(ioArgs);
                }
            }
            }catch (IOException e){
                e.printStackTrace();
                listener.onError(e);
            }

        }
    }

    static class sendHandleOutputBack extends IoProvider.handleOutputCallback{

        private final SocketChannel channel;
        private final IoArgs.IoArgsEventListener listener;

        public sendHandleOutputBack(SocketChannel channel, IoArgs.IoArgsEventListener listener) {
            this.channel=channel;
            this.listener=listener;
        }

        public void setAttach(Object attach)
        {
            this.attach =attach;
        }

        @Override
        protected void canProviderOutput(Object attach) {

            try {
                IoArgs ioArgs = getAttach();
            if (listener!=null)
            {
                listener.onStarted(ioArgs);
            }

            int i = ioArgs.writeTo(channel);
            if (i<0)
            {
                listener.onError(new Exception("writeByte < 0,channel 传送链接失败"));
            }else {
                if (listener!=null)
                {
                    listener.onCompleted(ioArgs);
                }
            }

            }catch (IOException e){
                e.printStackTrace();
                listener.onError(e);
            }
        }
    }

    public interface ConnectorChannel{
        void connectorClose(SocketChannel channel);
    }

}
