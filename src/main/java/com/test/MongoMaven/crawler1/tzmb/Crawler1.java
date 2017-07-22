package com.test.MongoMaven.crawler1.tzmb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.storm.command.list;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//投资脉搏
public class Crawler1 {

	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 PostData post=new PostData();
		MongoCollection<Document> collection = mongo.getShardConn("ww_tzmb_user");
		Bson filter = Filters.exists("crawl", false);
		MongoCursor<Document> cursor = collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator();
		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				Object uid = doc.get("id");
				String url = "http://www.imaibo.net/index.php?app=qae&mod=Question&act=answersOfAnchor&limit=10&min=0&showHead=1&expert_uid="+ uid;
				String html = HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
				if (html.length() > 300) {
					List<HashMap<String, Object>> list = parse(html);
					if (!list.isEmpty()) {
						mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
//						for(HashMap<String, Object> one:list){
//							one.remove("json_str");
//							String ttmp=JSONObject.fromObject(one).toString();
//							 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
//								if(su.contains("exception")){
//									System.err.println("写入数据异常！！！！  < "+su+" >");
//								}
//							}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public static List<HashMap<String,Object>> parse(String html){
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data");
		Object list=IKFunction.keyVal(data, "list");
		Object user=IKFunction.keyVal(data, "author");
		Object name=IKFunction.keyVal(user, "uname");
		int num=IKFunction.rowsArray(list);
		List<HashMap<String,Object>> listmap=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(list,i);
			Object ans=IKFunction.keyVal(one, "answer");
			if(StringUtil.isEmpty(ans.toString())||ans.toString().equals("")){
				continue;
			}
		    map=new HashMap<String, Object>();
			Object answer=IKFunction.keyVal(ans,"content");
			Object ctime=IKFunction.keyVal(ans, "ctime");
			String time=IKFunction.timeFormat(ctime.toString());
			if(!IKFunction.timeOK(time)){
				continue;
			}
			Object que=IKFunction.keyVal(one, "content");
			Object doc=IKFunction.JsoupDomFormat(que);
			String question=IKFunction.jsoupTextByRowByDoc(doc, "body", 0);
			if(!StringUtil.isEmpty(answer.toString())){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
//			map.put("json_str",one.toString());
			map.put("id", IKFunction.md5(question+answer));
			map.put("tid", question+ctime);
			map.put("question", question);
			map.put("answer", answer);
			map.put("time", time);
			map.put("name", name);
			map.put("website","投资脉搏");
			listmap.add(map);
		}
		return listmap;
	}

}
