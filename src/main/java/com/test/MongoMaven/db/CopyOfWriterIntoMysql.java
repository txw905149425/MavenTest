package com.test.MongoMaven.db;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 
 * @author chenjunliang
 */
public class CopyOfWriterIntoMysql  {
	DBStatement stmt = null;
	ResultSet rs = null;
	DataBase database = null;
	private   long mysql_last_commit_time = System.currentTimeMillis();
	private  int mysql_commit_size = 0;
	private final int  MYSQL_COMMIT_SZIE =1000;

	public void saveRecord(HashMap<String, List<HashMap<String, Object>>> result) {
		String dbType = "Mysql";		
//		String dbUrl = "jdbc:mysql://localhost:3306/some";		
		String dbUrl = "jdbc:mysql://172.16.0.115:3306/gdkj";
		String	dbUserName = "root";				
//		String 	dbPassword = "root";	
		String 	dbPassword = "bjgdFristMan115";
		try {
			if(database == null){
			database = DataBase.createConnection(dbType,dbUrl,dbUserName, dbPassword);
			stmt = database.createMyStatement();
			stmt.db.setAutoCommit(false);
			}
		}catch(Exception e){
			e.printStackTrace();
			if (stmt != null)
				stmt.close();
			if (database != null)
				database.close();
		}		
		try {
			for (Entry<String, List<HashMap<String, Object>>> entry : result.entrySet()) {
				String key = entry.getKey();
				List<HashMap<String, Object>> mapList = entry.getValue();
				int cnt = 0;
					for (HashMap<String, Object> map : mapList) {
						String sql = "insert into "+ key + " (";	
						Object[] params = new Object[map.entrySet().size()*2-1+1];
						int i = 0;
						int j = map.entrySet().size();
						String vvs = "";
						String updatestr = "";
						for (Entry<String,Object> em : map.entrySet()){	
							String emkey = em.getKey();
							
								updatestr += (emkey+"=?,");
								params[j] = em.getValue();
								j++;
						
							params[i] = em.getValue();
							sql += (emkey + ",");
							vvs += "?,";						
							i++;							
						}
						sql = sql.substring(0,sql.length()-1);
						vvs = vvs.substring(0,vvs.length()-1);
						updatestr = updatestr.substring(0,updatestr.length()-1);
						sql += ") values(" + vvs + ") ON DUPLICATE KEY UPDATE "+updatestr;
						//stmt.executeUpdate(sql, params);
						cnt++;
						System.out.println(sql);
						stmt.prepareBatch(sql);
						stmt.addBatch(params);
						stmt.executeBatch();
					}
				 	 			
					mysql_commit_size = mysql_commit_size + cnt;
					stmt.db.commit();
					mysql_last_commit_time = System.currentTimeMillis();
					mysql_commit_size = 0;
					stmt.closeBatch();
				
			}	
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
	HashMap<String,Object> map = new 	HashMap<String , Object>();
	map.put("passwd", 1234);
	map.put("name", "admin");
	List<HashMap<String , Object>> list = new ArrayList<HashMap<String , Object>>();
	list.add(map);
	HashMap<String, List<HashMap<String, Object>>>rs = new HashMap<String, List<HashMap<String, Object>>>();
	rs.put("test", list);
	CopyOfWriterIntoMysql copy = new CopyOfWriterIntoMysql();
	copy.saveRecord(rs);
	}

}
