package com.scoket.wz1.lib.clink.net.qiujuer.clink.box;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.SendPacket;

import java.io.IOException;

/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.box
 */
public class StringSendPacket extends SendPacket{

    private byte[] bytes;

    public  StringSendPacket(String msg){
        bytes = msg.getBytes();
        lenght=bytes.length;
    }

    @Override
    public byte[] getByte() {
        return bytes;
    }

    @Override
    public void close() throws IOException {

    }
}
