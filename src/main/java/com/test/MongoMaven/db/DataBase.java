package com.test.MongoMaven.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * ���ݿ��������
 * @author chenjunliang
 *
 */
public abstract class DataBase {
	protected Connection conn = null;
	public static DataBase createConnection() {
		String dbtype = "";
		String dbname = "";
		String user = "";
		String password = "";
		return createConnection(dbtype, dbname, user, password);
	}

	public static DataBase createConnection(String dbtype, String dburl, String username, String password) {
		DataBase dbc;
		if (dbtype.equals("Mysql")){
			dbc = new DBMySql(dburl, username, password);
			}
		else{
			dbc = null;
			}
		if (dbc.conn == null){
			return null;
			}
		return dbc;
	}

	//����һ��Ԥ����õ�SQL������
	public PreparedStatement createPrepare(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}
	//���س����SQL������
	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	/**
	 * �趨���� ������֤����������ԣ�
	 * 
	 * @throws SQLException
	 */
	public void setAutoCommit(boolean value) throws SQLException {
		this.conn.setAutoCommit(value);//
	}

	/**
	 * �ύ����
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		this.conn.commit();// �ύJDBC����
		// �ָ�JDBC�����Ĭ���ύ��ʽ
		// this.conn.setAutoCommit(true);
		// this.close();
	}

	//���ݴ����Ԥ����SQL����Ԥ����
	public void submitPrepare(PreparedStatement ps) throws SQLException {
		ps.executeBatch();
		conn.commit();
	}

	public abstract long insertWithAutoId(String sql, String mainColumn) throws Exception;

	public abstract Object getResultSetValue(ResultSet rs, int index) throws SQLException;

	// Ϊ�����Ч�ʣ����ͷ����ӣ��ظ�ʹ�ã���Ҫ�ֶ��ر�����
	// ʵ��Ϊ����ģʽ���Ա�֤����ռ��̫������	
	private static DataBase sharedConnection;

	public static DataBase getSharedConnection() {
		if (sharedConnection == null || sharedConnection.isClosed()) {
			sharedConnection = DataBase.createConnection();
			sharedConnection.shared = true;
		}
		return sharedConnection;
	}

	private boolean shared = false;

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean value) {
		this.shared = value;
	}

	public void close() {
		if (!shared)
			try {
				for (PreparedStatement pstmt : pstmtPool.values())
					pstmt.close();
				conn.close();
			} catch (Exception e) {
			}
	}

	public boolean isClosed() {
		try {
			return this.conn.isClosed(); // this.conn.isValid(arg0);
		} catch (Exception e) {
			return true;
		}
	}

	// lryh ���my statement����	
	public DBStatement createMyStatement() throws SQLException {
		return new DBStatement(this);
	}

	public DBStatement createMyStatement(boolean pooled) throws SQLException {
		return new DBStatement(this, pooled);
	}

	/**
	 * PreparedStatement�أ���SQL���Ϊ����
	 */
	private Map<String, PreparedStatement> pstmtPool = new WeakHashMap<String, PreparedStatement>();

	public PreparedStatement getPooledPrepare(String sql) {
		PreparedStatement ps = null;
		try {
			ps = pstmtPool.get(sql);
			// Oracle JDBCδʵ��isClosed����
			// if (ps == null || ps.isClosed()) {
			if (ps == null) {
				ps = createPrepare(sql);
				pstmtPool.put(sql, ps);
				//log.info(sql);
				//log.info("���ش�С��" + pstmtPool.size());
				// Oracle JDBCδʵ��setPoolable����
				// ps.setPoolable(true);						
			}
			return ps;
		} catch (SQLException e) {
			
			return null;
		}
	}
}
