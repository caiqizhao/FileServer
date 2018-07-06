package edu.csuft.cqz.fileserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网盘服务器启动器
 */
public class ServerApp {
    public static void main(String[] args){
        Server server=new Server();

        ExecutorService pool;
        pool=Executors.newCachedThreadPool();

        for(int i=9001;i<=9050;i++)
            pool.execute(new Servers(i));
        server.start();
    }
}
