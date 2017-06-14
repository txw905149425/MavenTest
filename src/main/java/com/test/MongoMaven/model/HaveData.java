package com.test.MongoMaven.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class HaveData {
	public static  MongoDbUtil mongo=new MongoDbUtil();
	public static String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
	public static void main(String[] args) {
		getBaiduData();
		getYcjData();	
		getThsData();
		getDfcfData();
		sortData();
	}
	
	public static void sortData(){
		MongoCollection<org.bson.Document>  collection=mongo.getShardConn("xuangu_model_all");
		Bson filter = Filters.eq("time", time);
		org.bson.Document  sort=new org.bson.Document("fudu",-1);
		MongoCursor<org.bson.Document> cursor =collection.find(filter).sort(sort).batchSize(10000).noCursorTimeout(true).iterator();
		org.bson.Document doc=null;
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		 Bson dele=Filters.exists("_id",true);
		 mongo.getShardConn("xuangu_model_sort").deleteMany(dele);
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 String code=doc.get("code").toString();
			 if(map.containsKey(code)){
				 int num=map.get(code)+1;
				 map.put(code, num);
			 }else{
				 map.put(code, 1);
			 }
			 doc.remove("_id");
			 doc.remove("id");
			 doc.append("id", code);
			 doc.append("count", map.get(code));
			 mongo.upsertDocByTableName(doc, "xuangu_model_sort_all");
			 doc.append("id", code);
			 mongo.upsertDocByTableName(doc, "xuangu_model_sort");
		 }
	}
	
	
	public static void getBaiduData(){
		String url="https://gupiao.baidu.com/concept/";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		parseBaiduList(html);
	}
	public static void parseBaiduList(String html){
	try {
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, "div.hot-concept.clearfix>a");
		for(int i=0;i<num;i++){
			String durl="https://gupiao.baidu.com"+IKFunction.jsoupListAttrByDoc(doc, "div.hot-concept.clearfix>a","href", i);
			String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			List<HashMap<String, Object >> list=parseBaiduDetail(dhtml);
			if(!list.isEmpty()){
					mongo.upsetManyMapByTableName(list, "xuangu_model_all");
			}
		}
	  } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  } 
	}
	public static List<HashMap<String, Object >> parseBaiduDetail(String html){
		List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		Document doc=Jsoup.parse(html);
		int num=doc.select(".column2>a").size();
		HashMap<String, Object> map=null;
		String block=doc.select(".info.clear-after>h1>a").get(0).text();
		for(int i=0;i<num;i++){
		  String fudu =doc.select("div[data-ratio]").get(i).text();
		  if(fudu.contains("-")){
			  continue;
		  }
		  map=new HashMap<String, Object>();
		  Element es=doc.select(".column2>a").get(i);
		  String name=es.select("div").get(0).text();
		  String code=es.select("div").get(1).text();
		  map.put("id", block+name+code+"baidu"+time);
		  map.put("code",code);
		  map.put("name",name);
		  map.put("fudu",fudu);
		  map.put("time",time);
		  map.put("block",block);
		  list.add(map);
		}
		return list;
	}
	
	
	public static void getYcjData(){
		String url="http://www.Ycjg.com/markethot/hot_news.html";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		parseYcjList(html);
		
	}
	
	public static void parseYcjList(String html){
		try {
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc, ".content1>h3>a");
			for(int i=0;i<num;i++){
				String durl="http://www.Ycjg.com"+IKFunction.jsoupListAttrByDoc(doc, ".content1>h3>a","href", i);
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				List<HashMap<String, Object >> list=parseYcjDetail(dhtml);
				if(!list.isEmpty()){
						mongo.upsetManyMapByTableName(list, "xuangu_model_all");
				}
			}
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  } 
	}
	
	public static List<HashMap<String, Object >> parseYcjDetail(String html){
		List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		Document doc=Jsoup.parse(html);
		int num=doc.select("tr[data-code]").size();
		HashMap<String, Object> map=null;
		String block=doc.select(".breadcrumb>li>a").get(1).text();
		for(int i=0;i<num;i++){
		  String fudu =doc.select("tr[data-code]>td[class]").get(i).text();
		  if(fudu.contains("-")){
			  continue;
		  }
		  map=new HashMap<String, Object>();
		  Element es=doc.select("tr[data-code]").get(i);
		  String title=es.select("td>a").get(0).text().trim();
		  String name=IKFunction.regexp(title, "(.*)（").trim();
		  String code=IKFunction.regexp(title, "(\\d+)").trim();
		  map.put("id", block+name+code+"ycj"+time);
		  map.put("code",code);
		  map.put("name",name);
		  map.put("fudu",fudu);
		  map.put("time",time);
		  map.put("block",block);
		  list.add(map);
		}
		return list;
	}
	
	public static void getThsData(){
		String url="http://data.10jqka.com.cn/funds/gnzjl/";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(StringUtil.isEmpty(html)&&html.length()>50){
			parseThsList(html);	
		}
		
		
	}
	public static void parseThsList(String html){
		try {
			Object doc=IKFunction.JsoupDomFormat(html);
			for(int i=0;i<10;i++){
				String durl=IKFunction.jsoupListAttrByDoc(doc, ".tl>a","href", i);
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				List<HashMap<String, Object >> list=parseThsDetail(dhtml);
				if(!list.isEmpty()){
				  mongo.upsetManyMapByTableName(list, "xuangu_model_all");
				}
			}
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  } 
	}
	
	public static List<HashMap<String, Object >> parseThsDetail(String html){
		List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		Document doc=Jsoup.parse(html);
		int num=doc.select(".m-table.m-pager-table>tbody>tr").size();
		HashMap<String, Object> map=null;
		String block=doc.select(".board-hq>h3").get(0).text();
		for(int i=0;i<num;i++){
		  String fudu =doc.select(".m-table.m-pager-table>tbody>tr>td:nth-child(5)").get(i).text();
		  if(fudu.contains("-")){
			  continue;
		  }
		  map=new HashMap<String, Object>();
		  String name=doc.select(".m-table.m-pager-table>tbody>tr>td:nth-child(3)").get(i).text();
		  String code=doc.select(".m-table.m-pager-table>tbody>tr>td:nth-child(2)").get(i).text();
		  map.put("id", block+name+code+"ths"+time);
		  map.put("code",code);
		  map.put("time",time);
		  map.put("name",name);
		  map.put("fudu",fudu+"%");
		  map.put("block",block);
		  list.add(map);
		}
		return list;
	}
//	http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C.BK08161&sty=FCOIATA&sortType=C&sortRule=-1&page=1&pageSize=20&token=7bc05d0d4c3c22ef9fca8c2a912d779c&_g=0.41087330057969274
//	http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C.BK06111&sty=FCOIATA&sortType=C&sortRule=-1&page=1&pageSize=20&token=7bc05d0d4c3c22ef9fca8c2a912d779c&_g=0.9132004471780535

	public static void getDfcfData(){
		String url="http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C._BKGN&sty=FPGBKI&st=c&sr=-1&p=1&ps=10&cb=&js=var%20BKCache=[(x)]&token=7bc05d0d4c3c22ef9fca8c2a912d779c&v=0.6489206246219708";
//		url="http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C.BK06081&sty=FCOIATA&sortType=C&sortRule=-1&page=1&pageSize=20&token=7bc05d0d4c3c22ef9fca8c2a912d779c&_g=0.41087330057969274";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(),"utf8", 1, new HashMap<String, String>()).get("html");
//		System.out.println(html);
		if(!StringUtil.isEmpty(html)){
			parseDfcfList(html);
		}
		
	}	
	public static void parseDfcfList(String html){
		Object doc=IKFunction.arrayFmt(html);
		try {
			int num=IKFunction.rowsArray(doc);
			for(int i=1;i<=num;i++){
				String one=IKFunction.array(doc, i).toString();
				String[] str=one.split(",");
				String tmp=str[1];
				String block=str[2];
				String durl="http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C."+tmp+"1&sty=FCOIATA&sortType=C&sortRule=-1&page=1&pageSize=20&token=7bc05d0d4c3c22ef9fca8c2a912d779c&_g=0.41087330057969274";
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(),"utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(dhtml)){
					List<HashMap<String, Object >> list=parseDfcfDetail(dhtml,block);
						mongo.upsetManyMapByTableName(list, "xuangu_model_all");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static List<HashMap<String, Object >> parseDfcfDetail(String html,String block){
		Object doc=IKFunction.arrayFmt(html);
		List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		int num=IKFunction.rowsArray(doc);
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			String one=IKFunction.array(doc, i).toString();
			String[] str=one.split(",");
			String code=str[1];
			String name=str[2];
			String fudu=str[5];
			 if(fudu.contains("-")){
				  continue;
			  }
			map.put("code", code);
			map.put("name", name);
			map.put("fudu", fudu);
			map.put("block", block);
			map.put("time", time);
			map.put("id", block+code+name+time+"dfcf");
			list.add(map);
		}
		return list;
	}
	
	
	

}
