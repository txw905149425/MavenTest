package com.test.MongoMaven.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.test.MongoMaven.uitil.HttpUtil;

public class PostMethod {

	public static void main(String[] args) {
		HttpUtil ht=new HttpUtil();
		String html=null;
		Map<String,String> resultMap=null;
		HashMap<String, String> map=new HashMap<String, String>();
		ArrayList<NameValuePair> list= new ArrayList<NameValuePair>();
		String url="http://ggzyjy.jl.gov.cn/JiLinZtb/Template/Default/MoreInfoJYXX.aspx?CategoryNum=004001";
		String aaaa="__EVENTTARGET@Pager;__EVENTARGUMENT@2;__VIEWSTATE@/wEPDwULLTE3ODc1MjExMzcPFgIeCXBhZ2VpbmRleAUBMBYCAgMPZBYOZg8QDxYGHg1EYXRhVGV4dEZpZWxkBQxDYXRlZ29yeU5hbWUeDkRhdGFWYWx1ZUZpZWxkBQtDYXRlZ29yeU51bR4LXyFEYXRhQm91bmRnZBAVCAzlt6XnqIvnsbvlnosM5pS/5bqc6YeH6LStDOW7uuiuvuW3peeoiwzmsLTliKnlt6XnqIsM5Lqk6YCa5bel56iLDOWcn+WcsOaVtOeQhgzlhpzkuJrlvIDlj5EM5Lqn5p2D5Lqk5piTFQgGMDA0MDAxCTAwNDAwMTAwMQkwMDQwMDEwMDIJMDA0MDAxMDAzCTAwNDAwMTAwNAkwMDQwMDEwMDUJMDA0MDAxMDA2CTAwNDAwMTAwNxQrAwhnZ2dnZ2dnZ2RkAgEPEA8WBh8BBQhDaXR5TmFtZR8CBQhDaXR5Q29kZR8DZ2QQFQ8G5Zyw5Yy6CeWQieael+ecgQnplb/mmKXluIIJ5ZCJ5p6X5biCCeWbm+W5s+W4ggnovr3mupDluIIJ6YCa5YyW5biCCeeZveWxseW4ggnmnb7ljp/luIIJ55m95Z+O5biCDOmHh+i0reS4reW/gwznnIHnuqfnm5HnnaMM6YeH6LSt55uR552jD+W7tui+ueiHquayu+W3nhLplb/nmb3lsbHnrqHlp5TkvJoVDwNhbGwGMjIwMDAwBjIyMDEwMAYyMjAyMDAGMjIwMzAwBjIyMDQwMAYyMjA1MDAGMjIwNjAwBjIyMDcwMAYyMjA4MDAGMjIwOTAwBjIyMTAwMAYyMjExMDAGMjIyNDAwBjIyMjUwMBQrAw9nZ2dnZ2dnZ2dnZ2dnZ2dkZAIDDxAPFgYfAQUMQ2F0ZWdvcnlOYW1lHwIFC0NhdGVnb3J5TnVtHwNnZBAVCAzlt6XnqIvnsbvlnosM5pS/5bqc6YeH6LStDOW7uuiuvuW3peeoiwzmsLTliKnlt6XnqIsM5Lqk6YCa5bel56iLDOWcn+WcsOaVtOeQhgzlhpzkuJrlvIDlj5EM5Lqn5p2D5Lqk5piTFQgGMDA0MDAxCTAwNDAwMTAwMQkwMDQwMDEwMDIJMDA0MDAxMDAzCTAwNDAwMTAwNAkwMDQwMDEwMDUJMDA0MDAxMDA2CTAwNDAwMTAwNxQrAwhnZ2dnZ2dnZxYBZmQCBA8QDxYGHwEFCENpdHlOYW1lHwIFCENpdHlDb2RlHwNnZBAVDwblnLDljLoJ5ZCJ5p6X55yBCemVv+aYpeW4ggnlkInmnpfluIIJ5Zub5bmz5biCCei+vea6kOW4ggnpgJrljJbluIIJ55m95bGx5biCCeadvuWOn+W4ggnnmb3ln47luIIM6YeH6LSt5Lit5b+DDOecgee6p+ebkeedowzph4fotK3nm5HnnaMP5bu26L656Ieq5rK75beeEumVv+eZveWxseeuoeWnlOS8mhUPA2FsbAYyMjAwMDAGMjIwMTAwBjIyMDIwMAYyMjAzMDAGMjIwNDAwBjIyMDUwMAYyMjA2MDAGMjIwNzAwBjIyMDgwMAYyMjA5MDAGMjIxMDAwBjIyMTEwMAYyMjI0MDAGMjIyNTAwFCsDD2dnZ2dnZ2dnZ2dnZ2dnZxYBZmQCBQ8QZGQWAGQCBw88KwAJAQAPFgQeCERhdGFLZXlzFgAeC18hSXRlbUNvdW50AhlkFjJmD2QWAmYPFQcBMSRjYTM1MGEwMS1lNGY1LTQ0N2UtOGJiMy03YjgzODdjZDI4M2RF6ZW/5pil5biC5Y+M6Ziz5Yy66b2Q5a626ZWH5YWz5a625bCP5a2m5paw5bu644CB57u05L+u5Y+K5Zy65Zyw5bel56iLRemVv+aYpeW4guWPjOmYs+WMuum9kOWutumVh+WFs+WutuWwj+WtpuaWsOW7uuOAgee7tOS/ruWPiuWcuuWcsOW3peeoiwzmlL/lupzph4fotK0LW+WPjOmYs+WMul0KMjAxOC0wNy0yN2QCAQ9kFgJmDxUHATIkZjJlY2ExYzMtOTRmNS00NDViLTkxOTItNWYzYjFkMTkzY2ZkLeWQieael+ecgeawtOWIqee7vOWQiOWKnuWFrOezu+e7n+S6jOacn+mhueebri3lkInmnpfnnIHmsLTliKnnu7zlkIjlip7lhazns7vnu5/kuozmnJ/pobnnm64M5rC05Yip5bel56iLCFvnnIHnuqddCjIwMTctMDItMDRkAgIPZBYCZg8VBwEzJGNmMTAwNmI3LWVjMzMtNDcxZi05MDA2LTQzYzUwYWE0NDExOEvlkInmnpfnnIHkuK3lsI/msrPmtYHmsaTmsrPnmb3lsbHluILmsZ/mupDljLrmub7msp/plYfkuIvmuLjmrrXloKTpmLLlt6XnqItL5ZCJ5p6X55yB5Lit5bCP5rKz5rWB5rGk5rKz55m95bGx5biC5rGf5rqQ5Yy65rm+5rKf6ZWH5LiL5ri45q615aCk6Ziy5bel56iLDOawtOWIqeW3peeoiwhb55yB57qnXQoyMDE3LTAxLTIzZAIDD2QWAmYPFQcBNCQ0MTU3MGIyMC0yMDE1LTQ3NmMtYTNkMi03MTUwYmMwODVlMGErMjAxNuW5tOWGhemZhua4lOaUv+aJp+azleW/q+iJh+i0ree9rumhueebrisyMDE25bm05YaF6ZmG5riU5pS/5omn5rOV5b+r6ImH6LSt572u6aG555uuDOawtOWIqeW3peeoiwhb55yB57qnXQoyMDE3LTAxLTIzZAIED2QWAmYPFQcBNSRjMmM0ODI5Mi0yYmViLTQzY2ItYTg5MS1iODhkZTlhNjUyZTF75Lit5bCP5rKz5rWB6ZuG5a6J5biC5bCP5paw5byA5rKz77yI5oql6ams5rKz5Y+j772e5paw5byA5rKz5aSn5qGl44CB6Iqx55S45aSn5qGl772e55m954Gw56qR5q6177yJ5aCk6Ziy5bel56iL77yI5LqM5pyf77yJe+S4reWwj+ays+a1gembhuWuieW4guWwj+aWsOW8gOays++8iOaKpemprOays+WPo++9nuaWsOW8gOays+Wkp+ahpeOAgeiKseeUuOWkp+ahpe+9nueZveeBsOeqkeaute+8ieWgpOmYsuW3peeoi++8iOS6jOacn++8iQzmsLTliKnlt6XnqIsIW+ecgee6p10KMjAxNy0wMS0yM2QCBQ9kFgJmDxUHATYkMGY1NzAxNTMtMmUxNS00ZWY1LTlkZWEtYTY0NTg0OWMzNmM0RemVv+aYpeW4guS5neWPsOWMuuS4reWMu+mZouS/oeaBr+euoeeQhuezu+e7n+mHh+i0remhueebruaLm+agh+WFrOWRikXplb/mmKXluILkuZ3lj7DljLrkuK3ljLvpmaLkv6Hmga/nrqHnkIbns7vnu5/ph4fotK3pobnnm67mi5vmoIflhazlkYoM5pS/5bqc6YeH6LStC1vkuZ3lj7DluIJdCjIwMTctMDEtMjBkAgYPZBYCZg8VBwE3JGU4NDRmZDM0LTUzYzgtNDVkNS05NzU2LTI3M2UxYjJiZDdmNFrplb/mmKXluILkuZ3lj7DljLrlu7rorr7ln47luILmo5rmiLfljLrlm57ov4Hlronnva7kvY/miL/kuIPlj7flnLDlm5vmnJ/ln7rnoYDorr7mlr3pobnnm65a6ZW/5pil5biC5Lmd5Y+w5Yy65bu66K6+5Z+O5biC5qOa5oi35Yy65Zue6L+B5a6J572u5L2P5oi/5LiD5Y+35Zyw5Zub5pyf5Z+656GA6K6+5pa96aG555uuDOaUv+W6nOmHh+i0rQtb5Lmd5Y+w5biCXQoyMDE3LTAxLTIwZAIHD2QWAmYPFQcBOCQwMjI2NTA3Ny0xOWI2LTRhMWMtOGUzZS0zOTZkOWE5NjU4Y2Rj6ZW/5pil5biC5Lmd5Y+w5Yy65pS/5bqc5LiD5Y+35Zyw5Z2X5Z+O5biC5qOa5oi35Yy65pS56YCg6aG555uu5Z+656GA6K6+5pa96YWN5aWX6YGT6Lev5pa95bel5bel56iLY+mVv+aYpeW4guS5neWPsOWMuuaUv+W6nOS4g+WPt+WcsOWdl+WfjuW4guajmuaIt+WMuuaUuemAoOmhueebruWfuuehgOiuvuaWvemFjeWll+mBk+i3r+aWveW3peW3peeoiwzmlL/lupzph4fotK0LW+S5neWPsOW4gl0KMjAxNy0wMS0yMGQCCA9kFgJmDxUHATkkODQ0NmY2MjItOTM2Yi00ZjkzLWFkOWItMjE1NjcwMzlkNmFmOeePsuaYpeW4guWFqOWfn+aXhea4uOWPkeWxleinhOWIkumhueebruacjeWKoeaLm+agh+mHh+i0rTnnj7LmmKXluILlhajln5/ml4XmuLjlj5HlsZXop4TliJLpobnnm67mnI3liqHmi5vmoIfph4fotK0M5pS/5bqc6YeH6LStC1vnj7LmmKXluIJdCjIwMTctMDEtMjBkAgkPZBYCZg8VBwIxMCRlYTI0OTg5My01ZWRhLTRlMTUtODhjYS0zYmUyZmFmYzhhZGNJ5ZCJ5p6X6K2m5a+f5a2m6ZmiKOacrOe6pynmoIflh4bljJblrp7ot7XmlZnogrLln7rlnLDlu7rorr4o5YWs5byA5oub5qCHKUnlkInmnpforablr5/lrabpmaIo5pys57qnKeagh+WHhuWMluWunui3teaVmeiCsuWfuuWcsOW7uuiuvijlhazlvIDmi5vmoIcpDOaUv+W6nOmHh+i0rRFb55yB6YeH6LSt5Lit5b+DXQoyMDE3LTAxLTE5ZAIKD2QWAmYPFQcCMTEkNGVkNmJmYWItNDk5Mi00MTJmLWE3MGItNDY2NzE3OTdiNWNkMeWQieael+ecgeaAu+W3peS8mijmnKznuqcp56yU6K6w5pys55S16ISRKOivouS7tykx5ZCJ5p6X55yB5oC75bel5LyaKOacrOe6pynnrJTorrDmnKznlLXohJEo6K+i5Lu3KQzmlL/lupzph4fotK0RW+ecgemHh+i0reS4reW/g10KMjAxNy0wMS0xOWQCCw9kFgJmDxUHAjEyJDNhOGIwZGZiLTc5YTEtNDY0ZS1iYjNkLTg1MGVlNzVkYjlmYjHlkInmnpfnnIHmgLvlt6XkvJoo5pys57qnKeeslOiusOacrOeUteiEkSjor6Lku7cpMeWQieael+ecgeaAu+W3peS8mijmnKznuqcp56yU6K6w5pys55S16ISRKOivouS7tykM5pS/5bqc6YeH6LStEVvnnIHph4fotK3kuK3lv4NdCjIwMTctMDEtMTlkAgwPZBYCZg8VBwIxMyQ1OGY2ZGIwNi1mY2Q3LTRhNjAtYTA5ZS1iZWI3NThiMjNmMWE+5ZCJ5p6X6Im65pyv5a2m6Zmi5a695bim5pyN5Yqh5Y+K5pWZ5a2m6K6+5aSHKOernuS6ieaAp+iwiOWIpCk+5ZCJ5p6X6Im65pyv5a2m6Zmi5a695bim5pyN5Yqh5Y+K5pWZ5a2m6K6+5aSHKOernuS6ieaAp+iwiOWIpCkM5pS/5bqc6YeH6LStEVvnnIHph4fotK3kuK3lv4NdCjIwMTctMDEtMTlkAg0PZBYCZg8VBwIxNCQ5MjhkMzA4Ni0zYmJiLTRkYjAtYjE0My1hZWQ4YjkxMGRmMjND5ZCJ5p6X55yB5Lqk6YCa6L+Q6L6T5Y6FKOacrOe6pynlip7lhazlj4rkuJPnlKjorr7lpIfotK3nva4o6K+i5Lu3KUPlkInmnpfnnIHkuqTpgJrov5DovpPljoUo5pys57qnKeWKnuWFrOWPiuS4k+eUqOiuvuWkh+i0ree9rijor6Lku7cpDOaUv+W6nOmHh+i0rRFb55yB6YeH6LSt5Lit5b+DXQoyMDE3LTAxLTE5ZAIOD2QWAmYPFQcCMTUkN2RmYmQzM2QtMWM2NS00ZmZiLTkwZTQtYmRmODVjYWZmZjU5ZeWQieael+ecgeS6uuWKm+i1hOa6kOWSjOekvuS8muS/nemanOS/oeaBr+euoeeQhuS4reW/g+WQieael+ecgeWwseS4muezu+e7n+ehrOS7tuW7uuiuvijlhazlvIDmi5vmoIcpZeWQieael+ecgeS6uuWKm+i1hOa6kOWSjOekvuS8muS/nemanOS/oeaBr+euoeeQhuS4reW/g+WQieael+ecgeWwseS4muezu+e7n+ehrOS7tuW7uuiuvijlhazlvIDmi5vmoIcpDOaUv+W6nOmHh+i0rRFb55yB6YeH6LSt5Lit5b+DXQoyMDE3LTAxLTE5ZAIPD2QWAmYPFQcCMTYkYzgxNGE4YzAtYTY0MS00ZmVlLWJmOTAtMmFiYjgyYzg2N2E5PuWQieael+ecgemVv+aYpeael+WMuuS4ree6p+azlemZouWKnuWFrOiuvuWkhyjnq57kuonmgKfosIjliKQpPuWQieael+ecgemVv+aYpeael+WMuuS4ree6p+azlemZouWKnuWFrOiuvuWkhyjnq57kuonmgKfosIjliKQpDOaUv+W6nOmHh+i0rRFb55yB6YeH6LSt5Lit5b+DXQoyMDE3LTAxLTE5ZAIQD2QWAmYPFQcCMTckZmNiYzFjNmUtODM2YS00YTI3LTk3YzYtMGFkODYyYWU5NzJmO+mVv+aYpeWkp+WtpuWkp+WtpueUn+a0u+WKqOS4reW/g+iuvuaWveaUuemAoCjlhazlvIDmi5vmoIcpO+mVv+aYpeWkp+WtpuWkp+WtpueUn+a0u+WKqOS4reW/g+iuvuaWveaUuemAoCjlhazlvIDmi5vmoIcpDOaUv+W6nOmHh+i0rRFb55yB6YeH6LSt5Lit5b+DXQoyMDE3LTAxLTE5ZAIRD2QWAmYPFQcCMTgkNDc1ZWNlOWItZGI5Yy00NTgyLTljZDktZmIzNGRmYjg5MjNjTuWbm+W5s+W4guiCsuaWh+WtpuagoeW6t+WkjeWZqOadkOWPiuiuvuWkh+S7quWZqOmhueebruernuS6ieaAp+iwiOWIpOmCgOivt+WHvU7lm5vlubPluILogrLmloflrabmoKHlurflpI3lmajmnZDlj4rorr7lpIfku6rlmajpobnnm67nq57kuonmgKfosIjliKTpgoDor7flh70M5pS/5bqc6YeH6LStC1vpk4HkuJzljLpdCjIwMTctMDEtMTlkAhIPZBYCZg8VBwIxOSQ0YTcwYzJkNy0zYjk3LTQ1M2YtODdiYi02ZTlhNWE3NGJmOWFL5Yac5a6J5Y6/6buE6bG85ZyI5Lmh6L+e5LiJ5Z2R5p2R6LSi5pS/5LiT6aG55om26LSr6LWE6YeR5YW754mb5bu66K6+6aG555uuS+WGnOWuieWOv+m7hOmxvOWciOS5oei/nuS4ieWdkeadkei0ouaUv+S4k+mhueaJtui0q+i1hOmHkeWFu+eJm+W7uuiuvumhueebrgzmlL/lupzph4fotK0LW+WGnOWuieWOv10KMjAxNy0wMS0xOWQCEw9kFgJmDxUHAjIwJDY2YTBhODlmLWU1YTEtNGY2Mi1iODc4LTQ2MTljMzQxZmM1NV3mibbkvZnluILlhajmsJHlu7rouqvmtLvliqjkuK3lv4Plu7rorr7pobnnm67pgInmi6nmlL/lupzotK3kubDmnI3liqHmib/mjqXkuLvkvZPph4fotK3pobnnm65d5om25L2Z5biC5YWo5rCR5bu66Lqr5rS75Yqo5Lit5b+D5bu66K6+6aG555uu6YCJ5oup5pS/5bqc6LSt5Lmw5pyN5Yqh5om/5o6l5Li75L2T6YeH6LSt6aG555uuDOaUv+W6nOmHh+i0rQtb5om25L2Z5Y6/XQoyMDE3LTAxLTE5ZAIUD2QWAmYPFQcCMjEkNDNkNzdiOTQtMzIwNi00ODg4LWI1YjYtOTY4ZjZhOGI0ZmVmUeWbm+W5s+W4gumTgeilv+WMuui0ouaUv+WxgOWbveW6k+aUr+S7mOezu+e7n+i9r+S7tuWNh+e6p+WNleS4gOadpea6kOmHh+i0reWFrOekulHlm5vlubPluILpk4Hopb/ljLrotKLmlL/lsYDlm73lupPmlK/ku5jns7vnu5/ova/ku7bljYfnuqfljZXkuIDmnaXmupDph4fotK3lhaznpLoM5pS/5bqc6YeH6LStC1vpk4Hopb/ljLpdCjIwMTctMDEtMTlkAhUPZBYCZg8VBwIyMiRjZDg4ZDIyZi0zZGRiLTQ3MGEtYTVlMS1lZWU3NTE1ZDk5ZDkzMjAwNy0yMDE15Yac5a6J5Y6/5Lit5bCP5a2m6KOF5aSH6aG555uu5oub5qCH5YWs5ZGKMzIwMDctMjAxNeWGnOWuieWOv+S4reWwj+WtpuijheWkh+mhueebruaLm+agh+WFrOWRigzmlL/lupzph4fotK0LW+WGnOWuieWOv10KMjAxNy0wMS0xOWQCFg9kFgJmDxUHAjIzJGUxYzQ5YWE2LWZlYzctNGU1NS04OWNiLWEwMjY1MzAyMThiMy3lvrfmg6DluILmoaPmoYjppobmoaPmoYjlr4bpm4bmnrbph4fotK3pobnnm64t5b635oOg5biC5qGj5qGI6aaG5qGj5qGI5a+G6ZuG5p626YeH6LSt6aG555uuDOaUv+W6nOmHh+i0rQtb5b635oOg5biCXQoyMDE3LTAxLTE5ZAIXD2QWAmYPFQcCMjQkY2Q3MWE0M2MtNGFjZC00OTMyLTgxNGItMDA2MjYyMjA2MTg5ZOWGnOWuieWOv+mrmOS4reeQhuWMlueUn+WunumqjOWupOOAgeeJueauiuaVmeiCsuWtpuagoeaVmeWtpuS4juW6t+Wkjeiuree7g+S7quWZqOiuvuWkh+mFjeWkh+mhueebrjFk5Yac5a6J5Y6/6auY5Lit55CG5YyW55Sf5a6e6aqM5a6k44CB54m55q6K5pWZ6IKy5a2m5qCh5pWZ5a2m5LiO5bq35aSN6K6t57uD5Luq5Zmo6K6+5aSH6YWN5aSH6aG555uuMQzmlL/lupzph4fotK0LW+WGnOWuieWOv10KMjAxNy0wMS0xOWQCGA9kFgJmDxUHAjI1JGI0MjgyYmZiLWZkMTYtNGUxMC05NTU4LWYzY2EyNTVmNzcyZDblhazkuLvlsq3luILlhazlronmtojpmLLlpKfpmJ/lrpjlhbXokKXmiL/kv67nvK7pobnnm6425YWs5Li75bKt5biC5YWs5a6J5raI6Ziy5aSn6Zif5a6Y5YW16JCl5oi/5L+u57yu6aG555uuDOaUv+W6nOmHh+i0rQ5b5YWs5Li75bKt5biCXQoyMDE3LTAxLTE5ZAIIDw8WBB4LUmVjb3JkY291bnQCyJcBHg5DdXN0b21JbmZvVGV4dAWUAeiusOW9leaAu+aVsO+8mjxmb250IGNvbG9yPSJibHVlIj48Yj4xOTQwMDwvYj48L2ZvbnQ+IOaAu+mhteaVsO+8mjxmb250IGNvbG9yPSJibHVlIj48Yj43NzY8L2I+PC9mb250PiDlvZPliY3pobXvvJo8Zm9udCBjb2xvcj0icmVkIj48Yj4xPC9iPjwvZm9udD5kZGTE41VFJlRfeIwbq97SXby7ScDGTg==;__EVENTVALIDATION@/wEWMwLbwefOAgL83OyQCQL4m5eLAQL1m5eLAQL2m5eLAQL7m5eLAQL8m5eLAQL5m5eLAQL6m5eLAQL8q8EOAo/37/sBAo/3w6YIAo/3t40DAo/3q+gLAo/3n9cCAo/387MFAo/3554MAo/328UEAo/3j50KAo/34/kCAuiY8tYPAuiY5r0GAsWBxLkGAsWBuOQOAuzRsusGAp3kj+UKAsHc7JAJAsWbl4sBAsibl4sBAsubl4sBAsabl4sBAsGbl4sBAsSbl4sBAsebl4sBAp7kj+UKAsOrwQ4CsPfv+wECsPfDpggCsPe3jQMCsPer6AsCsPef1wICsPfzswUCsPfnngwCsPfbxQQCsPePnQoCsPfj+QIC15jy1g8C15jmvQYC+oHEuQYC+oG45A4Cn+SP5QpMBd9qA9WQgsY3KTmdbcKgsu3gyw==";
		list=fmtStr(aaaa);
//		try {
////			html=ht.getHtml("http://ggzyjy.jl.gov.cn/JiLinZtb/Default.aspx", map, 1, 1);
//			resultMap=ht.getHtml(url, map, 1, 1);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		{"website":"吉林省公共资源交易信息网---招标公告","pageType":"jilin_gg_zhaobiao_list","cookie":"ASP.NET_SessionId=2bsg0hmg4j3uvmmb4y5mbw55; path=/; HttpOnly","bAjax":"false","isGetMethod":"false","bProxy":"true","page":2,"url":"http://ggzyjy.jl.gov.cn/JiLinZtb/Template/Default/MoreInfoJYXX.aspx?CategoryNum=004001"} curPageNum:2
//		System.out.println(resultMap.get("setCookie"));
		map.put("Cookie","ASP.NET_SessionId=2bsg0hmg4j3uvmmb4y5mb5; path=/; HttpOnly");
//		Document doc=Jsoup.parse(resultMap.get("html"));
//		String __VIEWSTATE=doc.select("input[name=__VIEWSTATE]").get(0).attr("value");
//		String __EVENTVALIDATION=doc.select("input[name=__EVENTVALIDATION]").get(0).attr("value");
//		System.out.println(__VIEWSTATE);
//		System.out.println(__EVENTVALIDATION);
//		list.add(new BasicNameValuePair("__EVENTTARGET", "Pager"));
//		list.add(new BasicNameValuePair("__EVENTARGUMENT", "3"));
//		list.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));
//		list.add(new BasicNameValuePair("__EVENTVALIDATION",__EVENTVALIDATION));
		try {
			html=ht.postHtml(url, map, list, 1, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(html);
		
	}
	
	public static ArrayList<NameValuePair> fmtStr(String param) {
		ArrayList<NameValuePair> listParams = new ArrayList<NameValuePair>() ;
		try{
			// <expression><![CDATA["keyword@"+$KEYVAL(task,"keyword")+";searchtype@0;objectType@2;dataType@1;page@"+(page+1)]]></expression>
			if(param!=null){//write by txw
				if(param.contains(";")){
					String[] liststr=param.split(";");
					for(int i=0;i<liststr.length;i++){
						String keystr=liststr[i];
						if(keystr.contains("@")){
							listParams.add(new BasicNameValuePair(keystr.split("@")[0],keystr.split("@")[1]));
						}
					}
				}else{
					if(param.contains("@")){
						listParams.add(new BasicNameValuePair(param.split("@")[0],param.split("@")[1]));
					}
				}
			}
			}catch(Exception e){
//				e.printStackTrace();
			}
		return listParams;
		}
	
}
