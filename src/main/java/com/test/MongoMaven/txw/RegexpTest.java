package com.test.MongoMaven.txw;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.test.MongoMaven.uitil.FileUtil;

public class RegexpTest {
	
	  public static void main(String[] args) {
		  ArrayList<String> list=FileUtil.readFileReturn("d:/a_a_company_name.txt");
//		  ArrayList<String> list=FileUtil.readFileReturn("d:/keyword.txt");
//		  ArrayList<String> list1=FileUtil.readFileReturn("d:/contains.txt");
//		  String regexp="(^[A-Za-z0-9]|+$)|";
//		  for(String str:list){
//			  regexp+="(.*"+str+"$)|";
//		  }
//		  for(String str:list1){
//			  regexp+="(.*"+str+".*)|";
//		  }
//		  String exp=regexp.substring(0, regexp.length()-1);
//		  System.out.println(exp);
//		  for(String str:list){
//			  String exp="(^[A-Za-z0-9]+$)|(.*社$)|(.*站$)|(.*行$)|(.*园$)|(.*店$)|(.*所$)|(.*庄 $)|(.*部$)|(.*市$)|(.*屋$)|(.*场$)|(.*摊$)|(.*通讯$)|(.*点$)|(.*处$)|(.*中心$)|(.*户$)|(.*坊$)|(.*房$)|(.*基地$)|(.*养殖$)|(.*队$)|(.*吧$)|(.*照明 $)|(.*货运$)|(.*院$)|(.*木艺$)|(.*城$)|(.*ＫＴＶ$)|(.*KTV$)|(.*门窗$)|(.*库$)|(.*服饰$)|(.*服装$)|(.*总汇$)|(.*业$)|(.*部）$)|(.*摊床$)|(.*代办$)|(.*电器 $)|(.*寄賣$)|(.*寄卖$)|(.*铺$)|(.*内衣 $)|(.*经销$)|(.*装潢$)|(.*馆$)|(.*斋$)|(.*大棚$)|(.*组$)|(.*班$)|(.*间$)|(.*家私$)|(.*柜$)|(.*分厂$)|(.*团$)|(.*画廊$)|(.*会$)|(.*苗圃$)|(.*阁$)|(.*运输$)|(.*村$)|(.*分公司.*)|(.*经销部.*)|(.*电站.*)|(.*分厂.*)";
////			  String tmp=" 81F31EC49DBB799BD714EB33A7F17E6C ]";  // [ ]
//			  if(str.contains("]")){
//				  str=str.replace("]","").replace("[", "");
//			  }
//			  str=str.replaceAll("\\s*","");
//			  Pattern p =Pattern.compile(exp);
//			  Matcher m = p.matcher(str);
//				if (m.find()) {
//					System.out.println(m.group());
//					
//				}else{
//					System.out.println("......");
//				}
//				System.out.println("****************\\n*****");
//		  }		
		  
		  
		  /*
		   * 过滤关键词
		   * 1.包含英文、数字
		   * 2.去掉（）和里面内容，
		   * 3.去掉（）后内容为空
		   * 4.去掉【】和里面内容
		   * 5.特殊符号 *  .  + - _
		   * 
		   */
		  
		  
		  
	}
	  public static  void test(){
		  String str="81F31EC49DBB799BD714EB33A7F17E6C";
		  String regexp="(^[A-Za-z0-9]+$)";
		  Pattern p =Pattern.compile(regexp);
		  Matcher m = p.matcher(str);
			if (m.find()) {
				System.out.println(m.group());
				
			}else{
				System.out.println("......");
			}
	  }
} 
