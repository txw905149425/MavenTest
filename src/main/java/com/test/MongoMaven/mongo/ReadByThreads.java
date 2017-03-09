package com.test.MongoMaven.mongo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

public  class ReadByThreads implements Runnable{
	static DB db;					 //mongodb连接
    static  String collection;       //图片存储的collection
    static GridFS gfsPhoto ;				 //GridFS gfsPhoto = new GridFS(db,collection);
    
    
    public ReadByThreads(DB db,String collection){
    	this.db=db;
    	this.collection=collection;
    }
    
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void readFile(GridFSDBFile gridDBFile) throws IOException{
//		  List<GridFSDBFile> listfiles=gridFs.find(query);  
//		    GridFSDBFile gridDBFile=listfiles.get(0);
//    	gridDBFile.writeTo("d:/a.png");  
    InputStream in=gridDBFile.getInputStream();
    byte[] buffer = new byte[1024*3]; 
    int flag=0;
    
    FileOutputStream fos = new FileOutputStream(new File("d:"+File.separator+gridDBFile.getFilename()));
	while((flag=in.read(buffer))!=-1){
		
	}

    
     
	}
	
	
	
	
}
