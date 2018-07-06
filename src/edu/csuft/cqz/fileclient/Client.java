package edu.csuft.cqz.fileclient;

import edu.csuft.cqz.GUI.FileFrame;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * 网盘客户端
 *
 * */

public class Client {
    /**
     * 套接字；封装了网络通信中的底层细节
     * 输出流：发送数据
     * 输入流：接受数据
     */
    private Socket socket;

    /**
     * 服务器地址
     */
    private String address = "127.0.0.1";



    /**
     * 端口号
     */
    private int port = 9000;



    /**
     * 选择端口初始化
     * @param port
     */
    public Client(int port){
        this.port=port;
    }



    /**
     * 使用默认端口
     */
    public Client(){

    }


    /**
     * 登陆客户端
     */
    public boolean land(String name, String password) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            socket = new Socket(address, port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            outputStream.write('1');
            outputStream.flush();
            if (inputStream.read() == '0') {

                outputStream.write(name.getBytes());
                outputStream.flush();

            }
            if (inputStream.read() == '0') {
                outputStream.write(password.getBytes());
                outputStream.flush();
            }
            if (inputStream.read() == '1') {
                return true;
            } else
                return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    socket = null;
                }
            }
        }
    }


    /**
     * 初始化用户界面
     * @param name
     * @return
     */
    public String[][] initUserGUI(String name) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket(address, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            outputStream.write('2');
            outputStream.flush();
            if (inputStream.read() == '0') {
                outputStream.write(name.getBytes());
                outputStream.flush();
            }
            String string2[][] = null;
            if (inputStream.read() == '0') {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int size;
                String str = "";
                byte b[] = new byte[1024 * 4];
                while ((size = inputStream.read(b)) != -1)
                    baos.write(b, 0, size);

                str = new String(baos.toByteArray());
                if(str.equals(""))
                    return null;
                String string[] = str.split("\\;");
                ArrayList<String[]> arrayList = new ArrayList<>();
                for (int i = 0; i < string.length; i++) {
                    String string1[] = string[i].split("\\+");
                    arrayList.add(string1);
                }
                string2 = new String[arrayList.size()][3];
                arrayList.toArray(string2);

            }
            return string2;

        } catch (IOException e) {
            return null;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    socket = null;
                }
            }
        }
    }


    /**
     * 删除文件
     * @param name
     * @param filename
     * @return
     */
    public boolean deleteFile(String name, String filename) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket(address, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            outputStream.write('3');
            if (inputStream.read() == '0') {
                outputStream.write(name.getBytes());
                outputStream.flush();
                outputStream.write(filename.getBytes());
                outputStream.flush();
            }
            int size;
            byte b[] = new byte[1024 * 4];
            size = inputStream.read(b);
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    socket = null;
                }
            }
            return Boolean.parseBoolean(new String(b, 0, size));


        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    socket = null;
                }
            }
            return false;
        }


    }


    /**
     * 上传完文件
     * @param file
     * @param user_name
     * @return
     */
    public boolean start(String file,String user_name) {
        //通信协议：TCP/HTTP
        //-----------------
        // 应用层:HTTP、FTP、POP／SMTP、XMPP、MQTT
        // 传输层:TCP、UDP
        // 网络层:IP
        // 物理层:电气接口的相关规范
        //----------------
        //创建 TCP 套接字
        try {


            //发送文件

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 4];

            //读取文件
            int size;
            while ((size = in.read(buf)) != -1) {
                //使用从套接字获得的输出流【发送】数据
                baos.write(buf, 0, size);

            }

            //生成哈希值

            byte data[] = baos.toByteArray();
            byte hash[] = MessageDigest.getInstance("SHA-256").digest(data);
            String file_hash = new BigInteger(1, hash).toString(16);

            socket = new Socket(address, port);
            System.out.println("客户端建立连接");
            OutputStream out = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            //发出上传请求
            out.write('4');
            out.flush();


            //上传哈希值
            if (inputStream.read() == '0') {
                out.write(file_hash.getBytes());
                out.flush();
            }



            //上传文件
            if (inputStream.read() == '0') {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                    }
                }
                new Thread(new insertUserfile(file,user_name,file_hash)).start();
                return true;
            } else {
                out.write(data);
                out.flush();

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                    }
                }
                new Thread(new insertUserfile(file,user_name,file_hash)).start();
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    socket = null;
                }
            }
            return false;
        } catch (NoSuchAlgorithmException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    socket = null;
                }
            }
            return false;
        }

    }


    /**
     * 退出用户
     * @param user_name
     */
    public void exitUser(String user_name){
        try {
            socket = new Socket(address, port);
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream=socket.getOutputStream();

            outputStream.write('6');
            outputStream.flush();

            if(inputStream.read()=='0') {
                outputStream.write(user_name.getBytes());
                outputStream.flush();
            }

        }catch (IOException e){
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    socket = null;
                }
            }
        }
    }


    /**
     * 下载文件
     * @param file
     * @param filename
     * @param user_name
     */
    public void downFile(File file, String filename,String user_name,FileFrame jFrame) {
        new Thread(new downFile(file,filename,user_name,jFrame)).start();
    }


    /**
     * 注册用户
     * @param user_name
     * @param password
     */
    public boolean createUser(String user_name,String password){
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            socket = new Socket(address, port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();


            outputStream.write('8');
            outputStream.flush();
            //上传用户名
            if(inputStream.read()=='0')
                outputStream.write(user_name.getBytes());

            //上传用户密码
            if (inputStream.read()=='0') {
                outputStream.write(password.getBytes());
                return true;
            }
            else {
                return false;
            }

        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    socket = null;
                }
            }
        }
    }

}
