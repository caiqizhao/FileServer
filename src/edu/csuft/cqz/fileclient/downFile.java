package edu.csuft.cqz.fileclient;

import edu.csuft.cqz.GUI.FileFrame;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class downFile implements Runnable {

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
     * 核心类
     */
    InetAddress address2;

    /**
     * 端口号
     */
    int port = 9001;

    File file;

    String filenmae;

    String user_name;

    FileFrame jFrame;

    public downFile(File file,String filenmae,String user_name,FileFrame jFrame){
        this.file=file;
        this.filenmae=filenmae;
        this.user_name=user_name;
        this.jFrame=jFrame;
    }


    @Override
    public void run() {

        try {
            socket = new Socket(address, port);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            outputStream.write('7');
            outputStream.flush();

            if(inputStream.read()=='0') {
                outputStream.write(filenmae.getBytes());
                outputStream.flush();
            }

            if (inputStream.read()=='0') {
                outputStream.write(user_name.getBytes());
                outputStream.flush();
            }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 4];

            //读取文件
            int size;
            if(inputStream.read()=='0') {
                while ((size = inputStream.read(buf)) != -1) {
                    //使用从套接字获得的输出流【发送】数据
                    baos.write(buf, 0, size);
                }


                byte data[] = baos.toByteArray();

                FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath() + "//" + filenmae);
                fileOutputStream.write(data);
                fileOutputStream.close();

                JOptionPane.showMessageDialog(jFrame,filenmae+"下载成功");
            }else {
                JOptionPane.showMessageDialog(jFrame,filenmae+"下载失败");
            }
            synchronized ((Object) FileFrame.count) {
                FileFrame.count--;
                jFrame.setjLabel();
            }
        }catch (IOException e){

        }
    }
}
