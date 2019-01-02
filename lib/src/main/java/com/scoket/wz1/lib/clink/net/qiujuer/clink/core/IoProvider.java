package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public interface IoProvider extends Closeable{

    //注册input
    boolean inputRegister(SocketChannel channel,handleInputCallback callback);
    //注册output
    boolean outputRegister(SocketChannel channel,handleOutputCallback callback);
    //取消注册input
    void unregisterInput(SocketChannel channel);
    //取消注册output
    void unregisterOutput(SocketChannel channel);

    abstract class handleInputCallback implements Runnable {

        protected Object attach;

        public final  <T> T getAttach() {
            T mAttach=  (T)this.attach;
            return mAttach;
        }
        public void setAttach(Object attach) {
            this.attach = attach;
        }

        @Override
        public void run() {
            canProviderInput(attach);
        }

        protected abstract void canProviderInput(Object args);
    }

    abstract class handleOutputCallback implements Runnable{

        protected Object attach;

        public final  <T> T getAttach() {
          T mAttach=  (T)this.attach;
          return mAttach;
        }

        public void setAttach(Object attach) {
            this.attach = attach;
        }

        @Override
        public void run() {
            canProviderOutput(attach);
        }

        protected abstract void canProviderOutput(Object attach);
    }

}
