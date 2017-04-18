package com.test.MongoMaven.crawlerjy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;

public class Test {
	
		public static void main(String[] args) {
		         Socket socket=null; 
		         try { 
		            //对服务端发起连接请求 
		            socket=new Socket("124.250.3.103", 52716); 
		            socket.setSoTimeout(10000);
		            System.out.println("Socket Success!"); 
		            
		            //接受服务端消息并打印 
		            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);  
		            
		            out.println("GET /myweb/demo.html HTTP/1.1");  
		            out.println("Accept: */*");  
		            out.println("Accept-Language: zh-cn");  
		            out.println("Host:172.16.99.99:12000");  
		            out.println("Connection:Keep-Alive");  
		            out.println();  
		            out.println();  
		            BufferedReader bufr=new BufferedReader(new InputStreamReader(socket.getInputStream()));  
		      
		            String line=null;  
		            while((line=bufr.readLine())!=null) { 
		                System.out.println(line);  
		                socket.close();
		            }
		        } catch (IOException e) { 
		            // TODO Auto-generated catch block 
		            e.printStackTrace(); 
		        } 
		}
		
		
}
