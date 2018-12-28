package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * 发送packet   可以得到 byte  把packet变成byte
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public abstract class SendPacket extends Packet {

    private boolean isCanceled;

    public abstract byte[] getByte();

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public boolean isCanceled(){
        return isCanceled;
    }
}
