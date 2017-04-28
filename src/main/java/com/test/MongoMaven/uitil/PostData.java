package com.test.MongoMaven.uitil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class PostData {

	  public  String postHtml(String url,HashMap<String,String> map,String json,String charset,int times) throws ClientProtocolException, IOException{
		  if(!IKFunction.isEmptyString(url)&&!IKFunction.isEmptyString(json)){}
		  String html="";
		  CloseableHttpClient httpclient = null;
		  CredentialsProvider credsProvider = new BasicCredentialsProvider();
		  credsProvider.setCredentials(new AuthScope("proxy.abuyun.com",9010), new UsernamePasswordCredentials("HYN02A3L87U914YP","C497A086A8EDCED4"));
		  httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			  HttpPost httpPost=new HttpPost(url);
			  RequestConfig requestConfig = RequestConfig.custom(). setConnectTimeout(60000)
			     .setConnectionRequestTimeout(60000)
			     .setSocketTimeout(60000).build();
			  httpPost.setConfig(requestConfig); 
			  if(times>1){
				  httpPost.setHeader("Accept-Encoding","gzip, deflate, sdch");  
				  httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.8");  
				  httpPost.setHeader("Content-Type","text/json;charset=utf8");  
				  httpPost.setHeader("Upgrade-Insecure-Requests","1");  
				  httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");  
				  httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");  
				  httpPost.setHeader("Cache-Control","max-age=0"); 
			  }
			  if(!map.isEmpty()){
	 			  for(String key:map.keySet()){
	 				 httpPost.setHeader(key,map.get(key));
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
	 		  StringEntity s = new StringEntity(json.toString(),"utf-8");
		      s.setContentEncoding("utf-8");
		      s.setContentType("application/json");//发送json数据需要设置contentType
		      httpPost.setEntity(s);
	 		CloseableHttpResponse response= httpclient.execute(httpPost);
	 		 HttpEntity entity = response.getEntity();
	 		 int statusCode = response.getStatusLine().getStatusCode();
	 		if(statusCode==200){
				ContentType contentType = ContentType.getOrDefault(entity);
				Charset defaultCharset = contentType.getCharset();
				try{
					if(entity!=null){
						if (defaultCharset == null) {
//						if (charset == null) {
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
//							html = EntityUtils.toString(entity,charset);
						}
					}
			//这里关流????
					response.close();
					httpclient.close();
				}catch(Exception es){
					es.printStackTrace();
				}finally{
					
				}
		} else {
			System.out.println("MyClient抓取页面失败： " + "执行的URL： " + url + " StatusCode: " + statusCode);
	}
	 	 		
	 	return html;
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
	  
}
