package com.test.MongoMaven.crawlerxg.wcxg;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

//i问财选股  没有时间！！！！！！   网页版上面有时间哟  （爱投顾）
public class Crawler {
	
	public static void main(String[] args) {
		String url="http://www.iwencai.com/wukong/mobile/index.html?theme=1&hidesearchbox=1";
//		http://rs.p5w.net/index/company/showQuestionPage.shtml?stationId=&query=1
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		
//		http://www.iwencai.com/wap/search?qs=gx_box&w=%E4%B8%8A%E6%B5%B7%E5%9B%BD%E8%B5%84%E6%94%B9%E9%9D%A9&source=phone&queryarea=all&querytype=&tid=stockpick
		System.out.println(html);
	}
	
	public static void parse(String html){
		if(StringUtil.isEmpty(html)){
			
		}
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc,".hot_title>span");
		for(int i=0;i<num;i++){
			String title=IKFunction.jsoupTextByRowByDoc(doc, ".hot_title>span", i);
		}
//		GT_C_T=1&GT_C_K=69d747c4b9f641baf4004be4297e9f3b&GT_C_V=MWJEbEVUNDRsVmt3OXV3R22F/Ws42CRgVeaDkkxZzofm4Eoib/jXBhs+ix7a4WWdurbLJjVa/NmDpV8H6NOaoIisMld1V/35u+sQeVuJJ2rl0tyuIaX4DPPLrZyVVBZH8ZVfCaHejmfObd7A2iDB7w==&GT_T=1494244457268&GT_C_S=c1GF00lMSmS1k8jQQL6uGMt+U9c=

	}
	
}
