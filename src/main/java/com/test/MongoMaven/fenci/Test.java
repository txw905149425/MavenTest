package com.test.MongoMaven.fenci;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.helper.StringUtil;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.test.MongoMaven.db.MysqlCollection;
import com.test.MongoMaven.uitil.FileUtil;

public class Test {
	
	 public static void main(String[] args) {
//		 String reg="(.*[A-Za-z]+.*)|(.*\\d+.*)|([`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？])";
//		 Pattern p =Pattern.compile(reg);
//		  Matcher m = p.matcher("a");
//			if (m.find()) {
//				System.out.println(m.group());
//				
//			}else{
//				System.out.println("......");
//			}
//		 String str="你好sss";
//		 for(int i=0;i<str.length();i++){
//			 String tmp=str.substring(i,i+1);
//			 System.out.println(tmp+"<   "+i);
//		 }
//		 MysqlCollection  mySQLcoll = new MysqlCollection(); 
//			Connection con= mySQLcoll.connSQL();
//			String update="insert into a_a_lost(keyword) values(?)";
			
		ArrayList<String > list=FileUtil.readFileReturn("d:/word.txt");
//		nohup java -cp mongoUtils-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.gaodig.Mongo2Cassandra collection=txw_judge_list_raw >txw_judge_list_raw &
		 for(String str:list){
			 String tmp="nohup java -cp mongoUtils-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.gaodig.Mongo2Cassandra collection=";
			 tmp=tmp+str+" > "+str+" &";
			 System.out.println(tmp);
			 
		 }
//		 nohup java -cp mongoUtils-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.gaodig.Mongo2Cassandra collection=lidan_bidding_contract_info_detail > lidan_bidding_contract_info_detail &
//		 for(String str:list){
//			 String tmp=null;
//			 for(int n=0;n<str.length();n++){
//				 tmp=str.substring(n,n+1);
//				 if(!StringUtil.isBlank(tmp)){
////					 System.out.println(tmp);
//					 updateSql(update,con,tmp);
//				 }
//			 }
////			 System.out.println(str);
//		 }
		 System.out.println(".....******....");
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
	 
	 
}
