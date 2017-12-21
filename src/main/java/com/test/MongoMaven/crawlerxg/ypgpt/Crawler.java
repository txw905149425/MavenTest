package com.test.MongoMaven.crawlerxg.ypgpt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
		
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_ypgpt_stock").deleteMany(Filters.exists("id"));
		String url="https://gpc.upchina.com/getStrategyInfoList?pagesize=30&categoryId=0&page=1";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)){
			int num=IKFunction.rowsArray(html);
			List<HashMap<String, Object>> listmap=null;
			HashMap<String,Object> stockmap=null;
			HashMap<String,Object> records=null;
			try {
			for(int i=1;i<=num;i++){
				records=new HashMap<String, Object>();
				Object one=IKFunction.array(html, i);
//				System.out.println(one);
//				https://gpc.upchina.com/getInfoStagySingal?gscode=%E4%B8%BB%E9%A2%98%E9%BE%99%E5%A4%B4&ymd=20170503
				Object gscode=IKFunction.charEncode(IKFunction.keyVal(one, "gscode"),"utf8");
				Object date=IKFunction.keyVal(one, "NewDate");
				String time="";
				if(date.toString().length()==8){
					 time=date.toString().substring(0, 4)+"-"+date.toString().substring(4, 6)+"-"+date.toString().substring(6, 8);
				}
//				if("".equals(time)){
//					System.err.println("sss  "+date);
//				}
//				./mongoexport -h 127.0.0.1:27071 -d crawler -u group2017 -p group2017666 -c xg_stock_last -f stockName,code,supportnum -o  /home/jcj/gupiao.csv  --type csv
				Object title=IKFunction.keyVal(one, "gsName");
				Object abs=IKFunction.keyVal(one, "gsStyle");
				Object describe=IKFunction.keyVal(one, "reviewDesc");
				String durl="https://gpc.upchina.com/getInfoStagySingal?gscode="+gscode+"&ymd="+date;
				String xml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				int size=IKFunction.rowsArray(xml);
				listmap=new ArrayList<HashMap<String,Object>>();
				for(int j=1;j<=size;j++){
					stockmap=new HashMap<String,Object>();
					Object two=IKFunction.array(xml, j);
					Object code=IKFunction.keyVal(two, "gpcode");
					if(!code.toString().isEmpty()){
						code=code.toString().substring(2, 8);
					}
					if("".equals(code.toString())){
						continue;
					}
					Object name=IKFunction.keyVal(two, "gpName");
					Object selecprice=IKFunction.keyVal(two, "price");
					stockmap.put("code", code);
					stockmap.put("selecprice", selecprice);
					stockmap.put("stockName", name);
					listmap.add(stockmap);
				}
				if(listmap.isEmpty()){
//					System.err.println(durl+"   "+title);
					continue;
				}
				records.put("url", durl);
				records.put("id", title+""+date);
				records.put("title", title);
				records.put("time", time);
				records.put("website","优品股票通");
				records.put("ads", abs);
				records.put("describe", describe);
				records.put("list", listmap);
				mongo.upsertMapByTableName(records, "xg_ypgpt_stock");
				mongo.upsertMapByTableName(records, "xg_stock_json_all");
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
			
	}
	
}
