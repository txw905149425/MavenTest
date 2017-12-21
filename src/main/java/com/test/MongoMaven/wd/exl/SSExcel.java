package com.test.MongoMaven.wd.exl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;



public class SSExcel {

	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) throws Exception {
//		  ArrayList<String> tlist =getTime();
		  ArrayList<String> tlist =FileUtil.readFileReturn("mydate");
		  for(String time:tlist){
			  time=time.trim();
			if(time.equals("2017-12-18")){
				continue;
			}
			System.out.println(time);
			XSSFWorkbook  wb = new XSSFWorkbook(); 
	        //创建一个SHEET  
			XSSFSheet  sheet1 = wb.createSheet("各股票当天的评论");  
	        ArrayList<String> title =getCode();
//	        ArrayList<String> title =new ArrayList<String>();
//	        title.add("000613");
//	        title.add("601106");
//	        title.add("000972");
	        //创建一行  
	        XSSFRow  row = sheet1.createRow(0);  
	        //填充标题  
	        int i=0;
	        XSSFCell cell =null;
	        HashMap<String , XSSFRow> rmap=new HashMap<String, XSSFRow>();
	        for (String  code:title){
	        	cell = row.createCell(i);
	            cell.setCellValue(code);
	            ArrayList<String> cont =getComments(time,code);
	            int j=1;
		        for(String content:cont){
		        	XSSFRow row1 =null;
		        	if(rmap.containsKey("row"+j)){
		        		row1=rmap.get("row"+j);
		        	}else{
		        		 row1 = sheet1.createRow(j);	//创建第j行
		        		 rmap.put("row"+j,row1);
		        	}
		        	row1.createCell(i).setCellValue(content);//createCell（i）第i列
//		        	System.out.println(content);
		        	j++;
		        }
	            i++; 
	        }  
	        FileOutputStream fileOut = new FileOutputStream("d:/ss_data/"+time+".xls");  
	        wb.write(fileOut);  
	        fileOut.close();
		  }
	        
	}
	
	public static ArrayList<String> getCode(){
		ArrayList<String> list=new ArrayList<String>();
		MongoCollection<Document> collection=mongo.getShardConn("stock_code");
		MongoCursor<String> cursor =collection.distinct("id", String.class).iterator();
		while(cursor.hasNext()){
			String t=cursor.next();
//			System.out.println(t);
			list.add(t);
		}
		cursor.close();
		return list;
	}
	
	public static ArrayList<String> getComments(String time,String code){
		ArrayList<String> list=new ArrayList<String>();
		MongoCollection<Document> collection=mongo.getShardConn("ss_all_speak");
		BasicDBObject find=new BasicDBObject();	
		find.put("timedel", time);
		find.put("stock_code", code);
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			if(!doc.containsKey("list")){
				continue;
			}
			Object dlist=doc.get("list");
			int num=IKFunction.rowsArray(dlist);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(dlist, i);
				String ucontent=IKFunction.keyVal(one,"ucontent").toString();
				list.add(ucontent);
				if(one.toString().contains("flist")){
					Object flist=IKFunction.keyVal(one, "flist");
					int size=IKFunction.rowsArray(flist);
					for(int j=1;j<=size;j++){
						Object two=IKFunction.array(flist,j);
						String content=IKFunction.keyVal(two, "content").toString();
						list.add(content);
					}
				}
			}
		}
		cursor.close();
		return list;
	}

	public static ArrayList<String> getTime(){
		ArrayList<String> list=new ArrayList<String>();
		MongoCollection<Document> collection=mongo.getShardConn("ss_all_speak");
		MongoCursor<String> cursor =collection.distinct("timedel", String.class).iterator();
		while(cursor.hasNext()){
			String t=cursor.next();
//			System.out.println(t);
			if(isWeekend(t)){
				continue;
			}
			list.add(t);
		}
		cursor.close();
		return list;
	}
	
	public static boolean isWeekend(String time){
		boolean flag=false;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = format.parse(time);
			String we=IKFunction.getWeekOfDate(d);
		       if(we.equals("星期日")||we.equals("星期六")){
		    	  flag=true;
		       }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		return flag;
	}
	
}
