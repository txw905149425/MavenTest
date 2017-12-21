package com.test.MongoMaven.wd.exl;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
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

public class ExcelData {


	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) throws Exception {
//		  ArrayList<String> tlist =getTime();
		  ArrayList<String> tlist =FileUtil.readFileReturn("mydate");
		  XSSFWorkbook  wb = new XSSFWorkbook(); 
	        //创建一个SHEET  
		  CellStyle style =wb.createCellStyle();
		  style.setAlignment(HorizontalAlignment.CENTER);
		  XSSFSheet  sheet1 = wb.createSheet("各股票当天的评论");
	       ArrayList<String> title =getCode();
//	        ArrayList<String> title =new ArrayList<String>();
//	        title.add("600000");
//	        title.add("600004");
//	        title.add("000972");
	        //创建一行  
	        XSSFRow  row = sheet1.createRow(0);//第一行
	        XSSFRow  row1 = sheet1.createRow(1);//第二行
	        //填充标题  
	        
	        XSSFCell cell =null;
	        HashMap<String , XSSFRow> rmap=new HashMap<String, XSSFRow>();
	        int h=2;
	        for(String time:tlist){
	        	XSSFRow  rtmp = sheet1.createRow(h);//第h+1行
	        	rmap.put("row"+h, rtmp);
        		XSSFCell cell0=rtmp.createCell(0);
        		cell0.setCellValue(time);
	        	h++;
	        }
	        int i=1;
	        for (String  code:title){
	        	System.out.println(code);
	        	cell = row.createCell(i);  //列
	            cell.setCellValue(code);
	            sheet1.addMergedRegion(new CellRangeAddress(0,0,i,i+2)); //	          	表示将first row, last row,first column,last column
	            for(int z=0;z<3;z++){
	            	cell = row1.createCell(z+i);  //列
	            	if(z==0){
	            		cell.setCellValue("股票涨跌");
	            	}else if(z==1){
	            		cell.setCellValue("言论变化");
	            	}else if(z==2){
	            		cell.setCellValue("言论数");
	            	}
	            }
	            ArrayList<HashMap<String , String>> cont =getComments(code);
		        for(HashMap<String , String> map:cont){
		        	String time=map.get("time");
		        	int j=tlist.indexOf(time);
		        	if(j==-1){
		        		continue;
		        	}
		        	XSSFRow row2 =rmap.get("row"+(j+2));
		        	for(int x=0;x<3;x++){
		        		cell = row2.createCell(x+i);  //列
		        		if(x==0){
		        			String rose=map.get("rose");
		        			if(!rose.contains("-")){
		        				rose="+"+rose;
		        			}
		        			cell.setCellValue(rose);
		            	}else if(x==1){
		            		String cp=map.get("cp");
//		            		System.out.println(cp);
		            		if(!cp.contains("-")){
		            			cp="+"+cp;
		            		}
		            		cell.setCellValue(cp);
		            	}else if(x==2){
		            		String comment=map.get("comment");	
		            		cell.setCellValue(comment);
		            	}
		        	}
		        }
	            i+=3; 
	        }
	        FileOutputStream fileOut = new FileOutputStream("d:/ss_data/ss_data.xlsx");  
	        wb.write(fileOut);  
	        fileOut.close();
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
	
	public static ArrayList<HashMap<String , String>> getComments(String code){
		ArrayList<HashMap<String , String>> list=new ArrayList<HashMap<String , String>>();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_count");
		BasicDBObject find=new BasicDBObject();	
		find.put("id", code);
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		HashMap<String , String> dmap=null;
		while(cursor.hasNext()){
			Document doc=cursor.next();
			if(!doc.containsKey("list")){
				continue;
			}
			Object dlist=doc.get("list");
			int num=IKFunction.rowsArray(dlist);
			for(int i=1;i<=num;i++){
				dmap=new HashMap<String, String>();
				Object one=IKFunction.array(dlist, i);
				String comment=IKFunction.keyVal(one,"comment").toString();
				String cp=IKFunction.keyVal(one,"cp").toString();
				String rose="";
				if(one.toString().contains("rose")){
					 rose=IKFunction.keyVal(one,"rose").toString();	
				}else{
					 rose="jcj";
				}
				String time=IKFunction.keyVal(one,"time").toString();
//				System.out.println(comment+" "+cp+" "+rose+" "+time);
				dmap.put("comment", comment);
				dmap.put("cp", cp);
				dmap.put("rose", rose);
				dmap.put("time", time);
				list.add(dmap);
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
