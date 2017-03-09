package com.test.MongoMaven.mongo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mongodb.gridfs.GridFSDBFile;

public class ReadMongo implements Runnable{
	private int fileLength;    //读取的文件大小
	private int threadNum;		//线程数
	private GridFSDBFile gridDBFile;	//需要读取的文件
	private HashMap<Integer,byte[]> map=new HashMap<Integer,byte[]>();
	public ReadMongo(int fileLength,int threadNum,GridFSDBFile gridDBFile){
		this.fileLength=fileLength;
		this.gridDBFile=gridDBFile;
		this.threadNum=threadNum;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
		
		
	}
	
	public void readFile(GridFSDBFile gridDBFile,int threadNum) throws IOException{
		
	    InputStream in=gridDBFile.getInputStream();
	    int size=(int)gridDBFile.getLength();
		 byte[] buffer = new byte[fileLength]; 
		 in.read(buffer);
		 map.put(threadNum,buffer);
		 
	    System.out.println(gridDBFile.getLength());
	    
	    
	     
		}
	
	public HashMap<Integer,byte[]> mapSort(HashMap<Integer,byte[]> map){
		HashMap<Integer,byte[]> map1=new HashMap<Integer,byte[]>();
		
		return map;
	}

}
