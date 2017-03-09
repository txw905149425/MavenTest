//package com.test.MongoMaven.txw;
//
//import java.io.StringReader;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.jsoup.helper.StringUtil;
//import org.wltea.analyzer.core.IKSegmenter;
//import org.wltea.analyzer.core.Lexeme;
//
//import com.test.MongoMaven.db.MysqlCollection;
//
//public class SplitKey2Mysql {
//	
//	public static void main(String[] args) {
//		MysqlCollection  mySQLcoll = new MysqlCollection(); 
//		Connection con= mySQLcoll.connSQL();
//		String query="select * from a_tyc_reparse_keyword";
//		String update="insert into a_a_keyword(keyword) values(?)";
//		ResultSet rs=querySql(query,con);
//		try {
//			while(rs.next()){
//				String keyword=rs.getString("keyword");
//				if(!StringUtil.isBlank(keyword)){
////					IKSegmenter ikseg=splitKeywords(keyword);
//				      StringReader reader = new StringReader(keyword);
//				      IKSegmenter ikseg=new IKSegmenter(reader, true);
//					try{
//						 while(true){
//							 Lexeme me = ikseg.next();
//							    if(me == null){
//							         break;
//							    }
//							   String term = me.getLexemeText();
//	//						   System.out.println(term);
//							   if(!StringUtil.isBlank(term)&&term.length()>=2){
//								   updateSql(update,con,term);
//							   }
//							 }
//					}catch(Exception e){
//						e.printStackTrace();
//					}finally{
//						reader.close(); 
//					}
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			mySQLcoll.closeMysql(con);
//		}
//	}
//	
//	public static ResultSet  querySql(String sql,Connection con){
//		ResultSet rs=null;
//		PreparedStatement statement=null;
//		try {
//			statement = con.prepareStatement(sql);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			rs=statement.executeQuery();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return rs;
//	}
//	
//	public static boolean  updateSql(String sql,Connection con,String key) throws SQLException{
//		boolean flag=false;
//		PreparedStatement statement=null;
//		statement = con.prepareStatement(sql);
//		statement.setString(1,key);
//		int num=statement.executeUpdate();
//		if(num>0){
//			flag=true;
//		}
//		return flag;
//	}
//	
//	/**
//     * 关键字切分
//     * @param sentence 要分词的句子
//     * @return 返回分词结果
//     */
//    public static IKSegmenter splitKeywords(String sentence) {
////    	ArrayList<String> keys = new ArrayList<String>();
//    	 IKSegmenter ikseg=null;
//        if(!StringUtil.isBlank(sentence))  {
//            StringReader reader = new StringReader(sentence);
//             ikseg = new IKSegmenter(reader, true);
////             reader.close(); 
//        }
//        return ikseg;
//    }
//  
//	
//}
