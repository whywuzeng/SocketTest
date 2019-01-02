package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.Closeable;

/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * 接受数据封装
 * 吧一个和多个IOArgs 封装成一个packet
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public interface RecevieDispatcher extends Closeable{
    void start();

    void stop();

    interface receviePacketCallBack{
        void onReceviePacketCompleted(ReceviePacket packet);
    }

}
