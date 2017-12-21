package com.test.MongoMaven.crawlergd.ggcj;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="https://apiitlb.ggcj.com/lecturer_feeds/welcome?per=10&sign=4954db88b7aed1916395917fa93f18d0fd4360eab7ca12b42186972e10b6a3b97a6228b7bd87733645b6de6264027e82581dde12a32046c39769a26dc76d7c34";
		HashMap<String , String > map=new HashMap<String, String>();
		map.put("mobile", "HUAWEI HUAWEI M2-801");
		map.put("did", "49381175d96b80dcbd1ef13a7182c159");
		map.put("guaguaid", "230724775");
		map.put("meck", "ac61315a46c6c366edf28cb58a60a92119d58db4b60c3e452ba0508a246401e0");
		map.put("User-Agent", "Android");
		map.put("oemid", "28");
		map.put("sv", "6.0");
		map.put("authtoken", "S8cWbXyX1sw+n+o9YEAZKq8xSvfZkjAuyxRqNqNnVztH/XYXxZwVxQiQTQ9Tzdt4MLWTwOWXFuOxWjXsgb3+Lzjx8aJ+hnPXYrWl934n8raMp8IttM50Ki6YZwJEiCxONW2sW25g0y1sMLSXXohLeN1R5F3uCwnP9GWLphJjfbVcHLCWjJF0KTT+ELlyWfAR94ZHkKcHeqXLqRm1ocr2TIhcDFsUO69NEazflytqa/t2T1qVmGmiOxmPdONQCzG2pxU8WUcUq7+YgICQFN8DTsPjd3PFZuf3Fcf+VAYD4+7s7uBLqaQBu7u7BB1Xb136SvBTrNZKsq2lbyNd9GmLZQ==|230724775|ik|1|1499069250140");
		map.put("network", "wifi");
		map.put("oemid", "28");
		map.put("version", "5.5.8.0");
		map.put("gs", "0");
		map.put("guagua_id", "230724775");
		map.put("language", "CN");
		map.put("Last-Modified", "0");
		map.put("channel", "926");
		map.put("dt", "2");
		map.put("Host", "apiitlb.ggcj.com");
		map.put("Connection", "Keep-Alive");
		String html=HttpUtil.getHtml(url, map, "utf8", 1, new HashMap<String, String>()).get("html");
		System.out.println(html);
		if(!StringUtil.isEmpty(html)&&html.length()>300){
			Object json=IKFunction.jsonFmt(html);
			Object content=IKFunction.keyVal(json, "content");
			Object data=IKFunction.keyVal(content, "lecturer_feeds");
			int num=IKFunction.rowsArray(data);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data, i);
				Object time=IKFunction.keyVal(one, "published_at");
				if(!IKFunction.timeOK(time.toString())){
					continue;
				}
				Object name=IKFunction.keyVal(one, "name");
				Object id=IKFunction.keyVal(one, "id");
			}
			
			
		}
		
		
	}
}
