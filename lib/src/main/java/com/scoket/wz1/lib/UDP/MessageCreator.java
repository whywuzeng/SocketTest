package com.scoket.wz1.lib.UDP;

/**
 * Created by Administrator on 2018-12-19.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.UDP
 */
public class MessageCreator {
    public static final String SN_HEADER="收到暗号,我是(SN):";
    public static final String PORT_HEADER="接收到暗号,请回电端口号:";

    public static String buildSN_header(String sn){
        return SN_HEADER+sn;
    }

    public static String buildPort_Header(int port){
        return PORT_HEADER+port;
    }

    //得到那个 端口号 从暗号
    public static int parsePort(String data)
    {
        if (data.startsWith(PORT_HEADER))
        {
            Integer integer = Integer.valueOf("30000");
//            String substring = data.substring(PORT_HEADER.length());
            return integer;
        }

        return -1;
    }

    //得到那个S/N编码
    public static String parseSN(String data)
    {
        if (data.startsWith(SN_HEADER))
        {
            return String.valueOf(data.substring(SN_HEADER.length()));
        }
        return null;
    }
}
