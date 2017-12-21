package com.test.MongoMaven.fenci;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class KeyWord {
	public static void main(String[] args) throws FileNotFoundException {
//		农发种业 600313
//		龙净环保 600388
//		清新环境 002573
//		三诺生物 300298
		String[] codelist={"三诺生物","300298","龙净环保","600388","清新环境","002573","大盘","上证","上证指数","沪深300指数"};
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  collection=mongo.getShardConn("lzx_viewpoint");
		Bson filter = Filters.eq("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
//		Bson filter = Filters.eq("timedel", "2017-08-26");
		MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator();
		PrintWriter pw1=new PrintWriter(new File("d:/三诺生物.txt"));
		PrintWriter pw2=new PrintWriter(new File("d:/龙净环保.txt"));
		PrintWriter pw3=new PrintWriter(new File("d:/清新环境.txt"));
		PrintWriter pw4=new PrintWriter(new File("d:/大盘.txt"));
		while(cursor.hasNext()){
			 Document doc=cursor.next();
			 Object list=doc.get("contentlist");
			 int num=IKFunction.rowsArray(list);
			 for(int i=1;i<=num;i++){
				 Object one=IKFunction.array(list,i);
				 String text=IKFunction.keyVal(one, "cont").toString();
				 if(!StringUtil.isEmpty(text)){
					 for(int j=0;j<codelist.length;j++){
							String tmp=codelist[j];
							if(text.contains(tmp)){
								String content=text;
								if(content.length()<50){
									int dis1=i+1;
									int dis2=i+2;
									int dis3=i+3;
									if(dis1<num){
										Object two=IKFunction.array(list,dis1);
										String text1=IKFunction.keyVal(two, "cont").toString();
										content=content+text1;
									}
									if(dis2<num){
										Object two=IKFunction.array(list,dis1);
										String text1=IKFunction.keyVal(two, "cont").toString();
										content=content+text1;
									}
									if(dis3<num){
										Object two=IKFunction.array(list,dis1);
										String text1=IKFunction.keyVal(two, "cont").toString();
										content=content+text1;
									}
								}
								if(j==0||j==1){
									 pw1.println(content);
								 }else if(j==2||j==3){
									 pw2.println(content);
								 }else if(j==4||j==5){
									 pw3.println(content);
								 }
								 else{
									 pw4.println(content);
								 }
								
							 }
						}
					 
				 }
			 }
			 
		}
		cursor.close();
		 pw1.close();
		 pw2.close();
		 pw3.close();
		 pw4.close();
	} 
}
