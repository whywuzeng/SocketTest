package com.scoket.wz1.lib.clink.net.qiujuer.clink.core;

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
    public static IoContext get() {
        return INSTANCE;
    }

    public  void setIoProvider(IoProvider mIoProvider) {
        this.mIoProvider = mIoProvider;
    }
    //静态类 相当于构建者模式

    static class Build {
        private IoProvider mIoProvider;

        public Build setIoProvider(IoProvider mIoProvider) {
            this.mIoProvider = mIoProvider;
            return this;
        }

        public IoContext build(){
            IoContext ioContext = new IoContext();
            ioContext.setIoProvider(mIoProvider);
            return ioContext;
        }
    }

}
