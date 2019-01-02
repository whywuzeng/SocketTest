package com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.async;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.box.StringReceviePacket;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.IoArgs;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Receiver;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.RecevieDispatcher;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.ReceviePacket;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2019-1-2.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.async
 */
public class AsyncRecevieDispatcher implements RecevieDispatcher {

    private RecevieDispatcher.receviePacketCallBack packetCompleteCallBack;
    //接受者
    private Receiver mReceiver;
    //ioargs
    private IoArgs ioArgs = new IoArgs();

    private ReceviePacket tmpPacket;

    private int total;

    private int position;

    //装数据的buffer 接受到信息
    private byte[] buffer;

    private AtomicBoolean isClose =new AtomicBoolean(false);

    @Override
    public void start() {
        registerReceiver();
    }

    //开启接受一条信息，开启监听，添加一个runable
    private void registerReceiver() {
        try {
            mReceiver.receiveAsync(ioArgs,listener);
        } catch (Exception e) {
            CloseUtils.close(this);
        }
    }

    public AsyncRecevieDispatcher(receviePacketCallBack packetCompleteCallBack, Receiver mReceiver) {
        this.packetCompleteCallBack = packetCompleteCallBack;
        this.mReceiver = mReceiver;
    }

    @Override
    public void stop() {

    }

    @Override
    public void close() throws IOException {
        if (isClose.compareAndSet(false,true))
        {
            ReceviePacket packet = this.tmpPacket;
            if (packet!=null)
            {
                CloseUtils.close(packet);
            }
        }
    }

    private IoArgs.IoArgsEventListener listener=new IoArgs.IoArgsEventListener() {
        @Override
        public void onStarted(IoArgs args) {
            // 长度的长度
            int recevieSize;
            if (tmpPacket==null)
            {
                recevieSize=4;
            }else {
                recevieSize = Math.min(total - position, args.getCap());
            }
            args.limit(recevieSize);
        }
        //接受到一条信息
        @Override
        public void onCompleted(IoArgs args) {
            assemblePacket(args);
            //接受吓一条数据
            registerReceiver();
        }

        @Override
        public void onError(Exception e) {

        }
    };

    private void assemblePacket(IoArgs args) {
        //1.得到包长度
        if (tmpPacket==null)
        {
            int lenght = args.readLenght();
            total=lenght; //整個packet的長
            position=0;
            tmpPacket = new StringReceviePacket(lenght);
            buffer=new byte[lenght];

        }else {
            //得到多少数据
            int i = args.writeTo(buffer, 0);
            if (i>0)
            {
                tmpPacket.savePacket(buffer,i);
               position+=i;
                 if (position>=total)
            {
                //接受完成 本次IoArgs 接受完成
                completePacket();
                return;
            }
            }
        }
    }

    private void completePacket() {
        packetCompleteCallBack.onReceviePacketCompleted(tmpPacket);
        CloseUtils.close(tmpPacket);
        tmpPacket=null;
    }
}
