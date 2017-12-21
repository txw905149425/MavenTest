package com.test.MongoMaven.crawlerxg.wdgp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//万得股票---每日金股
public class Crawler {
	public static void main(String[] args) {
		String day=IKFunction.getTimeNowByStr("yyyy-MM-dd");	
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_wdgp_stock").deleteMany(Filters.exists("id"));
		String id=FileUtil.readFileReturn("/home/jcj/crawler/xg_bat/wdgp").get(0).trim();
		String url="http://www.windpartner.com.cn/strategyservice/app/expoHttp.htm?cmdCode=0167001&param=%7B%22subjectId%22%3A%22"+id+"%22%7D";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		Object doc=IKFunction.jsonFmt(html);
		Object json=IKFunction.keyVal(doc, "resultObject");
		Object jlist=IKFunction.keyVal(json, "stocks");
//		int num=IKFunction.rowsArray(jlist);
		String dtime=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		for(int i=1;i<=3;i++){
			Object one=IKFunction.array(jlist, i);
			Object stockName=IKFunction.keyVal(one, "stockName");
			Object stockCode=IKFunction.keyVal(one, "stockCode");
			String code=IKFunction.regexp(stockCode, "(\\d+)");
			Object resion=IKFunction.keyVal(one, "bright");
			Object timeObj=IKFunction.keyVal(one, "inputDate");
//			String time=parseTime(timeObj.toString());
			String time=IKFunction.timeFormat(timeObj.toString());
			String tim=time.split(" ")[0];
			if(!tim.equals(day)){
				continue;
			}
			HashMap<String, Object> resultMap=new HashMap<String, Object>();
			 resultMap.put("id", time+stockCode);
			 resultMap.put("title","每日金股");
			 resultMap.put("time",time);
			 resultMap.put("timedel",dtime);
			 resultMap.put("type", "1");
			 resultMap.put("name", "每日金股");
			 resultMap.put("stockName", stockName);
			 resultMap.put("stockCode", code);
			 resultMap.put("resion",resion);
			 resultMap.put("website","万得股票");
			 try {
				mongo.upsertMapByTableName(resultMap, "xg_wdgp_stock");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int tmp=Integer.parseInt(id)+1;
		try {
			PrintWriter pw=new PrintWriter(new File("/home/jcj/crawler/xg_bat/wdgp"));
			pw.println(tmp);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
