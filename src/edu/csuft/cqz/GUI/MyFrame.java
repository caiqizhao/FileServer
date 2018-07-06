package edu.csuft.cqz.GUI;

import edu.csuft.cqz.fileclient.Client;
import edu.csuft.cqz.fileserver.Mysql;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class MyFrame extends JFrame {
    private JLabel jLabel,jLabel2,jLabel3;
    private JTextField jTextField,jTextField2;
    private JPasswordField jPasswordField;
    private JButton jButton,jButton2;
    private Client client;

    public MyFrame(){
        this.setLayout(new FlowLayout());
        init();
        Dimension dimension=getToolkit().getScreenSize();
        this.setBounds(dimension.width/2-150,dimension.height/2-125,300,250);
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        this.setVisible(true);
        initButton();
    }

//    public MyFrame(Client client){
//        this.client=client;
//        this.setLayout(new FlowLayout());
//        init();
//        Dimension dimension=getToolkit().getScreenSize();
//        this.setBounds(dimension.width/2-150,dimension.height/2-125,300,250);
//        this.setDefaultCloseOperation(3);
//        this.setResizable(false);
//        this.setVisible(true);
//        initButton();
//    }


    /**
     * 添加按钮功能
     */
    private void initButton() {

        /**
         * 登陆功能
         */
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	MyFrame.this.goFile();
            }
        });




        jTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyChar()==KeyEvent.VK_ENTER){
                    MyFrame.this.goFile();
                }
            }
        });


        jPasswordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO 自动生成的方法存根
				if(arg0.getKeyChar()==KeyEvent.VK_ENTER) {
		            MyFrame.this.goFile();
				}
			}
		});


        jTextField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyChar()==KeyEvent.VK_ENTER) {
                    MyFrame.this.goFile();
                }
            }
        });

        /**
         * 注册功能
         */
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MyFrame.this.dispose();
                new EnrollFrame();
            }
        });

    }

    /**
     * 初始化登陆界面
     */
    private void init() {
        URL url=getClass().getResource("../../../../lib/welcome.png");
        Icon icon=new ImageIcon(url);
        JLabel jLabel_icon=new JLabel(icon);
        
        this.getContentPane().add(jLabel_icon);

        JPanel jPanel=new JPanel();
        jLabel=new JLabel("账户");
        jTextField=new JTextField(20);
        jPanel.add(jLabel);
        jPanel.add(jTextField);
        this.getContentPane().add(jPanel);

        JPanel jPanel1=new JPanel();
        jLabel2=new JLabel("密码");
        jPasswordField=new JPasswordField(20);
        jPanel1.add(jLabel2);
        jPanel1.add(jPasswordField);
        this.getContentPane().add(jPanel1);

        JPanel jPanel3=new JPanel();
        jLabel3=new JLabel("选择端口 9001~9050");
        jPanel3.add(jLabel3);
        jTextField2=new JTextField(4);
        jPanel3.add(jTextField2);
        this.getContentPane().add(jPanel3);

        JPanel jPanel2=new JPanel();
        jButton=new JButton("登陆");
        jButton2=new JButton("注册");
        jPanel2.add(jButton);
        jPanel2.add(jButton2);
        this.getContentPane().add(jPanel2);

    }


    /**
     * 确定连接端口
     */
    private void goFile(){
        if(jTextField2.getText().equals("")) {
            JOptionPane.showMessageDialog(MyFrame.this,"端口为空自动使用9000端口");
            client = new Client();
        }
        else
            try {
                client=new Client(Integer.parseInt(jTextField2.getText()));
            }catch (Exception e){
                client=new Client();
                JOptionPane.showMessageDialog(MyFrame.this,"端口有误自动使用9000端口");

            }
        String user_name=jTextField.getText();
        String password=new String(jPasswordField.getPassword());
        if(user_name.equals("")||password.equals(""))
            JOptionPane.showMessageDialog(MyFrame.this,"账户密码不能为空");
        else {
            if (client.land(user_name, password)) {
                JOptionPane.showMessageDialog(MyFrame.this, "登陆成功");
                MyFrame.this.dispose();
                new FileFrame(jTextField.getText(), client);
            } else
                JOptionPane.showMessageDialog(MyFrame.this, "登陆失败");
        }

    }


}
