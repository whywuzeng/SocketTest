package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * //复用Bytebuffer
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public class IoArgs {

    //构建byteBuffer
    byte[] bytes=new byte[256];
    ByteBuffer buffer=ByteBuffer.wrap(bytes);
    //send Byte 返回int
    public int readByte(SocketChannel channel) throws IOException {
         return channel.read(buffer);
    }

    //write Byte返回int
    public int writeByte(SocketChannel channel ) throws IOException {
        return channel.write(buffer);
    }

    //形成buffer 字符串
    public String bufferString(){
        return new String(buffer.array(),0,buffer.position());
    }

    //IoArgs 事件监听
   public interface IoArgsEventListener{
        void onStarted(IoArgs args);
        void onCompleted(IoArgs args);
    }
}
