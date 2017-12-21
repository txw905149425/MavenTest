package com.test.MongoMaven.crawlertt.atg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.junit.runners.ParentRunner;

import com.test.MongoMaven.uitil.Constants;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//爱投顾
public class Crawler {
	public static void main(String[] args) {
		 Date date=new Date();
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	     String dd = sdf.format(date); 
		 String flag="";
		 for(String arg:args){
			if(arg.startsWith("flag=")){
				flag=arg.substring(5);
			}
		}
		String url="";
		if("1".equals(flag)){
			url="http://stock.jrj.com.cn/share/news/itougu/zhangting/"+dd+".js";
			System.out.println(url);
		}else if("2".equals(flag)){
			url="http://stock.jrj.com.cn/share/news/itougu/qingbao/"+dd+".js";
		}
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		try{
			List<HashMap<String, Object>> list=parseList(html);
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				PostData post=new PostData();
				for(HashMap<String, Object> result:list){
					result.remove("crawl_time");
					JSONObject mm_data=JSONObject.fromObject(result);
//					http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json
//					http://localhost:8888/import?type=tt_stock_json
				   String su=post.postHtml(Constants.ES_URI+"type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
					if(su.contains("exception")){
						System.out.println(mm_data.toString());
						System.err.println("写入数据异常！！！！  < "+su+" >");
					}
					mongo.upsetManyMapByTableName(list, "tt_json_all");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	public  static List<HashMap<String, Object>> parseList(String html){
		if(StringUtil.isEmpty(html)){
			return new ArrayList<HashMap<String, Object>>();
		}
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "newsinfo");
		int num=IKFunction.rowsArray(data);
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			for(int i=1;i<=num;i++){
				Object one =IKFunction.array(data, i);
				Object durl=IKFunction.keyVal(one, "infourl");
				HashMap<String,Object> map=parseDetail(durl.toString());
				if(map.isEmpty()){
					continue;
				}
				Object title=IKFunction.keyVal(one, "title");
				Object time=IKFunction.keyVal(one, "makedate");
//				Object abs=IKFunction.keyVal(one, "content");
				Object name=IKFunction.keyVal(one, "stockname");
				Object code=IKFunction.keyVal(one, "stockcode");
				List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
				if(name.toString().contains(",")){
					String[] tmp1=name.toString().split(",");
					String[] tmp2=code.toString().split(",");
					for(int j=0;j<tmp1.length;j++){
						HashMap<String, Object > map1=new HashMap<String, Object>();
						String sname=tmp1[j];
						String scode=tmp2[j];
						map1.put("name", sname);
						map1.put("code", scode);
						list1.add(map1);
					}
					code=code.toString().replace(",", " ");
				}else{
					HashMap<String, Object > map1=new HashMap<String, Object>();
					map1.put("name", name);
					map1.put("code", code);
					list1.add(map1);
				}
//				map.put("id", IKFunction.md5(title+"爱投顾"));
				map.put("tid", title+""+time);
				map.put("title", title);
				map.put("newsClass", "资讯");
				map.put("source", "爱投顾");
				map.put("code_list", list1);
				map.put("related",code );
//				map.put("abs", abs);
				map.put("time", time);
				map.put("durl", durl);
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				list.add(map);
		}
		return list;
	}
	
	public static HashMap<String, Object> parseDetail(String url){
		if(StringUtil.isEmpty(url)){
			return new HashMap<String, Object>();
		}
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		HashMap<String, Object> map=new HashMap<String, Object>();
		if(IKFunction.htmlFilter(html, ".tbox")){
			Object doc=IKFunction.JsoupDomFormat(html);
			String content=IKFunction.jsoupTextByRowByDoc(doc, ".tbox", 0);
			if(content.contains("你的浏览器不支持html5哟")){
				content=content.replace("你的浏览器不支持html5哟", "");
			}
			map.put("content", content);
			String id=content.substring(5, 20);
			map.put("id", IKFunction.md5(id));
		}
		return map;
	}
	
	
}
