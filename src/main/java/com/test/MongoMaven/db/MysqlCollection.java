package com.test.MongoMaven.db;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlCollection {
	 
	 PreparedStatement statement = null;  
	    // connect to MySQL  
	   public Connection connSQL() {  
		  Connection conn = null;  
	        String dbUrl = "jdbc:mysql://172.158.1099.42:332546/hh";	                        // 地址
	        String username = "testtt";  				//本地mysql
	        String password = "jhjjjjj";      
	        try {   
	            Class.forName("com.mysql.jdbc.Driver" );   
				 conn = DriverManager.getConnection(dbUrl, username, password);
	          } catch ( ClassNotFoundException cnfex ) { 
	        	//捕获加载驱动程序异常  
	             System.err.println("装载 JDBC/ODBC 驱动程序失败。");  
	             cnfex.printStackTrace();   
	         }catch ( SQLException sqlex ) {  
	        	//捕获连接数据库异常  
	             System.err.println( "无法连接数据库" );  
	             sqlex.printStackTrace();   
	         } 
	        return conn;
	    }  
	  
	   
	   //close mysql collection 
	   public void closeMysql(Connection conn) {  
	        try {  
	            if (conn != null)  
	                conn.close();  
	        } catch (Exception e) {  
	            System.out.println("关闭数据库问题 ：");  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    // execute selection language  
	    ResultSet selectSQL(String sql,Connection conn) {  
	        ResultSet rs = null;  
	        try {
	            statement = conn.prepareStatement(sql);  
	            rs = statement.executeQuery(sql);  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	        return rs;  
	    }  
	  
	    // execute insertion language  
	    public  boolean insertSQL(String sql,Connection conn) {  
	        try {  
	            statement = conn.prepareStatement(sql);  
	            statement.executeUpdate();  
	            return true;  
	        } catch (SQLException e) {  
	            System.out.println("插入数据库时出错：");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("插入时出错：");  
	            e.printStackTrace();  
	        }  
	        return false;  
	    }  
	    //execute delete language  
	    boolean deleteSQL(String sql,Connection conn) {  
	        try {  
	            statement = conn.prepareStatement(sql);  
	            statement.executeUpdate();  
	            return true;  
	        } catch (SQLException e) {  
	            System.out.println("插入数据库时出错：");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("插入时出错：");  
	            e.printStackTrace();  
	        }  
	        return false;  
	    }  
	    //execute update language  
	    boolean updateSQL(String sql,Connection conn) {  
	        try {  
	            statement = conn.prepareStatement(sql);  
	            statement.executeUpdate();  
	            return true;  
	        } catch (SQLException e) {  
	            System.out.println("插入数据库时出错：");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("插入时出错：");  
	            e.printStackTrace();  
	        }  
	        return false;  
	    }  
	    // show data in ju_users  
	    void layoutStyle2(ResultSet rs) {  
	        System.out.println("-----------------");  
	        System.out.println("执行结果如下所示:");  
	        System.out.println("-----------------");  
	        System.out.println(" 用户ID" + "/t/t" + "淘宝ID" + "/t/t" + "用户名"+ "/t/t" + "密码");  
	        System.out.println("-----------------");  
	        try {  
	            while (rs.next()) {  
	                System.out.println(rs.getInt("ju_userID") + "/t/t"  
	                        + rs.getString("taobaoID") + "/t/t"  
	                        + rs.getString("ju_userName")  
	                         + "/t/t"+ rs.getString("ju_userPWD"));  
	            }  
	        } catch (SQLException e) {  
	            System.out.println("显示时数据库出错。");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("显示出错。");  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    public static void main(String args[]) {  
	  
	    	MysqlCollection h = new MysqlCollection();  
	    	Connection conn= h.connSQL();  
//	        String s = "select * from a_a_company_name"; 
	        String name="高地科技666";
//	        try {
//	        	name=URLEncoder.encode("高地科技", "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
	        String insert = "insert into a_a_company_name(company_name) values('"+name+"')";  
//	        String update = "update ju_users set ju_userPWD =123 where ju_userName= 'mm'";  
//	        String delete = "delete from ju_users where ju_userName= 'mm'";  
	        if (h.insertSQL(insert,conn) == true) {  
	            System.out.println("insert successfully");  
//	            ResultSet resultSet = h.selectSQL(s);  
//	            h.layoutStyle2(resultSet);  
	        }
//	        if (h.updateSQL(update) == true) {  
//	            System.out.println("update successfully");  
//	            ResultSet resultSet = h.selectSQL(s);     
//	            h.layoutStyle2(resultSet);  
//	        }  
//	        if (h.insertSQL(delete) == true) {  
//	            System.out.println("delete successfully");  
//	            ResultSet resultSet = h.selectSQL(s);  
//	            h.layoutStyle2(resultSet);  
//	        }  
	          
	        h.closeMysql(conn);
	    }  
}
