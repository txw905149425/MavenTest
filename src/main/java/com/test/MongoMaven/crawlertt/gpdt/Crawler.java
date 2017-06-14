package com.test.MongoMaven.crawlertt.gpdt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


//股票灯塔
public class Crawler {
	
		public static void main(String[] args) {
			String url="https://news.wedengta.com/getNews?AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&action=AdditionList&startid=0&_="+System.currentTimeMillis();
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			try{
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					PostData post=new PostData();
					mongo.upsetManyMapByTableName(list, "tt_json_all");
					for(HashMap<String, Object> result:list){
						result.remove("crawl_time");
						JSONObject mm_data=JSONObject.fromObject(result);
					   String su=post.postHtml("http://localhost:8888/import?type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
						if(su.contains("exception")){
							System.out.println(mm_data.toString());
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
		public static List<HashMap<String, Object >> parse(String html){
			if(StringUtil.isEmpty(html)){
				return new ArrayList<HashMap<String,Object>>();
			}
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "content");
			Object arr=IKFunction.keyVal(data, "vAdditionDesc");
			int num=IKFunction.rowsArray(arr);
			HashMap<String, Object > map=null;
			List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
			for(int i=1;i<=num;i++){
				map=new HashMap<String, Object>();
				Object one=IKFunction.array(arr, i);
				Object abs=IKFunction.keyVal(one, "sObject");
				Object step=IKFunction.keyVal(one, "sProcess");
				Object time=IKFunction.keyVal(one, "sProcessDate");
				Object content=IKFunction.keyVal(one, "sProject");
				Object collect_money=IKFunction.keyVal(one, "fFund");
				List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object > map1=new HashMap<String, Object>();
				Object name=IKFunction.keyVal(one, "sSecName");
				Object code=IKFunction.keyVal(one, "sSecCode");
				map1.put("code", code);
				map1.put("name", name);
				list1.add(map1);
				map.put("id",name+""+step+time);
				map.put("title",step);
				map.put("related",code);
				map.put("abs",abs);
				map.put("newsClass", "定增");
				map.put("source", "股票灯塔");
				map.put("time", time);
				map.put("collect_money", collect_money+"万元");
				map.put("code_list", list1);
				map.put("content", content);
				list.add(map);
			}
			return list;
		}
}