package com.test.MongoMaven.mongo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongodbImg {
  
	static DB db;					 //mongodb连接
    static  String collection;       //图片存储的collection
    static GridFS gfsPhoto ;				 //GridFS gfsPhoto = new GridFS(db,collection);
    
	public MongodbImg(DB db,String collection){
		this.db=db;
		this.collection=collection;
		
	}
	
	public static void main(String[] args) {
		String url="http://www.creditchina.gov.cn/pubdetail?encryStr=djt5bGluMTY=&dataType=1";
		try {
			downLoadFromUrl(url,"","");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 /*
	 * @param  inputStream  文件流
	 * @param  fileName     文件存储名
	 */
	public static  void  saveImage(InputStream inputStream,String fileName) throws IOException{
		gfsPhoto = new GridFS(db,collection);
		GridFSInputFile gfsfile=gfsPhoto.createFile(inputStream,fileName, true);
		gfsfile.save();
	}
	
	
	
	public static void savaFile(DB db,String collection,byte[] data){
		gfsPhoto = new GridFS(db,collection); //创建了两个collection，一个存图片信息，一个存图片的二进制数据
		GridFSInputFile gfsfile=gfsPhoto.createFile(data);
		gfsfile.setFilename("img");
		gfsfile.save();
	}
	
	 public static void  downLoadFromUrl(String url,String fileName,String savePath) throws IOException{  
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 HttpGet httpget = new HttpGet(url);
		 httpget.setHeader("Accept","*/*");
		 HttpResponse response = httpclient.execute(httpget);
		 HttpEntity entity = response.getEntity();
		 InputStream is = entity.getContent();
//		 saveImage(is,fileName);
		 
		 FileOutputStream fos = new FileOutputStream(new File(savePath+File.separator+fileName));
		 byte[] buffer = new byte[3*1024]; 
		 int inByte=0;
		 while ((inByte = is.read(buffer)) != -1) {
		     fos.write(buffer,0,inByte);
		 }
		 is.close();
		 fos.close();
   
     }  
	 
	 
	 public static void readFile() throws IOException{
		  //查找条件  
//      DBObject query=new BasicDBObject("filename","img");
		 DBObject query=new BasicDBObject();
		 gfsPhoto=new GridFS(db,collection);
     //查询的结果：  
      List<GridFSDBFile> listfiles=gfsPhoto.find(query);  
     GridFSDBFile gridDBFile=listfiles.get(0);
     InputStream in=gridDBFile.getInputStream();
     int size=(int)gridDBFile.getLength();
     System.out.println(gridDBFile.getLength());
  		        
	}
	
}
