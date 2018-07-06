package edu.csuft.cqz.fileserver;



import java.sql.*;



public class Mysql {


	private Connection connect;
	private Statement stmt;

	/**
	 * 创建数据库连接数据库
	 */
	public Mysql() {
		try {
		      Class.forName("com.mysql.cj.jdbc.Driver");     //加载MYSQL JDBC驱动程序   
		      
		    }
		    catch (Exception e) {
		      
		      e.printStackTrace();
		    }
		    try {
		       connect = DriverManager.getConnection(
		          "jdbc:mysql://localhost:3306/file_manager"
		          + "?useUnicode=true&characterEncoding=utf-8&useSSL=false","root","caiqizhao");
		           //连接URL为   jdbc:mysql//服务器地址/数据库名  ，后面的2个参数分别是登陆用户名和密码
		       stmt = (Statement) connect.createStatement();
				
		    }catch (Exception e) {
		      System.out.print("get data error!");
		      e.printStackTrace();
		    }
	}

    /**
     * 用户登陆判断
     * @param name
     * @param password
     * @return
     */
	public  boolean isUser(String name,String password) {

		try {
			String s="select * from user where user_name='"+name+"' and user_password='"+password+"'";
			ResultSet rs=stmt.executeQuery(s);
			if(rs.next()){
			    if (rs.getBoolean("isuser")) {
			        s="update user set isuser=0 where user_name='"+name+"'";
			        stmt.executeUpdate(s);
                    return true;
                }
            }

		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return false;
	}


    /**
     * 初始化用户界面
     * @param name
     * @return
     */
	public String selectuserfile(String name) {
		
		try {
			String s="select * from userfile where user_name='"+name+"'";
			ResultSet rs=stmt.executeQuery(s);
            String str="";
			if(rs.next()) {

				do {
				    str= str+rs.getString("file_name")+"+"+rs.getString("file_date")
							+"+"+rs.getString("file_size")+";";

				}while(rs.next());
				return str;
			}else
				return null;
				
			
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return null;
		}
		
	}

    /**
     * 删除用户文件功能
     * @param name
     * @param file_name
     */
	public  boolean deletefile(String name,String file_name){


        try {
            String s2="select * from userfile where user_name='"+name+"' and file_name='"+file_name+"'";
            if(!stmt.executeQuery(s2).next())
                return false;
            String s1="update userhash set userhash.file_count=userhash.file_count-1 where userhash.file_hash in " +
                    "(select userfile.file_hash from userfile " +
                    "where userfile.user_name='"+name+"' and userfile.file_name='"+file_name+"')";
            stmt.executeUpdate(s1);
            String s="delete from userfile where user_name='"+name+"' and file_name='"+file_name+"'" ;
            stmt.executeUpdate(s);
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 查询HASH值
     * @param hash
     * @return
     */
    public boolean selectHash(String hash){
	    String s="select * from userhash where file_hash='"+hash+"'";
        try {
            if(stmt.executeQuery(s).next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 关闭数据库
     */
    public void closeMysql() {
		try {
			stmt.close();
		} catch (SQLException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		try {
			connect.close();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}


    /**
     * 用户上传文件时在userfile表中加入记录
     * @param filename
     * @param username
     * @param filedata
     * @param filesize
     * @param hash
     */
	public void insertUserfile(String filename,String username,String filedata,
                               String filesize,String hash){
        String s="insert into userfile values ('"+filename+"','"+username+"','"+filedata
        +"','"+filesize+"','"+hash+"')";
        try {
            stmt.executeUpdate(s);
        }catch (SQLException e){

        }
    }


    /**
     * 文件在服务器不存在时添加哈希记录
     * @param hash
     */
    public void insertUserhash(String hash){
	    String s="insert into userhash values ('"+hash+"','"+1+"')";
	    synchronized (Mysql.class) {
            try {
                stmt.executeUpdate(s);
            } catch (SQLException e) {
            }
        }
    }

    /**
     * 更新文件连接数目
     * @param hash
     */
    public void updatehash(String hash){
        String s="update userhash set userhash.file_count=userhash.file_count+1 " +
                "where userhash.file_hash='"+hash+"'";
        try {
            stmt.executeUpdate(s);

        } catch (SQLException e) {

        }
    }


    /**
     * 更新用户登陆的判断
     * @param username
     */
    public void updateuser(String username){
        String s="update user set user.isuser="+true+
                " where user.user_name='"+username+"'";
        try {
            stmt.executeUpdate(s);

        } catch (SQLException e) {

        }
    }


    /**
     * 查询用户的文件
     * @param file_name
     * @param user_anem
     * @return
     */
    public String selectfile(String file_name, String user_anem) {
        String s="select * from userfile where file_name='"+file_name+"' and user_name='"+user_anem+"'";
        try {
            ResultSet resultSet=stmt.executeQuery(s);
            if (resultSet.next()){
                s=resultSet.getString("file_hash");
                return s;
            }else
                return null;

        } catch (SQLException e) {
            return null;
        }
    }


    /**
     * 判断用户是否存在
     * @param user_name
     * @return
     */
    public boolean selectuser(String user_name) {
        String s="select * from user where user_name='"+user_name+"'";
        try {
            ResultSet resultSet=stmt.executeQuery(s);
            if (resultSet.next()){
                return true;
            }else
                return false;

        } catch (SQLException e) {
            return true;
        }
    }


    /**
     * 创建用户
     * @param user_name
     * @param password
     */
    public void createUser(String user_name, String password) {

        String s="insert into user values ('"+user_name+"','"+password+"','1')";
        try {
            synchronized (Mysql.class) {
                stmt.executeUpdate(s);
            }

        } catch (SQLException e) {

        }
    }
}
