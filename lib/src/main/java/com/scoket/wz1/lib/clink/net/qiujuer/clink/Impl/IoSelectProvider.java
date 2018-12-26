package com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.IoProvider;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl
 */
public class IoSelectProvider implements IoProvider {

    AtomicBoolean isClose =new AtomicBoolean(false);

    @Override
    public boolean inputRegister(SocketChannel channel, handleInputCallback callback) {

        return register;
    }

    @Override
    public boolean outputRegister(SocketChannel channel, handleOutputCallback callback) {
        return false;
    }

    @Override
    public void unregisterInput() {

    }

    @Override
    public void unregisterOutput() {

    }

    @Override
    public void close() throws IOException {

    }
}
