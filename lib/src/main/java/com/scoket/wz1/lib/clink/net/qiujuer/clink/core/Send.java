package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.Closeable;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public interface Send extends Closeable{

    boolean sendAsync(IoArgs ioArgs, IoArgs.IoArgsEventListener listener) throws Exception;
}
