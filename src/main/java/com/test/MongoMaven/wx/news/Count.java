package com.test.MongoMaven.wx.news;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Count {
	public static void main(String[] args) throws FileNotFoundException {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  coll=mongo.getShardConn("jg_wx_gzh_good");
		ArrayList<String> list=FileUtil.readFileReturn("wx_gzh2");
		String [] str={"2017-11-23","2017-11-22","2017-11-21","2017-11-20","2017-11-17","2017-11-16"};
		DecimalFormat df=new DecimalFormat("0.0000");
		PrintWriter pw=new PrintWriter(new File("weight"));
		for(String name:list){
			BasicDBObject doc5 = new BasicDBObject();
			doc5.put("name", name);
//			long size=coll.count(doc5);
			MongoCursor<Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			HashMap<String, HashMap<String, Integer>> dmap=new HashMap<String, HashMap<String, Integer>>();
			while(cursor.hasNext()){
				Document doc=cursor.next();
			    String dtime=doc.getString("dtime");
			    boolean flag=false;
			    for(int i=0;i<str.length;i++){
			    	if(dtime.equals(str[i])){
			    		flag=true;
			    		break;
			    	}
			    } 
			    if(flag){
			    	continue;
			    }
			    if(!doc.containsKey("rose")){
			    	continue;
			    }
			    double rose=doc.getDouble("rose");
			    if(dmap.containsKey(name)){
			    	HashMap<String, Integer> map=dmap.get(name);
			    	if(rose>0.00){
			    		if(map.containsKey("up")){
			    			map.put("up", map.get("up")+1);
			    			if(map.containsKey("down")){
			    				map.put("down",map.get("down"));
			    			}
			    			dmap.put(name, map);
			    		}else{
			    			map.put("up", 1);	
			    			if(map.containsKey("down")){
			    				map.put("down",map.get("down"));
			    			}
			    			dmap.put(name, map);
			    		}
				    }else{
				    	if(map.containsKey("down")){
			    			map.put("down", map.get("down")+1);
			    			if(map.containsKey("up")){
			    				map.put("up",map.get("up"));
			    			}
			    			dmap.put(name, map);
			    		}else{
			    			map.put("down", 1);	
			    			if(map.containsKey("up")){
			    				map.put("up",map.get("up"));
			    			}
			    			dmap.put(name, map);
			    		}
				    }
			    }else{
			    	HashMap<String, Integer> map=new HashMap<String, Integer>();
			    	if(rose>0.00){
			    		map.put("up",1);
			    	}else{
			    		map.put("down",1);
			    	}
			    	dmap.put(name, map);
			    }
			    
			}
			cursor.close();
			HashMap<String, Integer> map =dmap.get(name);
			if(map.containsKey("up")&&map.containsKey("down")){
				int up=map.get("up");
				int down=map.get("down");
				String c=df.format((float)up/(up+down));
				System.out.println(name+":"+c+":"+(up+down));
				pw.println(name+":"+c);
			}
		}
		pw.close();
		
	}
}
