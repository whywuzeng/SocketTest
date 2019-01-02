package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.Closeable;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public interface Receiver extends Closeable{
    //接收异步的方法
    boolean receiveAsync(IoArgs ioArgs,IoArgs.IoArgsEventListener listener) throws Exception;
}
