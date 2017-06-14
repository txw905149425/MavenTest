package com.test.MongoMaven.crawler1;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.PostData;

public class Tmp1 {
	public static void main(String[] args) {
		PostData post=new PostData();
		HashMap<String , String> map=new HashMap<String, String>();
//		map.put("User-Agent", "model/HM NOTE 1LTE version/android4.4.4 okhttp/3.1.2");
//		map.put("Content-Type", "application/x-www-form-urlencoded");
//		map.put("Host", "stockapp.nicaifu.com");
//		map.put("Connection", "Keep-Alive");
		
		
//		brand: Xiaomi
//		net: Wi-Fi
//		osversion: 4.4.4
//		Connection: close
//		version: 5.8.0
		map.put("htgtype","0");
		map.put("os","3");
		map.put("model","HM NOTE 1LTE");
		map.put("htgchannel","android");
		map.put("Cookie","htg_session=afd057cb2c9826a37c799a486492531bce300fc9");
		map.put("deviceToken","866401022288545");
		map.put("Content-Type","application/x-www-form-urlencoded");
		map.put("User-Agent","okhttp/3.3.0");
		map.put("Host","app.haotougu.com");
		map.put("Connection","Keep-Alive");
	try {
		String url="https://stockapp.nicaifu.com/stock/ox/recommendation_list";
		url="https://stockapp.nicaifu.com/stock/viewpoint/get_viewpoint_select";
		url="https://app.haotougu.com/message/tradeBoardv3";
		long time=System.currentTimeMillis();
		System.out.println(time);
		String ttt="a698979f6f470e06622bf8032077c8267906c689"+time;
//		System.out.println(ttt);
//		IKFunction.md5(ttt);
		String taken=getMd5Value(ttt);
		System.out.println(taken);
		String data="token="+taken+"&tm="+time;
		System.out.println(data);
		System.err.println("token=3b107722787d3490a02f83f892f5c7d8&tm=1495600542225");
		String html=post.postHtml(url, map,data,"utf8", 2);
		System.out.println(html);
		
	} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String exChange(String str){  
	    StringBuffer sb = new StringBuffer();  
	    if(str!=null){  
	        for(int i=0;i<str.length();i++){  
	            char c = str.charAt(i);  
	            if(Character.isUpperCase(c)){  
	                sb.append(Character.toLowerCase(c));  
	            }else{
	            	sb.append(c);  
	            } 
	        }  
	    }  
	      
	    return sb.toString();  
	}  
	
	
	public static String getMd5Value(String sSecret) {  
        try {  
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");  
            bmd5.update(sSecret.getBytes());  
            int i;  
            StringBuffer buf = new StringBuffer();  
            byte[] b = bmd5.digest();// 加密  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0)  
                    i += 256;  
                if (i < 16)  
                    buf.append("0");  
                buf.append(Integer.toHexString(i));  
            }  
            return buf.toString();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return "";  
    } 
}
