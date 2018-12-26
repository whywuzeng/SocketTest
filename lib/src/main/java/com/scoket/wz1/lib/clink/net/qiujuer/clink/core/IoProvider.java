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
    void unregisterInput();
    //取消注册output
    void unregisterOutput();

    abstract class handleInputCallback implements Runnable {

        @Override
        public void run() {
            canProviderInput();
        }

        protected abstract void canProviderInput();
    }

    abstract class handleOutputCallback implements Runnable{
        @Override
        public void run() {
            canProviderOutput();
        }

        protected abstract void canProviderOutput();
    }

}
