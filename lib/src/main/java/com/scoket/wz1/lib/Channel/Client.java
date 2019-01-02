package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.Impl.IoSelectProvider;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.core.IoContext;
import com.scoket.wz1.lib.clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz com.scoket.wz1.lib.Channel.Client
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class Client {

    public static void main(String[] args) throws IOException {

        //初始化 连接器。只需要一个 IOprovide
        //关闭
        IoContext ioContext = new IoContext.Build().setIoProvider(new IoSelectProvider()).build();

        ServerInfo serverInfo=null;
        //初始化搜索
        try {
            //得到serverinfo 信息，
             serverInfo = UDPSearcher.searchServer(10000);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        //TCP 连接serverinfo
        if (serverInfo!=null) {
            TCPClient link=null;
            try {
                link= TCPClient.linkwith(serverInfo);
                TCPClient.write(link);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("客户端socket连接出现异常");
            }finally {
                if (link!=null)
                {
                    CloseUtils.close(link);
                }
            }

        }else {
            System.out.println("没搜索到服务设备");
        }

        ioContext.close();
    }
}
