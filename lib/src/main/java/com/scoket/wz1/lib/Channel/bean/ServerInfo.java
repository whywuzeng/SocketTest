package com.scoket.wz1.lib.Channel.bean;

/**
 * Created by Administrator on 2018-12-20.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Channel.bean
 */
public class ServerInfo {

    //sn
    //port
    //address

    public int port;

    public ServerInfo(int port, String address, String sn) {
        this.port = port;
        this.address = address;
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "port=" + port +
                ", address='" + address + '\'' +
                ", sn='" + sn + '\'' +
                '}';
    }

    public String address;
    public String sn;
}
