package com.scoket.wz1.lib.clink.net.qiujuer.clink.box;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.ReceviePacket;

import java.io.IOException;


/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * 收到信息 会出现一个buffer
 *
 * buffer指定长度
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.box
 */
public class StringReceviePacket extends ReceviePacket {

    private int position;
    private byte[] buffer;

    public StringReceviePacket(int len) {
        buffer=new byte[len];
        lenght=len;
    }

    @Override
    public void savePacket(byte[] bytes, int count) {
        System.arraycopy(bytes,0,buffer,position,lenght);
        position+=count;
    }

    public String string(){
        return new String(buffer);
    }

    @Override
    public void close() throws IOException {

    }
}
