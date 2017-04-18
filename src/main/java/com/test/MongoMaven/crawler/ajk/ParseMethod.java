package com.test.MongoMaven.crawler.ajk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class ParseMethod {
	static String reg="(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d+)";
	

	
	/**
	 *html判断是否是正常内容
	 * */
	public static boolean htmlFilter(String html,String css){
		boolean flag=false;
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es = doc.select(css);
		if (es.size() > 0) {		
			flag=true;
		}
		return flag;
	}
	
	
	/*
	 * 抽内容,抽详情页的链接,爬虫性能上升，抽取评论数，判断是否大于0，然后在抓详情页，性能会高很多，代码会复杂)
	 * */
	public static List<String> parseList(String html){
		List<String> list=new ArrayList<String>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es=doc.select("p.title>span");
		int num=es.size();
		for(int i=0;i<num;i++){
			String url=IKFunction.jsoupListAttrByDoc(doc, "p.title>span", "href", i);
			list.add(url);
		}
		return list;
	}
	
	
	public static HashMap<String,Object> parseDetail(String html){
		HashMap<String,Object> jsonMap=new HashMap<String,Object>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		String name=IKFunction.jsoupTextByRowByDoc(doc, "#broker_true_name", 0);
		jsonMap.put("name", name);
		String phone=IKFunction.jsoupTextByRowByDoc(doc, ".broker_tel", 0);
		jsonMap.put("phone",phone);
		String company=IKFunction.jsoupListAttrByDoc(doc, ".broker_name>a","title", 0);
		jsonMap.put("company", company);
		String title=IKFunction.jsoupTextByRowByDoc(doc, ".wrapper>div>h3", 0);
		jsonMap.put("title", title);
		String plotName=IKFunction.jsoupTextByRowByDoc(doc, "h5.gray", 0);				//小区
		jsonMap.put("plotName", plotName);
		Element block=doc.select(".phraseobox.cf").get(0);
		Elements es=block.select(".p_phrase.cf"); 
		int num=es.size();
		String price="";//租金
		String yajing="";//押一付三
		String house_type="";//二室一厅
		String lease_way="";//租赁方式
		String position="";//位置
		String fitment="";//装修
		String floor="";//楼层
		String total_floor="";//总楼层
		String area="";//面积
		String towards="";//朝向
		String type="";//普通住宅
		for(int i=0;i<num;i++){
			String tmp=es.get(i).text();
			if(tmp.startsWith("租金押付")){
				yajing=tmp.substring(4);
				jsonMap.put("yajing", yajing);
			}else if(tmp.startsWith("租金")){
				price=tmp.replace("租金", "");
				jsonMap.put("price", price);
			}else if(tmp.startsWith("房型")){
				house_type=tmp.replace("房型", "");
				jsonMap.put("house_type", house_type);
			}else if(tmp.startsWith("租赁方式")){
				lease_way=tmp.replace("租赁方式", "");
				jsonMap.put("lease_way", lease_way);
			}else if(tmp.startsWith("位置")){
				position=tmp.replace("位置", "");
				jsonMap.put("position", position);
			}else if(tmp.startsWith("装修")){
				fitment=tmp.substring(2);;
				jsonMap.put("fitment", fitment);
			}else if(tmp.startsWith("面积")){
				area=tmp.replace("面积", "");
				jsonMap.put("area", area);
			}else if(tmp.startsWith("朝向")){
				towards=tmp.replace("朝向", "");
				jsonMap.put("towards", towards);
			}else if(tmp.startsWith("楼层")){
				String tt=tmp.replace("楼层", "");
				if(tt.contains("/")){
					floor=tt.split("/")[0];
					jsonMap.put("floor", floor);
					total_floor=tt.split("/")[1];
					jsonMap.put("total_floor", total_floor);
				}
			}else if(tmp.startsWith("类型")){
				type=tmp.replace("类型", "");
				jsonMap.put("type", type);
			}
			
		}
		String config=IKFunction.jsoupTextByRowByDoc(doc, "#proLinks", 0);//配置：床...
		jsonMap.put("config", config);
		String  account=IKFunction.jsoupTextByRowByDoc(doc, ".pro_detail", 0);//描述
		if(account.contains(config)){
			account=account.replace(config, "");
		}
		jsonMap.put("account", account);
		jsonMap.put("id", title+name+phone);
		return jsonMap;
	}

	
	
	
	public static String timeFormat(String str){
		if(StringUtil.isEmpty(str)){
			return "";
		}
		
		if(str.contains("今天")){
			 Date d = new Date();  
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		     String dateNowStr = sdf.format(d); 
			 str=dateNowStr+" "+str.replace("今天", "");
		}else if(str.contains("分钟前")){
			String tmp=str.replace("分钟前", "");
			int num=Integer.parseInt(tmp);
			long numMill=num*60*1000;
			Long s =System.currentTimeMillis()-numMill;
			Date date=new Date(s);
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		     String dateNowStr = sdf.format(date); 
			str=dateNowStr;
		}else if(str.contains("月")&&str.contains("日")){
			 Calendar now = Calendar.getInstance();  
		      int year=now.get(Calendar.YEAR); 
			  str=year+"-"+str.replace("月", "-").replace("日","");
		}else{
			System.err.println("*******====>  时间转换出现新情况："+str);
			return "1";
		}
//		System.out.println(str);
		return  str;
		
	}
	
	
	public static void test(){
		String str="你好slsksk";
		System.out.println(str.substring(0, 2));
	}
	
	public static void main(String[] args) {
		Map<String, String> map=HttpUtil.getHtml("http://zh.zu.anjuke.com/fangyuan/1058243146", new HashMap<String, String>(), "utf8", 1);
		String html=map.get("html");
		MongoDbUtil mongo=new MongoDbUtil();
		HashMap<String , Object> records=parseDetail(html);
		records.put("id", "http://zh.zu.anjuke.com/fangyuan/1058243146");
		mongo.upsertMapByTableName(records, "test");
	}
	
}
