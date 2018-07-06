package edu.csuft.cqz.GUI;

import edu.csuft.cqz.fileclient.Client;
import edu.csuft.cqz.fileserver.Mysql;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;

public class FileFrame extends JFrame{
    public static int count=0;

    private JLabel jLabel;
	private JTable jTable;
	private DefaultTableModel defaultTableModel;
	private JButton button,button2,button3;
	private static String str[]= {"文件名","上传日期","文件大小"};
	private String user_name;
	private Client client;
	
	public FileFrame(String name,Client client) {
		this.user_name=name;
		this.client=client;
		Dimension dim = getToolkit().getScreenSize();
		this.setBounds(dim.width/4, dim.height/4, 
						dim.width/2, dim.height/ 2);
		this.setLayout(new BorderLayout());
		initFrame();
		initFile();
        this.setVisible(true);
        initButton();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(count==0) {
                    client.exitUser(user_name);
                    System.exit(0);
                }else {
                    int i=JOptionPane.showConfirmDialog(FileFrame.this,
                            "当前下载数目不为0，强制退出可能影响文件完整性.是否强制退出");
                    if(i==0) {
                        client.exitUser(user_name);
                        System.exit(0);
                    }else{
                        new FileFrame(user_name,client);
                        FileFrame.this.dispose();
                    }
                }
            }
        });
	}

    /**
     * 文件大小转换器
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 按钮的初始化
     */
    public void initButton() {
        /**
         * 删除按钮
         */
	    button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i[]=jTable.getSelectedRows();
                if(i.length>0){
                    for(int n=0;n<i.length;n++) {
                        if (client.deleteFile(user_name, (String) defaultTableModel.getValueAt(i[n], 0))) {
                            defaultTableModel.removeRow(i[n]);
                        } else
                            JOptionPane.showMessageDialog(FileFrame.this,
                                    defaultTableModel.getValueAt(i[n], 0) + "删除失败");
                    }
                }else
                    JOptionPane.showMessageDialog(FileFrame.this,"未选中!!!");
            }
        });

        /**
         * 上传按钮
         */
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser=new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.showOpenDialog(null);
                File file=jFileChooser.getSelectedFile();
                if(file!=null&&file.isFile()) {
                    for (int i = defaultTableModel.getRowCount(); i > 0; i--) {
                        String filename=(String)defaultTableModel.getValueAt(i-1,0);
                        if(file.getName().equals(filename)) {
                            JOptionPane.showMessageDialog(FileFrame.this, "文件存在");

                            return;
                        }
                    }
                   if (client.start(file.getAbsolutePath(),user_name)){
                        JOptionPane.showMessageDialog(FileFrame.this,"上传成功");
                       SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                            String s[]={file.getName(),
                                    simpleDateFormat.format(new Date()),FormetFileSize(file.length())};
                            defaultTableModel.addRow(s);
                   }else {
                       JOptionPane.showMessageDialog(FileFrame.this, "上传失败");
                   }

                }
            }
        });
        /**
         * 下载按钮
         */
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.showOpenDialog(null);
                File file = jFileChooser.getSelectedFile();
                if (file!=null) {
                    int i[] = jTable.getSelectedRows();
                    if (i.length > 0) {
                        count = i.length;
                        jLabel.setText("当前下载数目:" + count);
                        for (int n = 0; n < i.length; n++) {
                            client.downFile(file, (String) defaultTableModel.getValueAt(i[n], 0), user_name, FileFrame.this);
                        }

                    } else
                        JOptionPane.showMessageDialog(FileFrame.this, "未选中下载!!!");

                }
            }
        });
    }


    /**
     * 初始化用户文件
     */
    private void initFile() {
		// TODO 自动生成的方法存根
		
		String str[][]=null;
		str=client.initUserGUI(user_name);

		
		if(str!=null) {
			for(int i=0;i<str.length;i++) {

				defaultTableModel.addRow(str[i]);
			}
		}else
			return ;
		
	}


    /**
     * 初始化用户界面
     */
	public void initFrame() {
		// TODO 自动生成的方法存根
		
		defaultTableModel=new DefaultTableModel(str, 0);
		jTable=new JTable(defaultTableModel){
            public boolean isCellEditable(int row, int column) {
                return false;//表格不允许被编辑
            }
    }; ;
		jTable.setShowGrid(true);
		this.add(new JScrollPane(jTable));


		JPanel jPanel=new JPanel();
		jLabel=new JLabel("当前下载数目:"+count);
		button=new JButton("上传");
		button2=new JButton("下载");
		button3=new JButton("删除");
		jPanel.add(jLabel);
		jPanel.add(button);
		jPanel.add(button2);
		jPanel.add(button3);
		this.add(jPanel,BorderLayout.SOUTH);
		
	}


    /**
     * 显示下载数目
     */
    public void setjLabel() {
        this.jLabel.setText("当前下载数目为:"+count);
    }


}
