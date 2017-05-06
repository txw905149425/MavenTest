package com.test.MongoMaven.crawlertt.zxjt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
		public static void main(String[] args) {
			String url="https://www.csc108.com/resourceCenter/things.jspx?pageNo=1"; //上市企业公告
//			url="https://www.csc108.com/messageinfo/tfpList.jspx"; //停复牌公告
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
//			System.out.println(html);
//			System.exit(1);
			try{
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					mongo.upsetManyMapByTableName(list, "tt_zxtj_gonggao");
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
					map.put("class", "公告");
					map.put("source", "中信建投证券");
					map.put("time", time);
					List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
					if(!StringUtil.isEmpty(related)){
						HashMap<String, Object> map1=new HashMap<String, Object>();
						Object code=IKFunction.regexp(related, "(\\d+)");
						Object name=IKFunction.regexp(related, "(.*)\\(");
						map1.put("code", code);
						map1.put("name", name);
						list1.add(map1);
						map.put("related",list1);
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
				map.put("content", context);
				map.put("id", IKFunction.md5(context));
			}
			return map;
		}
		
		
}
