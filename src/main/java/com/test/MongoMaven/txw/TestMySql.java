package com.test.MongoMaven.txw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.test.MongoMaven.db.MysqlCollection;

public class TestMySql {
  
	public static void main(String[] args) {
		
		MysqlCollection conn=new MysqlCollection();
		Connection con=conn.connSQL();
		
		
	}
	public void test(Connection con,String sql){
		PreparedStatement preState=null;
		try {
			preState=con.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		preState.set
		
	}
	
	
}
