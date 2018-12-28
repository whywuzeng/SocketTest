package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.Closeable;

/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * 放到一个队列里
 * 然后对基本数据进行包装
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public interface SendDispatcher extends Closeable{
    /**
     * 开始发送packet
     * @param packet 数据
     */
    public void start(SendPacket packet);

    /**
     * 取消发送packet
     * @param packet 数据
     */
    public void cancel(SendPacket packet);
}
