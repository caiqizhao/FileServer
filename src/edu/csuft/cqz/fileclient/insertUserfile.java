package edu.csuft.cqz.fileclient;

import edu.csuft.cqz.GUI.FileFrame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class insertUserfile implements Runnable {

    /**
     * 套接字；封装了网络通信中的底层细节
     * 输出流：发送数据
     * 输入流：接受数据
     */
    Socket socket;

    /**
     * 服务器地址
     */
    String address = "127.0.0.1";


    /**
     * 端口号
     */
    int port = 9002;

    File file;

    String user_name;
    String hash;

    public insertUserfile(String path,String user_name,String hash){
        file=new File(path);
        this.user_name=user_name;
        this.hash=hash;
    }



    @Override
    public void run() {
        try{
            socket=new Socket(address,port);
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream=socket.getOutputStream();

            outputStream.write('5');
            outputStream.flush();

            if (inputStream.read()=='0'){
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                String s=file.getName()+"+"+this.user_name+"+"+simpleDateFormat.format(new Date())+"+"+
                        FileFrame.FormetFileSize(file.length())+"+"+this.hash;
                outputStream.write(s.getBytes());
                outputStream.flush();
                socket.shutdownOutput();
            }

        }catch (IOException e){
            try {
                if (socket!=null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
}
