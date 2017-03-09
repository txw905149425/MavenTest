package com.test.MongoMaven.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;


public class HttpsSSL {
	
	/**
	 * 设置信任自签名证书
	 *  
	 * @param keyStorePath      密钥库路径
	 * @param keyStorepass      密钥库密码
	 * @return
	 */
	public static SSLContext custom(String keyStorePath, String keyStorepass){
	    SSLContext sc = null;
	    FileInputStream instream = null;
	    KeyStore trustStore = null;
	    try {
	        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        instream = new FileInputStream(new File(keyStorePath));
	        trustStore.load(instream, keyStorepass.toCharArray());
	        // 相信自己的CA和所有自签名的证书
	        sc =SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	    
	    } finally {
	        try {
	            instream.close();
	        } catch (IOException e) {
	        }
	    }
	    return sc;
	}
	
	
	
	
}
