package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.Closeable;

/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public abstract class Packet implements Closeable{
    protected byte type;
    protected int lenght;

    public byte getType() {
        return type;
    }

    public int getLenght() {
        return lenght;
    }
}
