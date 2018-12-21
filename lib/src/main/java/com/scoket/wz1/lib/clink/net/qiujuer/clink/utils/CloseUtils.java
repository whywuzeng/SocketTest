package com.scoket.wz1.lib.clink.net.qiujuer.clink.utils;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {
    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}