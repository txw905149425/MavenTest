package com.test.MongoMaven.wx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;






import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Test {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  coll=mongo.getShardConn("jg_wx_gzh_all");
		ArrayList<String> list=FileUtil.readFileReturn("wx_gzh");
		try {
			for(int i=0;i<list.size();i++){
				String str=list.get(i);
				String name=str.split("=")[1];
				BasicDBObject doc5 = new BasicDBObject();
				doc5.put("name", name);
				MongoCursor<Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
				while(cursor.hasNext()){
					Document doc=cursor.next();
					Object code_list=doc.get("code_list");
					Object time=doc.get("time");
					Object dtime=doc.get("dtime");
					Object id=doc.get("id");
					int num=IKFunction.rowsArray(code_list);
					for(int j=1;j<=num;j++){
						Object one=IKFunction.array(code_list, j);
						Object code=IKFunction.keyVal(one, "code");
						HashMap<String, Object> dmap=new HashMap<String, Object>();
						dmap.put("id", id+""+code);
						dmap.put("name", name);
						dmap.put("code", code);
						dmap.put("time", time);
						dmap.put("dtime", dtime);
						mongo.upsertMapByTableName(dmap, "jg_wx_gzh_good");
					}
				}
				cursor.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}   
	
}
