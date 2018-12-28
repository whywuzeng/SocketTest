package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

import java.io.IOException;

/**
 * Created by Administrator on 2018-12-26.
 * <p>
 * by author wz
 * <p>
 * com.scoket.wz1.lib.clink.net.qiujuer.clink.core
 */
public class IoContext {

    private IoContext(){}

    //IOprovide 实例
    private static IoContext INSTANCE;

    private IoProvider mIoProvider;

    //本身静态成员变量 返回
    public static IoContext getInstance() {
        return INSTANCE;
    }

    public void close() throws IOException {
        if (mIoProvider!=null) {
            mIoProvider.close();
        }
    }

    private   void setIoProvider(IoProvider mIoProvider) {
        this.mIoProvider = mIoProvider;
    }

    public IoProvider getIoProvider(){
        return mIoProvider;
    }
    //静态类 相当于构建者模式

   public static class Build {
        private IoProvider mIoProvider;

        public Build setIoProvider(IoProvider mIoProvider) {
            this.mIoProvider = mIoProvider;
            return this;
        }

        public IoContext build(){
            INSTANCE = new IoContext();
            INSTANCE.setIoProvider(mIoProvider);
            return INSTANCE;
        }
    }

}
