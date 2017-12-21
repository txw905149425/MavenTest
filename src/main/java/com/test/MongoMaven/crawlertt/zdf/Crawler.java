package com.test.MongoMaven.crawlertt.zdf;

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

//涨跌福   flag=1 (个股新闻)  flag=2 (股民热评)
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
			 url="http://api.matidata.com/news/stocks?app_key=a100001&api_token=c317cee0eb58f03f7379742c4f46009e&p=1&r=20";//个股新闻
		}else if("2".equals(flag)){
			url="http://api.matidata.com/news/comments";//股民热评
		}
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		try{
			List<HashMap<String, Object>> list=parse(html);
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				PostData post=new PostData();
				for(HashMap<String, Object> result:list){
					result.remove("crawl_time");
					JSONObject mm_data=JSONObject.fromObject(result);
//					http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json
//					http://localhost:8888/import?type=tt_stock_json
				   String su=post.postHtml(Constants.ES_URI+"type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
					if(su.contains("exception")){
						System.out.println(mm_data.toString());
						System.err.println("写入数据异常！！！！  < "+su+" >");
					}
				}
				mongo.upsetManyMapByTableName(list, "tt_json_all");
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
		Object data=IKFunction.keyVal(json, "data");
		Object arr=IKFunction.keyVal(data, "info");
		int num=IKFunction.rowsArray(arr);
		HashMap<String, Object > map=null;
		List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object one=IKFunction.array(arr, i);
			Object time=IKFunction.keyVal(one, "published");
			Object title=IKFunction.keyVal(one, "title");
			Object content=IKFunction.keyVal(one, "fragment");
			List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object > map1=null;
			String name=IKFunction.keyVal(one, "companyname").toString();
			String code=IKFunction.keyVal(one, "companycode").toString();
			if(name.contains(",")){
				String[] clist=code.split(",");
				String[] nlist=name.split(",");
				for(int j=0;j<clist.length;j++){
					map1=new HashMap<String, Object>();
					String ccode=clist[j];
					String nname=nlist[j];
					map1.put("code", ccode);
					map1.put("name", nname);
					list1.add(map1);
				}
				code=code.replace(",", " ");
			}else{
				map1=new HashMap<String, Object>();
				map1.put("code", code);
				map1.put("name", name);
				list1.add(map1);
			}
			map.put("id",IKFunction.md5(title+"涨跌福"));
			map.put("title",title);
			map.put("newsClass", "新闻");
			map.put("source", "涨跌福");
			map.put("time", time);
			map.put("related", code);
			map.put("code_list", list1);
			map.put("content", content);
			list.add(map);
		}
		return list;
	}
}
