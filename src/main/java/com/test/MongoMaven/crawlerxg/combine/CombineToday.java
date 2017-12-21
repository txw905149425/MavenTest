package com.test.MongoMaven.crawlerxg.combine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class CombineToday {
	
	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) {
		ArrayList<HashMap<String, Object>> dlist=new ArrayList<HashMap<String,Object>>();
		try{
			insert2Table("xg_gpdt_stock",dlist);
			insert2Table("xg_tzyj_stock",dlist);
			insert2Table("xg_ypgpt_stock",dlist);
			insert2Table("xg_yxg_stock",dlist);
			insert2Table("xg_znxg_stock",dlist);
			insert2Table("xg_xdgp_stock",dlist);
			complie2Table(dlist);
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void insert2Table(String table,ArrayList<HashMap<String, Object>> dlist) throws ClientProtocolException, IOException{
		MongoCollection<Document> collection=mongo.getShardConn(table);
		try{
		BasicDBObject find=new BasicDBObject();
		String today=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		find.put("time", today);
		 MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator(); 
		 		 Document doc=null;
		 HashMap<String, Object > records=null;
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 Object title=doc.get("title");
			 Object time=doc.get("time");
			 Object website=doc.get("website");
			 Object list=doc.get("list");
			 int num=IKFunction.rowsArray(list);
			 for(int i=1;i<=num;i++){
				 records=new HashMap<String, Object>();
				 Object one=IKFunction.array(list, i);
				 Object name=IKFunction.keyVal(one, "stockName");
				 if(name.toString().contains("ST")||name.toString().contains("st")){
					 continue;
				 }
				 Object code=IKFunction.keyVal(one, "code");
				 if(code.toString().length()!=6){
					 continue;
				 }
				 records.put("id", title+""+code+time+website);
				 records.put("stockName", name);
				 records.put("code", code);
				 records.put("title", title);
				 records.put("selectime", time);
				 records.put("website", website);
				 dlist.add(records); 
			 } 
		 }
	    cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void complie2Table(ArrayList<HashMap<String, Object>> dlist) throws ClientProtocolException, IOException{
		  try{ 
			  List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
			  HashMap<String, Object> records=null;
			 for(HashMap<String, Object> doc:dlist){
				 records=new HashMap<String, Object>();
				Object name= doc.get("stockName");
				Object code= doc.get("code");
				Object title= doc.get("title");
				Object time= doc.get("selectime");
				int supportnum=1;
				List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object > map1=new HashMap<String, Object>();
				map1.put("ss", title);
				int num=-1;
				for(int p=0;p<list.size();p++){
					HashMap<String, Object> map=list.get(p);
					if(map.get("code").toString().equals(code)){
						supportnum=Integer.parseInt(map.get("supportnum").toString())+1;
						list1=(List<HashMap<String, Object >>)map.get("support");
						num=p;
					}
				}
				if(num!=-1){
					 list.remove(num);
				}
				 list1.add(map1);
				 records.put("id", code+""+time);
				 records.put("stockName", name);
				 records.put("code", code);
				 records.put("selectime", time);
				 records.put("newprice", "");
				 records.put("supportnum", supportnum);
				 records.put("support", list1);
				 list.add(records);
			 }
			   List<HashMap<String, Object >> listOne=new ArrayList<HashMap<String,Object>>();
				for(HashMap<String,Object > record:list){
					String t1=record.get("supportnum").toString();
					int time=Integer.parseInt(t1);
//					if(time<=2){
//						continue;
//					}
					listOne.add(record);
				}
				 Collections.sort(listOne, new Comparator<HashMap<String, Object >>() {
			            public int compare(HashMap<String, Object > a, HashMap<String, Object > b) {
			                String  t1 =a.get("supportnum").toString();
			                String t2 = b.get("supportnum").toString();
			                int time=Integer.parseInt(t1);
			                int time1=Integer.parseInt(t2);
//			                return t2.compareTo(t1);
			                return time1-time;
			            }
			        });
			   mongo.upsetManyMapByTableName(listOne, "app_xg_all_our2");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

}
