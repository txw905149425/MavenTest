package com.test.MongoMaven.crawlerxg.ggx;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//股怪侠 --智能选股
public class Crawler {
	public static void main(String[] args) {
		String	day=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_ggx_stock").deleteMany(Filters.exists("id"));
		PostData post=new PostData();
		String url="https://www.guguaixia.com/eagle-frontap/jiabei/tao/v2.0/index";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>300){
			Object doc=IKFunction.jsonFmt(html);
			Object smartStock=IKFunction.keyVal(doc, "smartStock");
			Object jlist=IKFunction.keyVal(smartStock, "list");
			int num=IKFunction.rowsArray(jlist);
			String durl="https://www.guguaixia.com/eagle-frontap/jiabei/tao/smart/stock/list";
			try{
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(jlist, i);
				Object id=IKFunction.keyVal(one, "id");
				Object name=IKFunction.keyVal(one, "n");
				String json="{\"id\":\""+id+"\"}";
				String dhtml=post.postHtml(durl, new HashMap<String, String>(), json, "utf8", 1);
				ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
				String time="";
				if(!StringUtil.isEmpty(html)&&html.length()>300){
					Object ddoc=IKFunction.jsonFmt(dhtml);
					Object dlist=IKFunction.keyVal(ddoc, "list");
					time=IKFunction.keyVal(ddoc, "tm").toString();
					int dsize=IKFunction.comlitTimeReturnDay(time,day);
					if(dsize<2){
						int size=IKFunction.rowsArray(dlist);
						for(int j=1;j<=size;j++){
							Object two=IKFunction.array(dlist, j);
							Object stockName=IKFunction.keyVal(two,"n");
							Object stockCode=IKFunction.keyVal(two,"s");
							HashMap<String, Object> map=new HashMap<String, Object>();
							 map.put("stockName", stockName);
							 map.put("stockCode", stockCode);
							 list.add(map);
						}
					}
				}
				if(!list.isEmpty()){
					String dtime=IKFunction.getTimeNowByStr("yyyy-MM-dd");
					 HashMap<String, Object> resultMap=new HashMap<String, Object>();
					 resultMap.put("id", time+name);
					 resultMap.put("title",name);
					 resultMap.put("time",time);
					 resultMap.put("type","1");
					 resultMap.put("website","股怪侠");
					 resultMap.put("timedel",dtime);
					 resultMap.put("list",list);
					mongo.upsertMapByTableName(resultMap, "xg_ggx_stock");
					if(name.equals("超赢短线")){
					   mongo.upsertMapByTableName(resultMap, "app_xg_other_stock");
					}
				 }				
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
}
