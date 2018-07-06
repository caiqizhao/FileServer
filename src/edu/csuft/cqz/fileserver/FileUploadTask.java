package edu.csuft.cqz.fileserver;

import java.io.*;
import java.net.Socket;



/**
 * 定义服务端的具体操作，交给线程池执行的任务
 */
public class FileUploadTask implements Runnable {
    /**
     * 服务器与客户端通信的连接
     */
    Socket socket;

    /**
     * 数据库
     */
    Mysql mysql;

    /**
     * 接收套接字和数据库
     * @param socket
     * @param mysql
     */
    public FileUploadTask(Socket socket, Mysql mysql) {
        this.socket=socket;
        this.mysql=mysql;
    }

    /**
     * 用户登陆请求
     * @param inputStream
     * @param outputStream
     */
    private void isUser(InputStream inputStream,OutputStream outputStream){
        try {
            outputStream.write('0');
            outputStream.flush();

            //获取用户名
            byte[] bytes = new byte[1024];
            int size = inputStream.read(bytes);
            String name = new String(bytes, 0, size);

            outputStream.write('0');
            outputStream.flush();

            //获取密码
            size = inputStream.read(bytes);
            String password = new String(bytes, 0, size);

            //查询用户
            if (mysql.isUser(name.trim(), password.trim())) {
                outputStream.write('1');
                outputStream.flush();

            } else {
                outputStream.write('0');
                outputStream.flush();
            }
            socket.shutdownOutput();
            mysql.closeMysql();
        }catch (IOException e){

        }
    }

    /**
     * 初始化用户界面
     * @param inputStream
     * @param outputStream
     */
    private void initUserGUI(InputStream inputStream,OutputStream outputStream){
        try{
            outputStream.write('0');
            outputStream.flush();

            //获取用户名
            byte[] bytes = new byte[1024];
            inputStream.read(bytes);
            String name1 = new String(bytes);

            //获取初始化用户文件
            String string = mysql.selectuserfile(name1.trim());

            outputStream.write('0');
            outputStream.flush();

            if(string!=null){
                outputStream.write(string.getBytes());
                outputStream.flush();
            }
            socket.shutdownOutput();
            mysql.closeMysql();
        }catch (IOException e){}
    }


    /**
     * 删除文件
     * @param inputStream
     * @param outputStream
     */
    private void deleteFile(InputStream inputStream,OutputStream outputStream){
        try {
            outputStream.write('0');
            outputStream.flush();


            byte[] bytes = new byte[1024 * 4];
            int size;

            //接收用户名
            size = inputStream.read(bytes);
            String user_name = new String(bytes, 0, size);

            //接收文件名
            size = inputStream.read(bytes);
            String file_name = new String(bytes, 0, size);


            boolean aboolean = mysql.deletefile(user_name, file_name);
            outputStream.write(String.valueOf(aboolean).getBytes());
            socket.shutdownOutput();
            mysql.closeMysql();
        }catch (IOException e){}
    }



    /**
     * 上传文件
     * @param inputStream
     * @param outputStream
     */
    private void insertFile(InputStream inputStream,OutputStream outputStream){
        try {
            outputStream.write('0');
            outputStream.flush();

            byte bytes[] = new byte[1024 * 8];
            int size;


            //开始接收哈希值
            size = inputStream.read(bytes);
            String file_hash = new String(bytes, 0, size);

            if (mysql.selectHash(file_hash)) {
                outputStream.write('0');
                outputStream.flush();
                mysql.updatehash(file_hash);
            } else {
                outputStream.write('1');
                outputStream.flush();


                //开始接收文件
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((size = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, size);
                }
                byte data[] = baos.toByteArray();
                try (FileOutputStream out = new FileOutputStream(new File("/Users/caiqizhao/CQZ", file_hash))) {
                    out.write(data);
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //插入新的哈希记录
                mysql.insertUserhash(file_hash);
            }
            mysql.closeMysql();
        }catch (IOException e){}
    }

    /**
     * 在用户文件表中插入新记录
     * @param inputStream
     * @param outputStream
     */
    private void insertUserFile(InputStream inputStream,OutputStream outputStream){
        try {
            outputStream.write('0');
            outputStream.flush();

            byte[] bytes = new byte[1024 * 4];
            int size;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((size = inputStream.read(bytes)) != -1) {
                baos.write(bytes, 0, size);
            }
            String s = new String(baos.toByteArray());
            String s1[] = s.split("\\+");
            mysql.insertUserfile(s1[0], s1[1], s1[2], s1[3], s1[4]);
            mysql.closeMysql();
        }catch (IOException e){}
    }

    /**
     * 退出用户
     * @param inputStream
     * @param outputStream
     */
    private void deleteUser(InputStream inputStream,OutputStream outputStream){
        try {
            outputStream.write('0');
            outputStream.flush();

            byte[] bytes=new byte[1024*4];
            int size=inputStream.read(bytes);
            String user_name=new String(bytes,0,size);
            mysql.updateuser(user_name);
            mysql.closeMysql();
        }catch (IOException e){}
    }

    /**
     * 下载文件
     * @param inputStream
     * @param outputStream
     */
    private void downFile(InputStream inputStream,OutputStream outputStream){
        try {
            byte[] bytes=new byte[1024*4];
            int size;

            outputStream.write('0');
            outputStream.flush();
            size=inputStream.read(bytes);
            String file_name=new String(bytes,0,size);


            outputStream.write('0');
            outputStream.flush();
            size=inputStream.read(bytes);
            String user_name=new String(bytes,0,size);

            String file_hash=mysql.selectfile(file_name,user_name);
            if(file_hash!=null) {
                outputStream.write('0');
                outputStream.flush();
                FileInputStream fileInputStream = new FileInputStream("/Users/caiqizhao/CQZ/" + file_hash);
                while ((size = fileInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, size);
                }
                outputStream.flush();
                socket.shutdownOutput();
            }else {
                outputStream.write('1');
                outputStream.flush();
            }

            mysql.closeMysql();
        }catch (IOException e){

        }
    }

    /**
     * 注册用户
     * @param inputStream
     * @param outputStream
     */
    private void insertUser(InputStream inputStream,OutputStream outputStream){
        try{
            outputStream.write('0');
            outputStream.flush();

            byte[] bytes=new byte[1024*4];
            int size;

            size=inputStream.read(bytes);
            String user_name=new String(bytes,0,size);

            if(mysql.selectuser(user_name)){
                outputStream.write('1');
                outputStream.flush();
            }else {
                outputStream.write('0');
                outputStream.flush();

                size=inputStream.read(bytes);
                String password=new String(bytes,0,size);

                mysql.createUser(user_name,password);
            }
            mysql.closeMysql();
        }catch (IOException e){

        }
    }


    @Override
    public void run() {

        InputStream inputStream=null;
        OutputStream outputStream=null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            switch (inputStream.read()) {
                case '1':
                    isUser(inputStream,outputStream);
                    break;
                case '2':
                    initUserGUI(inputStream,outputStream);
                    break;
                case '3':
                    deleteFile(inputStream,outputStream);
                    break;
                case '4':
                    insertFile(inputStream,outputStream);
                    break;
                case '5':
                    insertUserFile(inputStream,outputStream);
                    break;
                case '6':
                    deleteUser(inputStream,outputStream);
                    break;
                case '7':
                    downFile(inputStream,outputStream);
                    break;
                case '8':
                    insertUser(inputStream,outputStream);
                    break;


            }

        }catch(IOException e){

        }

    }
}
