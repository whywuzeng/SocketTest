package com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.IoProvider;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl
 */
public class IoSelectProvider implements IoProvider {

    private final Selector read;
    private final Selector write;
    private final ExecutorService inputPoolSelectProvider;
    private final ExecutorService outputPoolSelectProvider;

    AtomicBoolean isClose =new AtomicBoolean(false);

    AtomicBoolean inputReg =new AtomicBoolean(false);
    AtomicBoolean outputReg=new AtomicBoolean(false);

    private final HashMap<SelectionKey,Runnable> inputMapRunable =new HashMap<>();
    private final HashMap<SelectionKey,Runnable> outputMapRunable =new HashMap<>();

    public IoSelectProvider() throws IOException {


        inputPoolSelectProvider = Executors.newFixedThreadPool(4, new SelectProviderThreadFactory("inputSelectProvider"));

        outputPoolSelectProvider = Executors.newFixedThreadPool(4, new SelectProviderThreadFactory("outputSelectProvider"));

        read = Selector.open();
        write = Selector.open();
        readSelector();
        writeSelector();
    }

    private void writeSelector() {
        //得到一个selector
        Thread thread = new Thread("selectProvider-write-selector"){
            @Override
            public void run() {
                super.run();
                try {

                    while (!isClose.get())
                    {
                        if (write.select()==0)
                        {
                            waitHandleRegister(outputReg);
                            continue;
                        }

                        Iterator<SelectionKey> iterator = write.selectedKeys().iterator();
                        while (iterator.hasNext())
                        {
                            if (iterator.next().isValid()) {
                                handleSelection(iterator.next(),SelectionKey.OP_WRITE, outputMapRunable,outputPoolSelectProvider);
                            }
                        }

                    }

                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        };

        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void readSelector() {
        //得到一个selector
            Thread thread = new Thread("selectProvider-read-selector"){
                @Override
                public void run() {
                    super.run();
                    try {

                    while (!isClose.get())
                    {
                        if (read.select()== 0)
                        {
                            waitHandleRegister(inputReg);
                            continue;
                        }

                        Iterator<SelectionKey> iterator = read.selectedKeys().iterator();
                        while (iterator.hasNext())
                        {
                            SelectionKey key = iterator.next();
                            if (key.isValid()) {
                                handleSelection(key,SelectionKey.OP_READ, inputMapRunable,inputPoolSelectProvider);
                            }
                        }

                    }

                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

            };

            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
    }

    @Override
    public boolean inputRegister(SocketChannel channel, handleInputCallback callback) {
        //注册监听
        return handleRegister(read,channel,callback,SelectionKey.OP_READ,inputMapRunable,inputReg)!=null;

    }

    private SelectionKey handleRegister(Selector read, SocketChannel channel, Runnable callback, int opregisterKey, HashMap<SelectionKey, Runnable> map, AtomicBoolean locker) {

        synchronized (locker){
            locker.set(true);
        try {
            //先wakeup  先前selector是select状态
            read.wakeup();

            SelectionKey key=null;

            if (channel.isRegistered())
            {
                 key = channel.keyFor(read);
                 if (key!=null) {
                     key.interestOps(key.readyOps() | opregisterKey);
                 }
            }

            if (key==null) {
                key= channel.register(read, opregisterKey);
                map.put(key,callback);
            }
            return key;
        } catch (ClosedChannelException e) {
            return null;
        }finally {
            locker.set(false);

            locker.notify();
        }

        }
    }

    private void waitHandleRegister(AtomicBoolean outputReg) {
        synchronized (outputReg) {
            if (outputReg.get()) {
                try {
                    outputReg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleSelection(SelectionKey key, int opRead, HashMap<SelectionKey, Runnable> inputMapRunable, ExecutorService inputPoolRunnable) {

        //修改这个next 注册状态
        key.cancel();

        Runnable runnable =null;

        if (inputMapRunable.get(key)!=null)
        {
            runnable=inputMapRunable.get(key);

            if (runnable!=null&&!inputPoolRunnable.isShutdown()) {
                inputPoolRunnable.execute(runnable);
            }
        }
    }

    @Override
    public boolean outputRegister(SocketChannel channel, handleOutputCallback callback) {

        //注册监听
        return handleRegister(write,channel,callback,SelectionKey.OP_WRITE,outputMapRunable,outputReg)!=null;
    }

    @Override
    public void unregisterInput(SocketChannel channel) {
         handleunRegister(channel,read,inputMapRunable);
    }

    private void handleunRegister(SocketChannel channel, Selector read, HashMap<SelectionKey, Runnable> inputMapRunable) {

        if (channel.isRegistered()) {
            SelectionKey selectionKey = channel.keyFor(read);
            if (selectionKey != null) {
                selectionKey.cancel();
                inputMapRunable.remove(selectionKey);
                read.wakeup();
            }
        }
    }

    @Override
    public void unregisterOutput(SocketChannel channel) {
        handleunRegister(channel,write, outputMapRunable);
    }

    @Override
    public void close() throws IOException {
        //停止了 while死循环
        isClose.compareAndSet(false,true);

        read.wakeup();
        write.wakeup();

        CloseUtils.close(read);
        CloseUtils.close(write);

        inputMapRunable.clear();
        inputMapRunable.clear();

        inputPoolSelectProvider.shutdown();
        outputPoolSelectProvider.shutdown();
}

    /**
     * The default thread factory
     */
    static class SelectProviderThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        SelectProviderThreadFactory(String threadname) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    threadname +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
