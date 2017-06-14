package com.test.MongoMaven.crawler1.hghd;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.PostData;

public class Crawler {
	//好股互动   直播间暂时没发现有问题！！！
	public static void main(String[] args) {
		String url="http://fundapi.wlstock.com:9002//Interactive/liveindex";
		PostData post=new PostData();
		HashMap<String, String> map=new HashMap<String, String>();
		String json="{\"sign\":\"1q2tKWXWnrGDm8fC\\/TcJWkvT40k=\",\"ver\":\"5.3.0\",\"oauth_token\":\"e7d60c4c-e7fd-4113-b5d4-7c05d04e87e7\"}";
		try {
			String html=post.postHtml(url, map, json, "utf8", 2);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	http://shiye.gesoiner.com:8080/shiyeim/chatLogController/getChatLogByRoomName.do?sign=b871696bbadaed552a20edddb17507a5&num=1&devType=PT_MIHM+NOTE+1LTE&start=0&roomName=meirifupan&toJid=obadhtyhkhfrm3wi43ocaskbglm&pt=2&roomId=3&ver=V4.2.0&user=obadhtyhkhfrm3wi43ocaskbglm
//	http://shiye.gesoiner.com:8080/shiye/fourInter/bbsListInterFourInter.dhtml?sign=e37f6de01ca213cee3af4e1232edbec8&devType=PT_MIHM+NOTE+1LTE&numPerPage=10&userId=obadhtxqfvdq0eawxavxol7knoye&pt=2&roomId=3&ver=V4.2.0&user=obadhtyhkhfrm3wi43ocaskbglm&pageNum=1
//	http://shiye.gesoiner.com:8080/shiye/fourInter/bbsListInterFourInter.dhtml?sign=55fc913d458f699e9ab255bd6b1d4d90&devType=PT_MIHM+NOTE+1LTE&numPerPage=10&userId=2493345391&pt=2&roomId=15&ver=V4.2.0&user=obadhtyhkhfrm3wi43ocaskbglm&pageNum=1
}
