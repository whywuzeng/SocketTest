package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * 接受packet 要把这个packet 存起来
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public abstract class ReceviePacket extends Packet{
    public abstract void  savePacket(byte[] bytes,int count);
}
