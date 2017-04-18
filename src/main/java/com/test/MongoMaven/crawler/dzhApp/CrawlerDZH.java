package com.test.MongoMaven.crawler.dzhApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;


/***
 *  大智慧APP论股数据
 *  优化代码(1.循环创建对象)
 * */
public class CrawlerDZH {
	static int threadNum=3;
	public static void main(String[] args) {
		  ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			 MongoDbUtil mongo=new MongoDbUtil();
			 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
//			 Bson filter = Filters.exists("name", true);
			 Bson filter1 = new Document("id","601206");
			 MongoCursor<Document> cursor =collection.find(filter1).batchSize(10000).noCursorTimeout(true).iterator(); 
			 DataUtil util=null;
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 Object code=doc.get("id");
//				 "https://htg.yundzh.com/message/getZdwdData?p=1&code=SH600300"; 大智慧问答请求链接
				 String url="http://t.10jqka.com.cn/api.php?method=group.getLatestPost&limit=20&page=0&pid=0&return=json&allowHtml=0&uid=384369689&code="+code;
				 
				 url="http://data.mistat.xiaomi.com/mistats/v2?app_id=2882303761517415879&app_key=5521741580879&channel=appshop&device_id=E4B9AEDE870E7492739650AC10430DD833018467&interval=-1&mistatv=3&policy=1&sign=42282d8ecfe0c2c41fa7a9af010f53e9&size=6&stat_value=277efa890f09859e79a0ce7d2c1cd362aaa6b28466ef0c3de0ee7ffd45ac588b2fe1e1b23ae6c0a1a8519ed3f311d7551f4e3336713268a27d578610b9cfbd2da7d7677c56dc75a8665182386e6252b159464f12d1fc9254ad9bc91a9bba0432acbd469a7c9b04c8371f225b7cc204758c33fef570ed085fc52dbe9b1490a45fb6fd14cc9b8a0fe8a1cc4ffe0dfe6b4f1f9ff6f5e8b36dd815727da5d025c6919da3c249ea64f00a43e4444ef0376b9f26ea21b4b717dda4e10f70ad8230c0057801ba0d14a1c98fbf78b8913aed01b3cc2093b15c736bb0798bd07e403c512bca94396cfc840c5c1ae204cb10e6b2fc4b4ade8b31b6754256c1d67a5e739b1721b4e51c9724042a4287ac6ecc52a3104dadc1ef74ebf6fba75f8c6af35d1ff4246c8da7e3692e1f5fd3441844b61d2665f088858be6854496e3cb249e99fbfa6dfda5de94b75218500216650673e82bbdc8ae2e5b58f9907d1284db15774aafa61ebf8c4cb5d57fe4a5826a6a55650189e739466ab707ff9ba1492055d034bf017f669af7327de283f1b906362a5d0223e1df6ef8e605d8d5f4609f610207b1155a8d0406e382376f6b510b021164600160fafe57561591596b970ce0354d24d38839feaa6b6c165b6e1ca24b38a02c513a4cc90f3506b2694fa6dcd0a9cfcadbff6ebd10d99e9fcafe3a47f8b639b90ef2d6ee6fb28aa7506bd0197e28bd728ea86a80deab60342811c410bea5887ab908c8e7acb3c51f0de379e90ed988caee1942a8dc4e1949379b7832792af250f13ee6db5117599cc55793cc95a37bf8a18864a83908483db9f607179426a6ea07e0604f67e3bca39b39fc5f72d0bfe92701c324e779865fd274498eb2fd333b0011952db73f7dcea71605199984d31ca05a2c7b743bf3bdaf9dcae30e96a93398f6936d8fb7d87e4bbc619598843895b42b2c1d627dbcdd695ab452daa7f0fb03a0a7cd832b284e5c301ed0420bb7eb8db48d2bd15032abb04da10f89afae54047e38ad398972f09453b8ed2aa8dcf431eec05a6f8fa927d2579c59a124314144cb0bb8bf9d78cccdeb31a52b643dbe89e739466ab707ff9ba1492055d034bf017f669af7327de283f1b906362a5d0223e1df6ef8e605d8d5f4609f610207b1155a8d0406e382376f6b510b021164600160fafe57561591596b970ce0354d24d38839feaa6b6c165b6e1ca24b38a02c513a4cc90f3506b2694fa6dcd0a9cfcaa67fcd89f0214d9919060862ec2258ff29e8164691077f3495d06ad3fe89e455c2a28b64f82ceb8dd0204f2f6eb184b10a69698f8b5caa220037ed8c16eaa319aab57da3981a06ca0d68960a01ed44ca02537e0981302b49142e523022d160176484b6e812c67b47fd1a0b1caed46f64c57b37748d75c891bb3f1f496b48544739264be36b5558092124b103e354b6fd5c9c14d5963ee8748cf7ec69eaa23175d5c1ba46727d46dac51bce15543dc2c587611907f5fd2a0f30c1da9b218a22b0195f8e39b0edcbe244ce648d900b8e8dbc7677b4f63708a761219efe32c99f6990801b9adff868f144e23571597b653bfe17ffed67e50ff842a81accd37da5e82b2b19f1287562dbbf583bdab60251c96fd4c7f08e278565b69e2549318b89c2588692abe36291c6ba1bf0ba54ae7428749b81b9d0e02e1113d7e9ea944020ef34164ce1e35a6a56bbf82930eee77ff96965da5ad0fcba87e9076ba4a59c0d8b4edca5254387380be92449c867fb08ec67a74842ea65898454b025fa3ce48950628b546f5787b709a6d2f4185f5f9f51fa5fa7f49bfee0d6a1f42d64899ff9a980941bfb1e4dc41cb8b44321135121b0&version=M817032230-MA";
				 util=new DataUtil();
				 util.setCode(code.toString());
				 util.setUrl(url);
//				 new Actions1(util);
				 executor.execute(new Actions(util)); 
			 }
		  cursor.close();
		  executor.shutdown();
	}
	
	
}
