package com.test.MongoMaven.txw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.db.Collenction1;
import  com.test.MongoMaven.db.MysqlCollection;

public class IDCmongo2Mysql {
	static String regexp="(^[A-Za-z0-9]+$)|(.*楼$)|(.*苑$)|(.*厅$)|(.*亭$)|(.*区$)|(.*社$)|(.*站$)|(.*家$)|(.*路$)|(.*街$)|(.*道$)|(.*煤矿$)|(.*室$)|(.*号$)|(.*口$)|(.*客车$)|(.*楼$)|(.*厅$)|(.*行$)|(.*园$)|(.*店$)|(.*所$)|(.*庄$)|(.*部$)|(.*市$)|(.*屋$)|(.*场$)|(.*摊$)|(.*通讯$)|(.*点$)|(.*处$)|(.*中心$)|(.*户$)|(.*坊$)|(.*房$)|(.*基地$)|(.*养殖$)|(.*队$)|(.*吧$)|(.*照明 $)|(.*货运$)|(.*院$)|(.*木艺$)|(.*城$)|(.*ＫＴＶ$)|(.*KTV$)|(.*门窗$)|(.*库$)|(.*服饰$)|(.*服装$)|(.*总汇$)|(.*业$)|(.*部）$)|(.*摊床$)|(.*代办$)|(.*电器 $)|(.*寄賣$)|(.*寄卖$)|(.*铺$)|(.*内衣 $)|(.*经销$)|(.*装潢$)|(.*馆$)|(.*斋$)|(.*大棚$)|(.*组$)|(.*班$)|(.*间$)|(.*家私$)|(.*柜$)|(.*分厂$)|(.*团$)|(.*画廊$)|(.*会$)|(.*苗圃$)|(.*阁$)|(.*运输$)|(.*村$)|(.*分公司.*)|(.*经销部.*)|(.*电站.*)|(.*分厂.*)";
    
	public static void main(String[] args) {
    	 Collenction1 IDCcollection=new Collenction1() ;
    	 MysqlCollection  mycoll = new MysqlCollection();  
    	 copyFileName2Mysql(IDCcollection,"tianyanchaJsonCompanyBase_index",mycoll);
	          	 
	}
	
	
     public static void copyFileName2Mysql(Collenction1 IDCcollection,String collectionName,MysqlCollection h){
    	 boolean flag=false;
     	MongoDatabase IDCmongo=null;
     	Connection conn= h.connSQL();
     	try {
 			IDCmongo=IDCcollection.getMongoDataBase();
 		} catch (Exception e) {
 			try{
 				System.out.println("IDC服务器mogodb连接异常，尝试重新连接...");
 				IDCmongo=IDCcollection.getMongoDataBase();
 			}catch(Exception e1) {
 				System.out.println("IDC服务器mogodb连接异常...");
 				e1.printStackTrace();
 				System.exit(0);
 			}
 		}    	
     try{
//    	 http://t.10jqka.com.cn/api.php?method=group.getLatestPost&limit=20&page=0&pid=0&return=json&allowHtml=0&uid=384369689&code=601344
     	MongoCollection<Document> mogoCollection=IDCmongo.getCollection(collectionName);
     	String sql ="insert into a_a_company_clean(company_name) values(?)";
     	PreparedStatement  statement=conn.prepareStatement(sql) ;
    	BasicDBObject need=new BasicDBObject();             //取出需要的字段          
     	need.put("id", 1);
//     	Bson filter = Filters.exists("copyDown", false);
     	MongoCursor<Document> cursor =mogoCollection.find().projection(need).batchSize(10000).noCursorTimeout(true).iterator(); 
     	while(cursor.hasNext()){    		
     		Document doc=cursor.next();
     		Object company_name=doc.get("id");
     		int num=0;
     		if(isUpdate(company_name)){
	     		statement.setString(1,company_name.toString());
	        	try {
	        		num=statement.executeUpdate();
	   	        } catch (SQLException e) {  
	   	            System.out.println("插入数据库时出错：");  
	   	            e.printStackTrace();  
	   	        } catch (Exception e) {  
	   	            System.out.println("插入时出错：");
	   	            e.printStackTrace();  
	   	        }  
	        	if(num>0){
	        		System.out.println("插入数据："+company_name);
	        	}else{
	        		System.err.println("失败数据："+company_name);
	        	}
     		}     		    		
//     		insertSQL(sql, conn);
     	}
     	statement.close();
     	h.closeMysql(conn);
     	cursor.close();
    	System.out.println("ok!!!!!!!!!!!!!");
     	flag=true;
     	}catch(Exception e){
     		e.printStackTrace();
     		flag=false;
     	} 
     }
      
     
     public static boolean isUpdate(Object company_name){
    	 boolean flag=true;
    	 if(company_name==null||company_name.toString()==""){
    		 return false;
    	 }
    	 String name=company_name.toString();
    	 name=name.replaceAll("\\s*","");
		  Pattern p =Pattern.compile(regexp);
		  Matcher m = p.matcher(name);
			if (m.find()) {
				flag=false;
//				System.out.println(m.group());
			}
    	 return flag;
     }
     
     
     
     
     
     
//     public static  boolean insertSQL(String sql,Connection conn) {  
//	        try {  
//	        	 PreparedStatement  statement = conn.prepareStatement(sql);  
////	        	 statement.setString(1, "");
//	            statement.executeUpdate();  
//	            return true;
//	        } catch (SQLException e) {  
//	            System.out.println("插入数据库时出错：");  
//	            e.printStackTrace();  
//	        } catch (Exception e) {  
//	            System.out.println("插入时出错：");  
//	            e.printStackTrace();  
//	        }  
//	        return false;  
//	    }
     
}
