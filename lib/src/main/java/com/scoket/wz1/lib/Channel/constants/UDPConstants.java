package com.scoket.wz1.lib.Channel.constants;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel.constants
 */
public class UDPConstants {
    public static byte[] HEADER = new byte[]{7,7,7,7,7,7,7,7};

    //固化UDP服务器端口:30201;
    public static int PORT_SERVER=30211;

    //客户端回送端口
    public static int PORT_CLIENT_RESPONSE=30202;

}
