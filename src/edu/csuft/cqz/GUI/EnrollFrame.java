package edu.csuft.cqz.GUI;

import edu.csuft.cqz.fileclient.Client;
import edu.csuft.cqz.fileserver.Mysql;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EnrollFrame extends JFrame {
    private JLabel jLabel,jLabel2,jLabel3;
    private JTextField jTextField;
    private JPasswordField jPasswordField,jPasswordField2;
    private JButton jButton,jButton2;
    private Client client;

    public EnrollFrame( ){
    	this.client=new Client();
        this.setLayout(new FlowLayout());
        init();
        Dimension dimension=getToolkit().getScreenSize();
        this.setBounds(dimension.width/2-200,dimension.height/2-100,400,200);
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        this.setVisible(true);
        initButton();
    }


    /**
     * 注册界面按钮的功能
     */
    private void initButton() {

        /**
         * 注册功能
         */
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUser();
            }
        });

        jTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyChar()==KeyEvent.VK_ENTER)
                    createUser();
            }
        });

        jPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyChar()==KeyEvent.VK_ENTER)
                    createUser();
            }
        });

        jPasswordField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyChar()==KeyEvent.VK_ENTER)
                    createUser();
            }
        });



        /**
         * 取消功能
         */
        jButton2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO 自动生成的方法存根
				EnrollFrame.this.dispose();
				new MyFrame(client);
			}
		});
    }


    /**
     * 组册界面组件的初始化
     */
    private void init() {
        JPanel jPanel=new JPanel();
        jLabel=new JLabel("输入账户");
        jTextField=new JTextField(20);
        jPanel.add(jLabel);
        jPanel.add(jTextField);
        this.getContentPane().add(jPanel);

        JPanel jPanel1=new JPanel();
        jLabel2=new JLabel("输入密码");
        jPasswordField=new JPasswordField(20);
        jPanel1.add(jLabel2);
        jPanel1.add(jPasswordField);
        this.getContentPane().add(jPanel1);

        JPanel jPanel2=new JPanel();
        jLabel3=new JLabel("确认密码");
        jPasswordField2=new JPasswordField(20);
        jPanel2.add(jLabel3);
        jPanel2.add(jPasswordField2);
        this.getContentPane().add(jPanel2);

        jButton=new JButton("确认注册");
        this.getContentPane().add(jButton);
        jButton2=new JButton("取消");
        this.getContentPane().add(jButton2);

    }


    /**
     * 判断账户的格式
     * @param username
     * @return
     */
    private static boolean checkUser(String username)
    {
        //String regex="[a-zA-Z][0-9a-zA-Z_]{5,9}";
        //String regex="[a-zA-Z][\\da-zA-Z_]{5,9}";// \d  要转成 \\d
        String regex="\\w{1,15}";
        return username.matches(regex);
    }

    /**
     * 判断密码的格式
     * @param username
     * @return
     */
    private static boolean checkPassword(String username)
    {
        //String regex="[a-zA-Z][0-9a-zA-Z_]{5,9}";
        //String regex="[a-zA-Z][\\da-zA-Z_]{5,9}";// \d  要转成 \\d
        String regex="\\w{5,15}";
        String regex1="[0-9]{5,15}";
        if (username.matches(regex1))
            return false;
        if(username.matches(regex))
            return true;
        return false;

    }

    /**
     * 创建用户函数
     */
    private void createUser(){
        if(jTextField.getText().equals("")) {

            JOptionPane.showMessageDialog(EnrollFrame.this, "账户不能为空");
            return;
        }
        if(jPasswordField.getPassword().length<=0||jPasswordField2.getPassword().length<=0) {
            JOptionPane.showMessageDialog(EnrollFrame.this, "密码不能为空");
            return;
        }
        String pass1=new String(jPasswordField.getPassword());
        String pass2=new String(jPasswordField2.getPassword());

        if(!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(EnrollFrame.this, "两次密码不相同");
            return;
        }else {
            if(!checkUser(jTextField.getText())){
                JOptionPane.showMessageDialog(EnrollFrame.this,
                        "只能以字母、数字以及下划线为账户名");
                return;
            }
            if (!checkPassword(pass1)){
                JOptionPane.showMessageDialog(EnrollFrame.this,
                        "只能以长度大于5的字母、数字为密码");
                return;
            }else {
                if(client.createUser(jTextField.getText(),pass1)){
                    JOptionPane.showMessageDialog(EnrollFrame.this,
                            "注册成功");
                    EnrollFrame.this.dispose();
                    new MyFrame(client);
                }
                else {
                    JOptionPane.showMessageDialog(EnrollFrame.this, "注册失败,用户存在");
                    return;
                }

            }
        }


    }
}
