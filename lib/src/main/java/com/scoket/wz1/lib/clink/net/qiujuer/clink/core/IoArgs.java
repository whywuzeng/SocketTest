package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.EOFException;
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
    //从byte里 哪里开始读数据

    private int limit;

    private int cap;

    public int readFrom(byte[] bytes,int offset)
    {
        //吧形参的bytes read bytebuffer里
        int size = Math.min(bytes.length - offset, buffer.remaining());
        buffer.put(bytes,0,size);
        //本次操作的 长度
        return size;
    }
    //写到byte里去，从哪里开始写

    public int writeTo(byte[] bytes,int offset)
    {
        //write bytebuffer里去
        int size = Math.max(bytes.length - offset, buffer.remaining());
        //这里是 写到 bytes里去
        buffer.get(bytes,0,size);
        return size;
    }
    //write Byte返回int

    //从 socketCHannel 读取数据
    public int readFrom(SocketChannel channel) throws IOException {
        startWriting();

        int bytesProduced =0;
        //返回是否还有未读内容
        while (buffer.hasRemaining())
        {
            int len = channel.read(buffer);
            if (len<0)
            {
                //异常
                throw new EOFException();
            }
            bytesProduced+=len;
        }

        finishWriting();

        return bytesProduced;
    }

    //写数据到 socketChannel里去
    public int writeTo(SocketChannel channel ) throws IOException {
            int byteProduced =0;
            while (buffer.hasRemaining())
            {
                int len = channel.write(buffer);
                if (len<0)
                {
                    throw new EOFException();
                }
                byteProduced+=len;
            }

        return byteProduced;
    }

    //设置limit大小
    public void limit(int limit){
        this.limit= limit;
    }

    //开始写入数据
    public void startWriting(){
        buffer.clear();
        //限制大小limit
        buffer.limit(limit);
    }

    //完成吸入数据
    public void finishWriting(){
        buffer.flip();
    }

    //形成buffer 字符串
    public String bufferString(){
        return new String(buffer.array(),0,buffer.position());
    }

    public void writeLenght(int total) {
        buffer.putInt(total);
    }

    //读出 首包长度
    public int readLenght()
    {
        return buffer.getInt();
    }

    public int getCap() {
        return bytes.length;
    }

    //IoArgs 事件监听
   public interface IoArgsEventListener{
        void onStarted(IoArgs args);
        void onCompleted(IoArgs args);
        void onError(Exception e);
    }
}
