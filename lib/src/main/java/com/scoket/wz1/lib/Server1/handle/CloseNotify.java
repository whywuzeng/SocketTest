package com.scoket.wz1.lib.Server1.handle;

/**
 * Created by Administrator on 2018-12-21.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.Serverp.handle
 */
public interface CloseNotify {

    void onSelfClosed(ClientHandler handler);

    void onNewMessageArrived(ClientHandler handler,String msg);
}
