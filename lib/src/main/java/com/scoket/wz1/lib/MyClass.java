package com.scoket.wz1.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MyClass {

    public static void main(String[] args) throws IOException{

        System.out.println("111111111");
        Socket socket=new Socket();
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(),2000),3000);
        System.out.println("发起服务端连接");
        //打印服务端  和 客户端地址 和 端口
        System.out.println("客户端信息:"+socket.getLocalAddress()+"端口号:"+socket.getLocalPort());
        System.out.println("服务端信息:"+socket.getInetAddress() +"端口号:"+socket.getPort());

        //发送数据
        try {
            todo(socket);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("客户端异常...");
        }
        //释放资源
        socket.close();
        System.out.println("客户端关闭...");
    }

    public static void todo(Socket client)throws IOException{
        //构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //得到socket输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        //得到socket输入流，并转换为bufferedReader
        InputStream inputStream = client.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag =true;

        do {
            //读取键盘输入流
            String s = input.readLine();

            //发送到服务器
            printStream.println(s);

            String s1 = reader.readLine();
            if (s1.equalsIgnoreCase("bye"))
            {
                flag=false;
            }else {
                System.out.println(s1);
            }
        }while (flag);

        printStream.close();
        reader.close();
    }
}
