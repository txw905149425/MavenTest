package com.test.MongoMaven.crawler.thsApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class ParseMethod {
	
	/**
	 * 该方法只适应于同花顺论股的解析
	 * 存储json源码+源码的部分描述,参数html在传递之前，要做非空以及数据准确性判断！！！！
	 *1. 抽取部分json数据（code,stock_name）作为对整个json的描述
	 *2. 返回一个HashMap<String,Object>
	 * */
	public static HashMap<String,Object> parseAllJson(String html){
//		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> dbMap=new HashMap<String, Object>();
		Object json=IKFunction.jsonFmt(html);
		Object tmp=IKFunction.keyVal(json, "result");
		Object tmp1=IKFunction.keyVal(tmp, "postlist");
		Object tmp2=IKFunction.keyVal(tmp1, "1");
		Object tmp3=IKFunction.keyVal(tmp2, "forumObj");
		Object code=IKFunction.keyVal(tmp3, "code");
		Object name=IKFunction.keyVal(tmp3, "name");
		Object id=IKFunction.md5(html);
		dbMap.put("id",id);
		dbMap.put("code", code);
		dbMap.put("name", name);
		dbMap.put("html", json);
//		listDbMap.add(dbMap);
		return dbMap;
	}
	
	public static HashMap<String,Object> parseJson(String html){
//		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
	
		Object json=IKFunction.jsonFmt(html);
//		JSONObject json =JSONObject.fromObject(html);
		Object tmp=IKFunction.keyVal(json, "result");
		HashMap<String,Object> dbMap=formatTHSJson(tmp);
		Object postlist=IKFunction.keyVal(tmp, "postlist");
		Object tmp2=IKFunction.keyVal(postlist, "1");
		Object tmp3=IKFunction.keyVal(tmp2, "forumObj");
		Object code=IKFunction.keyVal(tmp3, "code");
		Object name=IKFunction.keyVal(tmp3, "name");
		dbMap.put("id", code);
		dbMap.put("name", name);
		dbMap.put("code", code+""+name);
//		dbMap.put("result", tmp);
		return dbMap;
	}
	
	
	
	/**
	 * 0:正常
	 * 14：该论股堂暂时不支持讨论
	 * 8888：其他情况
	 * */
	public static int htmlFilter(String html){
		int htmlCode=0;
		Object json=IKFunction.jsonFmt(html);
//		
		Object errorCode=IKFunction.keyVal(json, "errorCode");
		if(errorCode.toString().equals("0")){
			
		}else if(json.toString().contains("该论股堂暂时不支持讨论")){
			htmlCode=14;
//			System.out.println(json);
		}else{
			htmlCode=8888;
		}
		
		return htmlCode;
	}
	
	public static void main(String[] args) {
		String str="僵尸股……<begin>{\"type\":\"br\", \"content\":\"\", \"action\":\"\"}<end>跟跌，不跟涨！僵尸股……<begin>{\"type\":\"br\", \"content\":\"\", \"action\":\"\"}<end>跟跌，不跟涨！僵尸股……<begin>{\"type\":\"br\", \"content\":\"\", \"action\":\"\"}<end>跟跌，不跟涨！";
		str=str.replaceAll("<begin>","").replaceAll("<end>","");
		System.out.println(str);
		String test=str.replaceAll("\\{.*?\\}", "");
		System.out.println(test);
	}
	
	
	public static HashMap<String,Object> formatTHSJson(Object json){
		HashMap<String,Object> records=new HashMap<String, Object>();
		List<HashMap<String,Object>> listJsonMap=new ArrayList<HashMap<String,Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		Object postlist=IKFunction.keyVal(json, "postlist");
		Object userinfo=IKFunction.keyVal(json, "userinfo");
		for(int i=1;i<=20;i++){
			List<Long> timeList=new ArrayList<Long>();
			Object block=IKFunction.keyVal(postlist, i);
			if(StringUtil.isEmpty(block.toString())){
				break;
			}
			HashMap<String,Object> result=new HashMap<String, Object>();
			Object uid=IKFunction.keyVal(block,"uid");
			Object uname=IKFunction.keyVal(IKFunction.keyVal(userinfo,uid),"nickname");
			Object utime=IKFunction.keyVal(block,"ctime");
			long timel=Long.parseLong(utime.toString());
			timeList.add(timel);
			String ttt=IKFunction.timeFormat(utime.toString());  
			String ucontent=IKFunction.keyVal(block,"content").toString();
			if(ucontent.contains("<begin>")){//僵尸股……<begin>{"type":"br", "content":"", "action":""}<end>跟跌，不跟涨！
				ucontent=ucontent.replaceAll("<begin>","").replaceAll("<end>","");
				ucontent=ucontent.replaceAll("\\{.*?\\}", "");
//				continue;
			}
			
			String comment=IKFunction.keyVal(block,"comment").toString();
			
			if(!StringUtil.isEmpty(comment)){
				//判断情况不确定，以后估计还有其他情况！！！！ 2017-06-13   唐
				
				List<HashMap<String,Object>> jsonMap=new ArrayList<HashMap<String,Object>>();
				JSONObject js=JSONObject.fromObject(comment);
				Iterator it = js.keys(); 
				HashMap<String,Object> dbMap=null;
	            while(it.hasNext()){  
	            	dbMap=new HashMap<String, Object>();
	               String  key = (String) it.next();
	               Object comm=IKFunction.keyVal(comment, key);
	               Object cid=IKFunction.keyVal(comm, "aid");
	               Object name=IKFunction.keyVal(IKFunction.keyVal(userinfo,cid),"nickname");
	               Object time=IKFunction.keyVal(comm,"ctime");
	               long timel1=Long.parseLong(time.toString());
	               timeList.add(timel1);
	               String ttt1=IKFunction.timeFormat(time.toString());
	               String content=IKFunction.keyVal(comm,"content").toString();
	               if(content.toString().contains("<begin>")){
	            	   content=content.replaceAll("<begin>","").replaceAll("<end>","");
	            	   content=content.replaceAll("\\{.*?\\}", "");
	               }
//	               System.out.println("评论：    "+name+"   "+time+"   "+content);
	            dbMap.put("name", name);
	   			dbMap.put("time", ttt1);
	   			dbMap.put("content", content);
	   			jsonMap.add(dbMap);
	            } 
	            if(jsonMap!=null&&!jsonMap.isEmpty()){
						result.put("flist", jsonMap);
				}
			}
			Collections.sort(timeList);
			result.put("lastCommentTime", IKFunction.timeFormat(timeList.get(timeList.size()-1).toString()));
			result.put("uname", uname);
			result.put("utime", ttt);
			result.put("ucontent", ucontent);
			result.put("website", "同花顺");
			listJsonMap.add( result);
		}
		records.put("list", listJsonMap);
		return records;
		
	}
	
	
}