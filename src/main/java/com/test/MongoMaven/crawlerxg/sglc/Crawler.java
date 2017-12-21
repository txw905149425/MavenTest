package com.test.MongoMaven.crawlerxg.sglc;

import java.util.Date;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//傻瓜理财炒股--荐股--盘中实时更新
public class Crawler {
	public static void main(String[] args) {
		String day=IKFunction.getTimeNowByStr("yyyy-MM-dd");	
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://napi.shagualicai.cn/public_room/getrecomstocklist.shtml?page=1";
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			int num=IKFunction.rowsArray(data);
			mongo.getShardConn("xg_sglc_stock").deleteMany(Filters.exists("id"));
			HashMap<String, Object > map=null;
			try{
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data,i);
				String stock=IKFunction.keyVal(one, "stock").toString();
				String stockCode=IKFunction.regexp(stock, "(\\d+)");
				String stockName=stock.replace("("+stockCode+")", "");
				String timeObj=IKFunction.keyVal(one, "ctime").toString();
				String time=IKFunction.timeFormat(timeObj);
				String ktime=time.split(" ")[0];
				int dsize=IKFunction.comlitTimeReturnDay(ktime,day);
				if(dsize<2){
//					if(!IKFunction.judgeTime(time,IKFunction.getTimeNowByStr("yyyy-MM-dd HH:mm:ss"), 17)){//每天计划8:30抓取，就取到前一天收盘之后一个小时刚好17个小时
//						continue;
//					}
					Object answer=IKFunction.keyVal(one, "recominfo");
					Object name=IKFunction.keyVal(one, "room_name");
					map=new HashMap<String, Object>();
					 map.put("id", time+name);
					 map.put("title","傻瓜理财荐股");
					 map.put("time",ktime);
					 map.put("stockName", stockName);
					 map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					 map.put("type","1");
					 if(StringUtil.isEmpty(stockCode)){
					 	Document doc= mongo.getShardConn("stock_code").find(new BasicDBObject("name",stockName)).first();
					 	stockCode=doc.get("id").toString();
					 }
					 map.put("stockCode", stockCode);
					 map.put("name", name);
					 map.put("resion", answer);
					 map.put("website","傻瓜理财炒股");
					mongo.upsertMapByTableName(map, "xg_sglc_stock");
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  }
	
	}
}
