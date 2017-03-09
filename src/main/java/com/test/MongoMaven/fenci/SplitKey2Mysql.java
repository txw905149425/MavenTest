package com.test.MongoMaven.fenci;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.helper.StringUtil;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.test.MongoMaven.db.MysqlCollection;

public class SplitKey2Mysql {
	static String reg="(.*[A-Za-z]+.*)|(.*\\d+.*)|([`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？])";
	public static void main(String[] args) {
		String j=args[0];
		int ss=Integer.parseInt(j);
//		System.out.println(isUpdate("aa业"));
		MysqlCollection  mySQLcoll = new MysqlCollection(); 
		Connection con= mySQLcoll.connSQL();
		int size=querySize("a_a_company_clean",con);
		String update="insert into a_a_keyword_copy(keyword) values(?)";
//		String query="select * from a_a_company_clean limit 0,10000";
		int num=1;
		if(size%10000==0){
			num=size/10000;
		}else{
			num=size/10000+1;
		}
		StringReader reader=null;
		Lexeme me=null;
		IKSegmenter ikseg=null;
		ResultSet rs=null;
		int aa=0;
		try {
			for(int i=ss;i<num;i++){
				aa=i;
				int limit=i*10000;
				String query="select * from a_a_company_clean limit "+limit+",10000";
				rs=querySql(query,con);
					while(rs.next()){
						String keyword=rs.getString("company_name");
						if(!StringUtil.isBlank(keyword)){
						      	reader = new StringReader(keyword);
						      	ikseg=new IKSegmenter(reader, true);
								 while((me=ikseg.next())!=null){
								   String term = me.getLexemeText();
								   if(!StringUtil.isBlank(term)&&term.length()>=2&&isUpdate(term)){
									   boolean ggg=updateSql(update,con,term);
									   if(ggg){
										   System.out.println("succed :  "+term);
									   }else{
										   System.out.println("failed :  "+term);
									   }
								   }
								}
							 reader.close(); 
						}
					}
				rs.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.out.println("出错之前的i为：  "+aa);
		}
		
			
			mySQLcoll.closeMysql(con);
	}
	
	public static ResultSet  querySql(String sql,Connection con){
		ResultSet rs=null;
		PreparedStatement statement=null;
		try {
			statement = con.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs=statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public static int  querySize(String table,Connection con){
		int num=0;
		ResultSet rs=null;
		PreparedStatement statement=null;
		String sql="select count(*) as num from "+table;
		try {
			statement = con.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs=statement.executeQuery();
			rs.next();
			num=rs.getInt("num");
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return num;
	}
	
	public static boolean  updateSql(String sql,Connection con,String key) {
		boolean flag=false;
		try{
			PreparedStatement statement=null;
			statement = con.prepareStatement(sql);
			statement.setString(1,key);
			int num=statement.executeUpdate();
			if(num>0){
				flag=true;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
     * 关键字切分
     * @param sentence 要分词的句子
     * @return 返回分词结果
     */
    public static IKSegmenter splitKeywords(String sentence) {
//    	ArrayList<String> keys = new ArrayList<String>();
    	 IKSegmenter ikseg=null;
        if(!StringUtil.isBlank(sentence))  {
            StringReader reader = new StringReader(sentence);
             ikseg = new IKSegmenter(reader, true);
//             reader.close(); 
        }
        return ikseg;
    }
    
   public static boolean isUpdate(Object company_name){
   	 boolean flag=true;
   	 if(company_name==null||company_name.toString()==""){
   		 return false;
   	 }
   	 String name=company_name.toString();
   	 name=name.replaceAll("\\s*","");
		  Pattern p =Pattern.compile(reg);
		  Matcher m = p.matcher(name);
			if (m.find()) {
				flag=false;
//				System.out.println(m.group());
			}
   	 return flag;
    }
   
 
	
}
