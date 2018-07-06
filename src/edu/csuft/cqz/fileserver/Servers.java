package edu.csuft.cqz.fileserver;

/**
 * 开启多个服务器来监听多个端口
 */
public class Servers implements Runnable {

    /**
     * 端口号
     */
    private int port;

    public Servers(int p){
        this.port=p;
    }


    @Override
    public void run() {
        new Server(port).start();
    }
}
