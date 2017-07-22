package com.test.MongoMaven.crawler1.kcj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//看财经
public class Crawler {
	public static void main(String[] args) {
		String url="http://wsq.mtq.tvm.cn/fastquery/actions/posts/getpostslist.do?&topicid=1512152327370c41948b89aa0412cef0ed_0&postsids=1702191341237348a0a34e9be09dae8ecd_0%2C1702151524424b48ae93d32029b6546820_0&datatype=1&first=true&ui=%7B%22st%22%3A1496578733%2C%22role%22%3A%22user%22%2C%22headimg%22%3A%22http%3A%2F%2Fwx.qlogo.cn%2Fmmopen%2F82iaiacrlczbia5g7b8fUjAOvdg0c032URtGZbPPgcsVYxexr6wLib31NZU0AEvCEoEh6IsW7R6jeE97AXuNjDibQOfW8ddtGmlLW%2F0%22%2C%22sex%22%3A%22%22%2C%22sign%22%3A%22013e93815e39814aa68557ee4d223b3a%22%2C%22fans%22%3A1%2C%22province%22%3A%22%22%2C%22v%22%3A1%2C%22nickname%22%3A%22ik%22%2C%22jibie%22%3A%22%22%2C%22wxtoken%22%3A%2248578ccf2f60%22%2C%22id%22%3A%22wx_onkiuuAyZ1G5RkNJLWsAtbDj-LZI_0%22%2C%22username%22%3A%22%22%7D";
//		url="http://wsq.mtq.tvm.cn/fastquery/actions/posts/getpostslist.do?topicid=1512152327370c41948b89aa0412cef0ed_0&datatype=1&first=false&ui=%7B%22st%22%3A1496646652%2C%22role%22%3A%22user%22%2C%22headimg%22%3A%22http%3A%2F%2Fwx.qlogo.cn%2Fmmopen%2F82iaiacrlczbia5g7b8fUjAOvdg0c032URtGZbPPgcsVYxexr6wLib31NZU0AEvCEoEh6IsW7R6jeE97AXuNjDibQOfW8ddtGmlLW%2F0%22%2C%22sex%22%3A%22%22%2C%22sign%22%3A%22013e93815e39814aa68557ee4d223b3a%22%2C%22fans%22%3A1%2C%22province%22%3A%22%22%2C%22v%22%3A1%2C%22nickname%22%3A%22ik%22%2C%22jibie%22%3A%22%22%2C%22wxtoken%22%3A%2248578ccf2f60%22%2C%22id%22%3A%22wx_onkiuuAyZ1G5RkNJLWsAtbDj-LZI_0%22%2C%22username%22%3A%22%22%7D&last_id=170605112247084d5d8d6a5cc9538874da_0&last_timestamp=1496632967512";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&html.length()>200){
			parseList(html);
		}
		
	}
	
	public static void parseList(String html){
		MongoDbUtil mongo=new  MongoDbUtil();
		 PostData post=new PostData();
		Object doc=IKFunction.jsonFmt(html);
	 try {
		Object data=IKFunction.keyVal(doc, "postslist");
		int num=IKFunction.rowsArray(data);
		String url="";
		List<HashMap<String, Integer>> listq=new ArrayList<HashMap<String, Integer>>();//有答案的问题
		HashMap<String, Integer> qmap=null;
		List<HashMap<String, Object>> listn=new ArrayList<HashMap<String, Object>>();//无答案的问题
		HashMap<String, Object> records=null;
		for(int i=2;i<=num;i++){
			Object one =IKFunction.array(data, i);
			Object uid=IKFunction.keyVal(one, "id");
			String question=IKFunction.keyVal(one, "content").toString();
			Object answernum=IKFunction.keyVal(one, "plcount").toString();
			if("0".equals(answernum)){
				records=new HashMap<String, Object>();
				Object time=IKFunction.keyVal(one, "create_time");
				if(!IKFunction.timeOK(time.toString())){
					continue;
				}
				Object uname=IKFunction.keyVal(one, "nickname");
				records.put("id", IKFunction.md5(question));
				records.put("tid", question+time);
				records.put("name", uname);
				records.put("time", time);
				records.put("question", question);
				records.put("answer", "");
				records.put("ifanswer","0");
				records.put("website", "看财经");
				listn.add(records);
				continue;
			}
			int tmp=Integer.parseInt(answernum.toString());
			Object zan=IKFunction.keyVal(one, "zancount");
			qmap=new HashMap<String, Integer>();
			qmap.put(question, tmp);
			url=url+uid+":"+zan+":"+answernum+"|";
			listq.add(qmap);
		}
		if(!"".equals(url)){
			url=url.substring(0,url.length()-1);
			url=IKFunction.charEncode(url, "gb2312");
			String tmp="{'st':1496631070,'role':'user','headimg':'http://wx.qlogo.cn/mmopen/82iaiacrlczbia5g7b8fUjAOvdg0c032URtGZbPPgcsVYxexr6wLib31NZU0AEvCEoEh6IsW7R6jeE97AXuNjDibQOfW8ddtGmlLW/0','sex':'','sign':'013e93815e39814aa68557ee4d223b3a','fans':1,'province':'','v':1,'nickname':'ik','jibie':'','wxtoken':'48578ccf2f60','id':'wx_onkiuuAyZ1G5RkNJLWsAtbDj-LZI_0','username':''}";
			tmp=IKFunction.charEncode(tmp, "utf8");
			url="http://wsq.mtq.tvm.cn/fastquery/actions/comment/getcommentlist.do?topicid=1512152327370c41948b89aa0412cef0ed_0"+"&postsinfo="+url+"&ui="+tmp;
			String dhtml=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>200){
				List<HashMap<String, Object>> list=parse(dhtml,listq);
					mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
			}
		}
		if(!listn.isEmpty()){
		   mongo.upsetManyMapByTableName(listn, "ww_ask_online_all");
		}
	 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static List<HashMap<String, Object>> parse(String html,List<HashMap<String, Integer>> listq){
		Object doc=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(doc, "data");
		Object plist=IKFunction.keyVal(data, "pllist");
//		int num=IKFunction.rowsArray(plist);
		int count=0;
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();//无答案的问题
		HashMap<String, Object> records=null;
		for(int i=0;i<listq.size();i++){
			HashMap<String, Integer > qmap=listq.get(i);
			Iterator<String> iter = qmap.keySet().iterator();
			for (Entry<String, Integer> entry : qmap.entrySet()) {
				  String question=entry.getKey();
		           int  value= entry.getValue();
		           records=new HashMap<String, Object>();
		           String time="";
		           String answer="";
		           String uname="";
		           if(value==1){
		        	   Object one=IKFunction.array(plist, (count+1));  
		        	   count=count+1;
		        	   Object otime=IKFunction.keyVal(one, "create_timestamp");
		   			    time=IKFunction.timeFormat(otime.toString());
		   			    uname=IKFunction.keyVal(one, "nickname").toString(); 
		   		        answer=IKFunction.keyVal(one, "content").toString();
//		   		        if(answer.contains("您的提问已被选中")){
//		   		        	continue;
//		   		        }
		           }else{
		        	   for(int j=0;j<value;j++){
		        		   Object one=IKFunction.array(plist, count+j+1); 
		        		   Object otime=IKFunction.keyVal(one, "create_timestamp");
		       			   time=IKFunction.timeFormat(otime.toString());
		       			   Object uname1=IKFunction.keyVal(one, "nickname"); 
		       			   uname=uname+uname1+";";
		       			   Object answer1=IKFunction.keyVal(one, "content");
		       			   answer=answer+answer1+";";
		        	   }
		        	   answer= answer.substring(0, answer.length()-1);
		        	   uname=uname.substring(0,uname.length()-1);
		        	   count=count+value;
		           }
		            if(!StringUtil.isEmpty(answer)&&!answer.contains("您的提问已被选中")){
		            	records.put("ifanswer","1");
					}else{
						records.put("ifanswer","0");
					}
		            records.put("id", IKFunction.md5(question+time));
		            records.put("tid", question+time);
					records.put("name", uname);
					records.put("time", time);
					records.put("question", question);
					records.put("answer", answer);
					records.put("website", "看财经");
					list.add(records);
		    }
		}
		return list;
		
	}
	
}
