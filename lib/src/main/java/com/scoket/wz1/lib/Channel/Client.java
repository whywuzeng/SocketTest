package com.scoket.wz1.lib.Channel;

import com.scoket.wz1.lib.Channel.bean.ServerInfo;

import java.io.IOException;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel
 */
public class Client {

    public static void main(String[] args){

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
            TCPClient.linkwith(serverInfo);
        }else {
            System.out.println("没搜索到服务设备");
        }

    }
}
