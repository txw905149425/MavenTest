package com.test.MongoMaven.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * mySqlҵ����

 *
 */
public class DBMySql extends DataBase {
	public DBMySql(String url, String user, String password) {
		String dbUrl = url;
		String theUser = user;
		String thePw = password;
		String driverName = "com.mysql.jdbc.Driver";
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(dbUrl, theUser, thePw);
		} catch (Exception e) {
			e.printStackTrace();
			conn = null;
		}	 
	}
	
	@Override
	public long insertWithAutoId(String sql, String mainColumn) throws Exception {
		long retID = 0;
		return retID;
	}

	@Override
	public Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		return null;
	}

}
