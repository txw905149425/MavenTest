package com.test.MongoMaven.fenci;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.helper.StringUtil;
import org.wltea.analyzer.core.IKSegmenter;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.test.MongoMaven.db.MysqlCollection;
import com.test.MongoMaven.uitil.FileUtil;



public class SplitKeyOneWord {
	static String reg="([`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？])";
	
	public static void main(String[] args) {
		String j=args[0];
		if(j==null){
			j="0";
		}
		int ss=Integer.parseInt(j);
		MysqlCollection  mySQLcoll = new MysqlCollection(); 
		Connection con= mySQLcoll.connSQL();
		ResultSet rs=null;
		String update="insert into a_a_lost(keyword) values(?)";
		int size=querySize("a_a_keyword_copy",con);
		int num=1;
		if(size%10000==0){
			num=size/10000;
		}else{
			num=size/10000+1;
		}
		int aa=0;
		int suNum=0;
		
		for(int i=ss;i<num;i++){
			aa=i;
			int limit=i*10000;
			String query="select * from a_a_keyword limit "+limit+",10000";
			rs=querySql(query,con);
			try{
				while(rs.next()){
					String keyword=rs.getString("keyword");
					if(!StringUtil.isBlank(keyword)){
						String tmp=null;
						boolean ggg=false;
					for(int n=0;n<keyword.length();n++){
							 tmp=keyword.substring(n,n+1);
							 if(!StringUtil.isBlank(tmp)){
								 try{
									 if(isUpdate(tmp)){
										 ggg=updateSql(update,con,tmp);
									 }
									}catch(Exception es){
									  es.printStackTrace();
									}
								if(ggg){
									suNum++;
								}
							 }
					 }
						if(suNum%10000==0&&suNum>0){
							System.out.println("插入数据《 "+suNum+" 》条");
						}
						
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				System.out.println("出错之前的i为：  "+aa);
			}
		}
		
	}
	
	
	public static ResultSet  querySql(String sql,Connection con){
		ResultSet rs=null;
		PreparedStatement statement=null;
		try {
			statement = con.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs=statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public static int  querySize(String table,Connection con){
		int num=0;
		ResultSet rs=null;
		PreparedStatement statement=null;
		String sql="select count(*) as num from "+table;
		try {
			statement = con.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs=statement.executeQuery();
			rs.next();
			num=rs.getInt("num");
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return num;
	}
	
	public static boolean  updateSql(String sql,Connection con,String key) {
		boolean flag=false;
		try{
			PreparedStatement statement=null;
			statement = con.prepareStatement(sql);
			statement.setString(1,key);
			int num=statement.executeUpdate();
			if(num>0){
				flag=true;
			}
		}catch(MySQLIntegrityConstraintViolationException es){
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean isUpdate(Object key){
	   	 boolean flag=true;
	   	 if(key==null||key.toString()==""){
	   		 return false;
	   	 }
	   	 String name=key.toString();
	   	 name=name.replaceAll("\\s*","");
			  Pattern p =Pattern.compile(reg);
			  Matcher m = p.matcher(name);
				if (m.find()) {
					flag=false;
//					System.out.println(m.group());
				}
	   	 return flag;
	   }
}
