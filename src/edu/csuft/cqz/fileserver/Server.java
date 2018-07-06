package edu.csuft.cqz.fileserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网盘服务器
 */

public class Server {
    /**
     * 服务器套接字
     */
    ServerSocket serverSocket;

    /**
     * 服务器端口号
     */
    int port=9000;

    /**
     * 封装好的线程池
     */
    ExecutorService pool;



    /**
     * 可以选择端口
     * @param port
     */
    public  Server(int port){
        this.port=port;
    }

    /**
     * 默认端口
     */
    public Server(){ }


    /**
     * 启动服务器
     */
    public void start() {
        //线程池
        pool=Executors.newFixedThreadPool(9);
        pool=Executors.newSingleThreadExecutor();
        pool=Executors.newCachedThreadPool();

        try {
            serverSocket=new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();

                //接收数据
                pool.execute(new FileUploadTask(socket, new Mysql()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
