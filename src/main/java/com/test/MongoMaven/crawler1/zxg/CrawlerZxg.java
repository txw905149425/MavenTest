package com.test.MongoMaven.crawler1.zxg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//自选股App,股市直播问答数据（筛选有答案的，都会存）   ，数据量普通。2分钟抓取左右一次
public class CrawlerZxg {
	
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 PostData post=new PostData();
		 MongoCollection<Document> collection=mongo.getShardConn("ww_ask_online_all");		
		String url="http://proxy.finance.qq.com/group/newstockgroup/Live/getSquareList3?check=0&_appName=android&_dev=HM+NOTE+1LTE&_devId=28ec48936b5a9d2b42feb837340121c6d4a090b2&_mid=28ec48936b5a9d2b42feb837340121c6d4a090b2&_md5mid=7473A582ACF122D5CF8466B2C6B21A5E&_omgid=2dc4062302a6154fc7d804bd8d3d2b5dbc5a001021070b&_omgbizid=a487cdcc46702543c7f8511f43c222dfd58f014021230a&_appver=5.4.1&_ifChId=119&_screenW=720&_screenH=1280&_osVer=4.4.4&_uin=10000&_wxuin=20000&_net=WIFI&__random_suffix=37667";
		HashMap<String, String> map= new HashMap<String, String>();
		map.put("Referer", "http://zixuanguapp.finance.qq.com");
		map.put("User-Agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; HM NOTE 1LTE MIUI/V8.1.1.0.KHICNDI)");
		map.put("Content-Type", "application/x-www-form-urlencoded");
		ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("openid", "anonymous"));
		try {
			String html=HttpUtil.postHtml(url, map, list, 100, 1);
			list.add(new BasicNameValuePair("v", "2"));
			list.add(new BasicNameValuePair("begin", "-1"));
			list.add(new BasicNameValuePair("limit", "100"));
			List<String> listStr=parseList(html);
			String durl="http://183.57.48.75/group/newstockgroup/GroupChat/getGroupChatMsg?check=0&_appName=android&_dev=HM+NOTE+1LTE&_devId=28ec48936b5a9d2b42feb837340121c6d4a090b2&_mid=28ec48936b5a9d2b42feb837340121c6d4a090b2&_md5mid=7473A582ACF122D5CF8466B2C6B21A5E&_omgid=2dc4062302a6154fc7d804bd8d3d2b5dbc5a001021070b&_omgbizid=a487cdcc46702543c7f8511f43c222dfd58f014021230a&_appver=5.4.1&_ifChId=119&_screenW=720&_screenH=1280&_osVer=4.4.4&_uin=10000&_wxuin=20000&_net=WIFI&__random_suffix=16398";
			for(String str:listStr){
				list.add(new BasicNameValuePair("groupChatId", str));
				html=HttpUtil.postHtml(durl, map, list, 100, 1);
				List<HashMap<String, Object>> recordList=parseDetail(html);
				if(!recordList.isEmpty()){
					mongo.upsetManyMapByCollection(recordList, collection, "ww_ask_online_all");
//					for(HashMap<String, Object> one:recordList){
//						one.remove("json_str");
//						String ttmp=JSONObject.fromObject(one).toString();
//						 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
//							if(su.contains("exception")){
//								System.err.println("写入数据异常！！！！  < "+su+" >");
//							}
//						}
				}
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static List<String> parseList(String html){
		List<String> list =new ArrayList<String>();
		Object json=IKFunction.jsonFmt(html);
		Object block=IKFunction.keyVal(json, "data");
		Object array=IKFunction.keyVal(block, "hot_recommend");
		JSONArray tmp=JSONArray.fromObject(array);
		Object array1=IKFunction.keyVal(block, "recommend");
		JSONArray tmp1=JSONArray.fromObject(array1);
		for(int i=0;i<tmp.size();i++){
			list.add(tmp.get(i).toString());
		}
		for(int i=0;i<tmp1.size();i++){
			list.add(tmp1.get(i).toString());
		}
		return list;
	}
	
	public static List<HashMap<String, Object>> parseDetail(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object block=IKFunction.keyVal(json, "data");
		Object arr=IKFunction.keyVal(block, "data");
		int num=IKFunction.rowsArray(arr);
		String question="";
		String answer="";
		String name="";
		String time="";
		 HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object tmp=IKFunction.array(arr, i);
			Object ctime=IKFunction.keyVal(tmp, "ctime");
			time=IKFunction.timeFormat(ctime.toString());
			if(!IKFunction.timeOK(time)){
				continue;
			}
			if(tmp.toString().contains("parentInfo")){
				Object que=IKFunction.keyVal(tmp, "parentInfo");
				question=IKFunction.keyVal(que, "content").toString();
				answer=IKFunction.keyVal(tmp, "content").toString();
			}else{
				question=IKFunction.keyVal(tmp, "content").toString();
			}
			name=IKFunction.keyVal(tmp, "nickname").toString();
			map.put("id",IKFunction.md5(question+""+answer));
			map.put("tid",question+""+ctime);
			map.put("question", question);
			map.put("name", name);
			if(answer!=null&&answer.length()>4){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("answer", answer);
			map.put("time", time);
			map.put("website", "自选股");
//			map.put("json_str", tmp.toString());
			list.add(map);
		}
		
		return list;
		
	}
}
