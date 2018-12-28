package com.scoket.wz1.lib.Server1.handle;

import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.Connector;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2018-12-21.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Serverp.handle
 */
public class ClientHandler {

    private final CloseNotify closeNotify;
    private final ClientWriteHandle writeHandle;
    private final SocketChannel socket;
    private final Connector connector;

    public String getClientInfo() {
        return clientInfo;
    }

    private final String clientInfo;

    public ClientHandler(SocketChannel socket, final CloseNotify closeNotify) throws IOException {

        //拿到 socketChannel 客户端   上级服务端是 serversocketChannel通道
        this.socket=socket;
        //socket非阻塞通道

         connector = new Connector() {
            @Override
            public boolean receiveNewMessage(String s) {
                closeNotify.onNewMessageArrived(ClientHandler.this,s);
                return super.receiveNewMessage(s);
            }

            @Override
            public void connectorClose(SocketChannel channel) {
                super.connectorClose(channel);
                exitbySelf();
            }
        };
        connector.setup(socket);

        //构建selector 设置状态
        Selector write = Selector.open();
        socket.register(write, SelectionKey.OP_WRITE);

        //socket写流程
        writeHandle = new ClientWriteHandle(write);
        //反馈线程
        this.closeNotify = closeNotify;

        //新用户信息
//        this.clientInfo="A[" + socket.getInetAddress().getHostAddress()
//                + "] P[" + socket.getPort() + "]";

         this.clientInfo=socket.getRemoteAddress().toString();
    }

    public void exit(){
        CloseUtils.close(connector);
        //线程退出
        writeHandle.exit();
        //socket退出
        if (socket!=null) {
            CloseUtils.close(socket);
        }
    }

    public void send(String str){
        writeHandle.send(str);
    }


    public void exitbySelf(){
        exit();
        closeNotify.onSelfClosed(this);
    }

    class ClientReadHandler extends Thread{

        private final Selector readSelector;
        private final ByteBuffer byteBuffer;
        private boolean done =false;

        public ClientReadHandler(Selector readSelector)  {
            this.readSelector = readSelector;
            this.byteBuffer= ByteBuffer.allocate(256);
        }

        @Override
        public void run() {
            super.run();
            try {
                //得到输入流  客户端拿到一条数据 接收数据
                //接收一行数据
                do {
                        //判断等于0 selector
                    if (readSelector.select()==0)
                    {
                        if (done) {
                            break;
                        }
                        continue;
                    }
                    //得到迭代器key
                    Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
                    while (iterator.hasNext()){

                        SelectionKey next = iterator.next();
                        iterator.remove();

                        if (next.isReadable())
                        {
                            SocketChannel channel = (SocketChannel) next.channel();
                            int read = channel.read(byteBuffer);
                            if (read>0)
                            {
                                String str = new String(byteBuffer.array(), 0, read - 1);
                                closeNotify.onNewMessageArrived(ClientHandler.this,str);
                            }else if (read<0){
                                    System.out.println("客户端已无法读取数据！");
                                    ClientHandler.this.exitbySelf();
                                    break;
                            }
                        }
                    }
                    //得到迭代器状态
                    //得到channel
                    //channel.read(byteBuffer)

                    //大于0  转换为string 小于0  发生异常

                } while (!done);

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    System.out.println("客户端" + socket.getRemoteAddress() + "出现问题..");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (!done)
                {
                    ClientHandler.this.exitbySelf();
                }
            }finally {
                CloseUtils.close(readSelector);
            }


        }

        void exit(){
            done=true;
        }
    }

    class ClientWriteHandle {
        private final ExecutorService executorService;
        private final Selector writeSelector;
        private final ByteBuffer byteBuffer;
        private boolean isdone =false;
        //单线程池
        //打印流 发送str


        public ClientWriteHandle(Selector outputStream) {
            this.writeSelector =outputStream;
            executorService = Executors.newSingleThreadExecutor();
            this.byteBuffer = ByteBuffer.allocate(256);
        }

        void exit(){
           CloseUtils.close(writeSelector);
            executorService.shutdown();
        }

        void send(String msg){
            WriteRunable writeRunable = new WriteRunable(msg,writeSelector);
            executorService.execute(writeRunable);
        }

        class WriteRunable implements Runnable {

            private final String msg;
            private final Selector writeSelector;

            public WriteRunable(String msg, Selector writeSelector) {
                this.msg =msg;
                this.writeSelector=writeSelector;
            }

            @Override
            public void run() {
                if (ClientWriteHandle.this.isdone)
                {
                    return;
                }

                try {
                //构建bytebuffer string 专buffer
                byteBuffer.clear();
                byteBuffer.put(msg.getBytes());

                byteBuffer.flip();

                while (!isdone&&byteBuffer.hasRemaining())
                {
                    int len = socket.write(byteBuffer);
                    if (len<0)
                    {
                        System.out.println("客户端无法发送数据");
                        ClientHandler.this.exitbySelf();
                        break;
                    }
                }

                //返回小于0  异常

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
