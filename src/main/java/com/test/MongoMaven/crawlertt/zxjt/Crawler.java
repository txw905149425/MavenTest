package com.test.MongoMaven.crawlertt.zxjt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.Constants;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//中信建投 公告
public class Crawler {
	
		public static void main(String[] args) {
			
			 String flag="";
			 for(String arg:args){
				if(arg.startsWith("flag=")){
					flag=arg.substring(5);
				}
			 }
			 String url="";
			if("1".equals(flag)){
				 url="https://www.csc108.com/resourceCenter/things.jspx?pageNo=1"; //上市企业公告
			}else if("2".equals(flag)){
				url="https://www.csc108.com/messageinfo/tfpList.jspx"; //停复牌公告
			}
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			try{
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					PostData post=new PostData();
					for(HashMap<String, Object> result:list){
						result.remove("crawl_time");
						JSONObject mm_data=JSONObject.fromObject(result);
//						http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json
//						http://localhost:8888/import?type=tt_stock_json
					   String su=post.postHtml(Constants.ES_URI+"type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
						if(su.contains("exception")){
							System.out.println(mm_data.toString());
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
					}
					mongo.upsetManyMapByTableName(list, "tt_json_all");
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static List<HashMap<String, Object >> parse(String html){
			if(StringUtil.isEmpty(html)){
				return new ArrayList<HashMap<String,Object>>();
			}
			List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
			if(IKFunction.htmlFilter(html, ".qydt_list>ul>li")){
				Object doc=IKFunction.JsoupDomFormat(html);
				int num=IKFunction.jsoupRowsByDoc(doc, ".qydt_list>ul>li");
				for(int i=0;i<num;i++){
					String title=IKFunction.jsoupTextByRowByDoc(doc, ".qydt_list_left", i);
					String related="";
					if(title.contains(":")){
						related=title.split(":")[0];
					}else if(title.contains("：")){
						related=title.split("：")[0];
					}
					String time=IKFunction.jsoupTextByRowByDoc(doc, ".qydt_list_right",i);
					String durl="https://www.csc108.com"+IKFunction.jsoupListAttrByDoc(doc, ".qydt_list>ul>li>a", "href", i);
					HashMap<String, Object> map=parseDetail(durl);
					if(map.isEmpty()){
						continue;
					}
					map.put("title", title);
					map.put("newsClass", "公告");
					map.put("source", "中信建投证券");
					map.put("time", time);
					map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
					if(!StringUtil.isEmpty(related)){
						HashMap<String, Object> map1=new HashMap<String, Object>();
						Object code=IKFunction.regexp(related, "(\\d+)");
						Object name=IKFunction.regexp(related, "(.*)\\(");
						map1.put("code", code);
						map1.put("name", name);
						list1.add(map1);
						map.put("code_list",list1);
						map.put("related",code);
					}
					list.add(map);
					
				}
			}
			
			return list;
		}
		
		public static HashMap<String, Object> parseDetail(String url){
			if(url.length()<23){
				return new HashMap<String, Object>();
			}
			HashMap<String, Object> map=new HashMap<String, Object>();
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".metal_content")){
				Object doc=IKFunction.JsoupDomFormat(html);
				String context=IKFunction.jsoupTextByRowByDoc(doc, ".metal_content", 0);
				if(context.isEmpty()){
					return map;
				}
				map.put("content", context);
				map.put("id",IKFunction.md5(context));
				map.put("tid", url);
			}
			return map;
		}
		
		
}
