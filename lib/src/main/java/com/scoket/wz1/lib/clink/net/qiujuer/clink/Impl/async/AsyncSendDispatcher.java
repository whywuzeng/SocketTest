package com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.async;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.IoArgs;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Packet;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Send;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.SendDispatcher;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.SendPacket;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018-12-28.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.async
 */
public class AsyncSendDispatcher implements SendDispatcher {

    private final Send mSend;
    private final Queue<SendPacket> queue =new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isSending=new AtomicBoolean(false);

    private SendPacket tmpPacket;
    private IoArgs mIoArgs =new IoArgs();

    private int total; //当前大小
    private int position; //进度值

    public AsyncSendDispatcher(Send send) {
        this.mSend=send;
        
    }

    /**
     * 开始有数据来的时候
     * @param packet 数据
     */
    @Override
    public void start(SendPacket packet) {
        queue.offer(packet);
        //同步开关
        if (isSending.compareAndSet(false, true))
        {
            sendNestMsg();
        }
    }

    private void sendNestMsg() {

        SendPacket packet= tmpPacket;
        if (packet!=null)
        {
            //那packet来拆包，来发送
            //packet 有 封装包 状态
            //packet 有close  状态
            CloseUtils.close(packet);
        }

        SendPacket packet= tmpPacket = takePacket();
        if (packet==null)
        {
            //队列是取完了
            isSending.set(false);
            return;
        }

        total=packet.getLenght();
        position=0;
        sendCurrentPacket();
    }

    private void sendCurrentPacket() {
         IoArgs ioArgs= mIoArgs;
         ioArgs.startWriting();

         if (position>=total)
         {
             sendNestMsg();
             return;
         }else if (position == 0)
         {
             //首保 有长度信息 写入
             ioArgs.writeLenght(total);
         }

        byte[] aByte = tmpPacket.getByte();
        int count = ioArgs.readFrom(aByte, position);
        position+=count;

        ioArgs.finishWriting();

        try {
            mSend.sendAsync(mListener);
        } catch (Exception e) {
           closeAndNotify();
        }
    }

    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    @Override
    public void close() throws IOException {

    }

    private IoArgs.IoArgsEventListener mListener =new IoArgs.IoArgsEventListener() {
        @Override
        public void onStarted(IoArgs args) {

        }

        @Override
        public void onCompleted(IoArgs args) {
            //继续发送当前包 ，当前包还没完成
            sendCurrentPacket();
        }

        @Override
        public void onError(Exception e) {

        }
    };


    private SendPacket takePacket() {
        SendPacket packet = queue.poll();
        if (packet!=null && packet.isCanceled())
        {
            return takePacket();
        }
        return packet;
    }

    @Override
    public void cancel(SendPacket packet) {

    }

}
