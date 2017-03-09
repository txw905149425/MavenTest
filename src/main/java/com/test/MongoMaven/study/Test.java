package com.test.MongoMaven.study;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
  public static void main(String[] args) {
	  String reg="ni(.*)h(他.*)";
	 Pattern p=Pattern.compile(reg);
	  String text="ni是豬么？h他是的。";
	  Matcher m=p.matcher(text);
	  if(m.find()){
		  System.out.println("0"+m.group(0));
		  System.out.println("1:"+m.group(1));
		  System.out.println("2:"+m.group(2));
		  
	  }
	  
  }
}
