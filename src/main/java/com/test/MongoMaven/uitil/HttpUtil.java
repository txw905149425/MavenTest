package com.test.MongoMaven.uitil;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.test.MongoMaven.http.PostMethod;

public class HttpUtil {
       
	 /** 
     * ���ݸ���������get������ȡԴ���� 
     * @param url ���������� 
     * @param map ����ͷ
     * @param num ����ʱ��
     * @param times �������
     * @return htmlԴ����
     */ 
	@SuppressWarnings("resource")
	public static Map<String,String> getHtml(String url,HashMap<String,String> map,String charset,int time,HashMap<String,String> proxyMap){
		    url=url.trim();
			Map<String,String> resultMap=new HashMap<String,String>();
			String html="";
		  	CloseableHttpClient httpclient = null;
			  HttpGet httpGet=new HttpGet(url);
			  RequestConfig requestConfig = null;
			   Builder configBuilder = RequestConfig.custom();
			   CredentialsProvider credsProvider = new BasicCredentialsProvider();
			  
			   String ip="";
			   int port=8888;
			   String user="";
			   String pwd="";
		try {
			   if(!proxyMap.isEmpty()){
				   //密码设置代理
				   if(proxyMap.containsKey("ip")){
					   ip=proxyMap.get("ip");
				   }
				   if(proxyMap.containsKey("port")){
					   port=Integer.parseInt(proxyMap.get("port"));
				   }
				   if(proxyMap.containsKey("user")){
					   user=proxyMap.get("user");
				   }
				   if(proxyMap.containsKey("pwd")){
					   pwd=proxyMap.get("pwd");
				   }
				    if(proxyMap.containsKey("need")){
				    	credsProvider.setCredentials(new AuthScope(ip,port), new UsernamePasswordCredentials(user,pwd));
						   httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
						   org.apache.http.HttpHost proxyer = new org.apache.http.HttpHost(ip,port);
						    configBuilder.setProxy(proxyer);  //设置代理
					   }else{
						   org.apache.http.HttpHost proxyer = new org.apache.http.HttpHost(ip,port);
							configBuilder.setProxy(proxyer);					   }
			   }
			   if(time<10000){
				   requestConfig=configBuilder.setConnectTimeout(18000).setConnectionRequestTimeout(15000).setSocketTimeout(15000).build();
			   }else{
				   requestConfig=configBuilder.setConnectTimeout(time).setConnectionRequestTimeout(time).setSocketTimeout(time).build();
			   }
			   
			   
			    httpGet.setConfig(requestConfig);  
		 		 httpGet.setHeader("Accept-Encoding","gzip, deflate, sdch");  
	 			 httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");  
	 			 httpGet.setHeader("Upgrade-Insecure-Requests","1");  
	 			 httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");  
	 			 httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");  
	 			 httpGet.setHeader("Cache-Control","max-age=0");  
	 		  if(!map.isEmpty()){
	 			  for(String key:map.keySet()){
	 				  httpGet.setHeader(key,map.get(key));
	 			  }
	 		  }
	 		 DefaultHttpRequestRetryHandler retryHandler = new  DefaultHttpRequestRetryHandler(0,false);
	 		
	 		if (url.startsWith("https")) {//https 证书
				SSLContext sslcontext=null;
				SSLConnectionSocketFactory sslsf=null;
				try {
					sslcontext = SSLContext.getInstance("TLS");
					sslcontext.init(null, new TrustManager[] { truseAllManager }, null);
					sslsf= new SSLConnectionSocketFactory(sslcontext);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				httpclient = HttpClients.custom().setRetryHandler(retryHandler).setDefaultCredentialsProvider(credsProvider).setSSLSocketFactory(sslsf).build();
			}else{
				httpclient = HttpClients.custom().setRetryHandler(retryHandler).setDefaultCredentialsProvider(credsProvider).build();
			}
	 		CloseableHttpResponse response=null;
				response = httpclient.execute(httpGet);
				   HttpEntity entity = response.getEntity();
//				    if(entity!=null){
//				    	 html=EntityUtils.toString(entity);
//				    	 resultMap.put("html",html);
//				    }
				 int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					ContentType contentType = ContentType.getOrDefault(entity);
					Charset defaultCharset = contentType.getCharset();
					//获取本次访问的cookie
					 Header[] header = response.getAllHeaders(); 
//					 resultMap.put("cookie",response.getFirstHeader("Set-Cookie").toString());
//					 System.out.println(response.getFirstHeader("Set-Cookie"));
					 if (header != null){ 
					    for (int i = 0; i < header.length; i++){ 
//					    	System.out.println(header[i].getName() + ":" + header[i].getValue()); 
						    if (header[i].getName().equalsIgnoreCase("Set-Cookie")){ 
						     	resultMap.put("setCookie",header[i].getValue());
						    }else if(header[i].getName().equalsIgnoreCase("cookie")){
						    	resultMap.put("cookie",header[i].getValue());
						    }
					    } 
					 } 
					try{
						if(entity!=null){
							if (defaultCharset == null) {
								byte[] raw = EntityUtils.toByteArray(entity);
								html = new String(raw);
								String charsetstr =StringUtil.getCharSet(html);
								if (!StringUtil.isEmpty(charsetstr)){
									html = new String(raw, charsetstr);
								}else if(!StringUtil.isEmpty(charset)){
									html = new String(raw, charset);
								}
							} else {
								html = EntityUtils.toString(entity,defaultCharset);
							}
							resultMap.put("html",html);
						}
						//这里关流????
						try{response.close();}catch(Exception e){}finally{
							httpclient.close();
						}
						
					}catch(Exception es){
						es.printStackTrace();
					}finally{
						
					}
			} else {
					System.out.println("MyClient抓取页面失败： " + "执行的URL： " + url + " StatusCode: " + statusCode);
			}
		} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			System.out.println("使用ip: "+ip+":"+port+"执行：  "+url+"异常");
		}catch(Exception e){
				e.printStackTrace();
				
		}
		  return resultMap;
	}
	
	private static TrustManager truseAllManager = new X509TrustManager(){  
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        	
        }  
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        	
        }  
        public X509Certificate[] getAcceptedIssuers() {  
            return null;  
        }  
    }; 
	
	 /** 
     * ���ݸ���������get������ȡԴ���� 
     * @param url ���������� 
     * @param map ����ͷ
     * @param list �������
     * @param num ����ʱ��
     * @param times �������
     * @return htmlԴ����
     */ 
	public static  String postHtml(String url,HashMap<String,String> map,ArrayList<NameValuePair> list,int num,int times) throws ClientProtocolException, IOException{
		  String html="";
		  CloseableHttpClient httpclient = null;
		  CredentialsProvider credsProvider = new BasicCredentialsProvider();
		  credsProvider.setCredentials(new AuthScope("proxy.abuyun.com",9010), new UsernamePasswordCredentials("HYN02A3L87U914YP","C497A086A8EDCED4"));
		  httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//		  for(int i=0;i<times;i++){
			  HttpPost httpPost=new HttpPost(url);
			  //����ʱ������
			  RequestConfig requestConfig = RequestConfig.custom(). setConnectTimeout(5000)
			  .setConnectionRequestTimeout(5000) 
			  .setSocketTimeout(5000).build();
			  httpPost.setConfig(requestConfig);  
			  httpPost.setHeader("Accept-Encoding","gzip, deflate, sdch");  
			  httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.8");  
			  httpPost.setHeader("Upgrade-Insecure-Requests","1");  
			  httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");  
			  httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");  
			  httpPost.setHeader("Cache-Control","max-age=0"); 
			  String postStr=map.get("postStr");
			  if(!StringUtil.isEmpty(postStr)){
				  map.remove("postStr");//移除postStr键值，该值不是请求头
				  //设置请求参数参数
		        StringEntity entity1=new StringEntity(postStr);
		        httpPost.setEntity(entity1);
			  }
	 		  if(!list.isEmpty()){
	 			 httpPost.setEntity(new UrlEncodedFormEntity(list,"utf-8")); 
	 		  }
	 		 if(!map.isEmpty()){
	 			  for(String key:map.keySet()){
	 				 httpPost.setHeader(key,map.get(key));
	 			  }
	 		  }
	 		 
	 		CloseableHttpResponse response= httpclient.execute(httpPost);
//	 		System.out.println(response.getStatusLine());	  
	 		try {
			    HttpEntity entity = response.getEntity();
			    if(entity!=null){
			    	 html=EntityUtils.toString(entity,"utf-8");
			    }
			    EntityUtils.consume(entity);
			}finally {
			    response.close();
			}
//		 	if(html!=null&&html!=""){
//		 		break;
//		 	}
//		  }
		  return html;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		String url="http://www.ccgp-shanghai.gov.cn/news.do?method=purchasePracticeMore&ec_i=bulletininfotable&bulletininfotable_crd=10&treenum=09&title=%E6%BE%84%E6%B8%85%E5%85%AC%E5%91%8A&flag=cqgg&method=purchasePracticeMore&bulletininfotable_totalpages=6&bulletininfotable_totalrows=58&bulletininfotable_pg=1&bulletininfotable_rd=10&findAjaxZoneAtClient=false&&bulletininfotable_p=1";
		url="http://www.yztz.com/trade/strategy/sim/getUserDetailInfo.htm";
		url="http://gmv.cjzg.cn/Mv/get_more.html";
		url="http://wisefinance.chinaeast.cloudapp.chinacloudapi.cn:8000/wf/search?callback=JSON_CALLBACK&type=ss_stock_json&terms=name:连云港";
		url="http://wisefinance.chinaeast.cloudapp.chinacloudapi.cn:8000/wf/search?type=ss_stock_json_test&terms=name:%E5%9B%BD%E4%B8%AD%E6%B0%B4%E5%8A%A1";
//		String tmp=IKFunction.charEncode("连云港", "utf8");
//		System.out.println(tmp);
//		url="http://wisefinance.chinaeast.cloudapp.chinacloudapi.cn:8000/wf/search?callback=JSON_CALLBACK&type=ss_stock_json&terms=id:000831";
		HashMap< String, String> map=new HashMap<String, String>();
		String html=getHtml(url, map, "utf8", 1, new HashMap<String, String>()).get("html");
		System.out.println(IKFunction.jsonFmt(html));
//		ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
//		list.add(new BasicNameValuePair("loadingnum", "1"));
//		list.add(new BasicNameValuePair("pageSize", "10"));
//		String html=postHtml(url, map, list, 1000, 1);
		
		System.out.println(html);
//		%E8%BF%9E%E4%BA%91%E6%B8%AF
//		%E8%BF%9E%E4%BA%91%E6%B8%AF
//		ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
//		keyword:中鼎
//		searchtype:1
//		objectType:2
//		areas:
//		creditType:
//		dataType:0
//		areaCode:
//		templateId:
//		exact:0
//		page:1
//		list.add(new BasicNameValuePair("keyword", "中鼎"));
//		list.add(new BasicNameValuePair("searchtype", "1"));
//		list.add(new BasicNameValuePair("objectType", "2"));
//		list.add(new BasicNameValuePair("dataType", "0"));
//		list.add(new BasicNameValuePair("exact", "0"));
//		list.add(new BasicNameValuePair("page", "1"));
////		list.add(new BasicNameValuePair("", ""));
//		String html=postHtml(url, map, list, 10000, 1);
//		 System.out.println(html);
//		 JSONObject json= JSONObject.fromObject(html);
//		 System.out.println(json);
		/*
		 * httpclient post请求时 request Payload 参数传递测试
		 * */
//		String str="callCount=1\n"+
//      "page=/bulletin.do?method=indexList\n"+
//      "httpSessionId=\n"+
//      "scriptSessionId=407C98D4E71CCF43C9C0B1C50B81D63C550\n"+
//      "c0-scriptName=bulletinlist\n"+
//      "c0-methodName=indexList\n"+
//      "c0-id=0\n"+
//      "c0-param0=string:1\n"+
//      "c0-param1=string:\n"+
//      "batchId=0\n";
//map.put("postStr", str);	
//		url="http://www.yngp.com/dwr/call/plaincall/bulletinlist.indexList.dwr";
//		CloseableHttpClient  httpClient =HttpClients.custom().build();  
//        HttpPost post = new HttpPost(url);  
//        post.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");  
//        post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");  
//        post.setHeader("Accept-Language","zh-cn,zh;q=0.8");  
//        post.setHeader("Accept-Encoding","gzip, deflate, sdch");  
//        post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");  
//        post.setHeader("Connection","keep-alive");  
//        post.setEntity(new UrlEncodedFormEntity(list,"utf-8")); 
////        StringEntity entity1=new StringEntity(str);
////        post.setEntity(entity1);
//        CloseableHttpResponse response=httpClient.execute(post); 
//        System.out.println(response.getStatusLine());	  
//        if(response.getEntity()!=null){
//        	String html =EntityUtils.toString(response.getEntity());
//        	System.out.println(html);
//	      }
        
	}
}