package com.test.MongoMaven.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class DBStatement {
	public static final String SQL_DATE_FORMAT = "yyyy-mm-dd hh24:mi:ss";
	public DataBase db = null;
	protected boolean pooled;

	//��ͬ�Ĺ�����ʵ��
	public DBStatement(DataBase value) {
		this.db = value;
		pooled = true;
	}
	public DBStatement(DataBase db, boolean pooled) {
		this.db = db;
		this.pooled = pooled;
	}
	
	public boolean isPooled() {
		return this.pooled;
	}

	public void setPooled(boolean value) {
		this.pooled = value;
	}

	protected PreparedStatement pstmt = null;

	public void close() {
		try {
			if (pstmt != null && !pooled)
				pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
		}
	}

	/**
	 * ��ѯ
	 * ������
	 */
	public ResultSet executeQuery(String sql, Object[] params) throws Exception {
		try {
			if (pooled)
				pstmt = db.getPooledPrepare(sql);
			else
				pstmt = db.createPrepare(sql);
			if (params != null)
				for (int i = 0; i < params.length; i++)
					preparedStatementSet(pstmt, params[i], (i + 1));
			return pstmt.executeQuery();
		} catch (SQLException e) {
			close();
			throw new RuntimeException(e.getMessage());
		}
	}
	public ResultSet executeQuery(String sql) throws Exception {
		try {
			pstmt = db.createPrepare(sql);
			return pstmt.executeQuery();
		} catch (SQLException e) {
			close();
			throw new RuntimeException(e.getMessage());
		}
	}

	public ResultSet executeQuery(String sqlStub, Map<String, Object> params) throws Exception {
		String expStr = "";
		Object param;
		List<Object> l = new ArrayList<Object>();
		int i = 0;
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		for (Object field : keys) {
			param = params.get(field);
			if (param != null) {
				if (i > 0)
					expStr += " and ";
				expStr += field + " = " + convertParamToQuestion(param);
				l.add(convertParamType(param));
				i++;
			}
		}
		// ��һû�в���
		if (i == 0)
			expStr = " 1 = 1 ";
		String sql = String.format(sqlStub, expStr);
		return executeQuery(sql, l.toArray());
	}

	/**
	 * ����
	 * 
	 * @param sql
	 * @param params          ��ӦprepareStatement�Ĳ�����װ
	 * @return
	 * @throws SQLException
	 * 
	 */
	public int executeUpdate(String sql, Object[] params) throws Exception {
	//	try {
			if (pooled){
				pstmt = db.getPooledPrepare(sql);
				}else{
				pstmt = db.createPrepare(sql);
				}
			if (params != null)
				for (int i = 0; i < params.length; i++)
					preparedStatementSet(this.pstmt, params[i], (i + 1));
			return pstmt.executeUpdate();
//		} 
//		catch (SQLException e) {
//			db.conn.rollback();
//			close();
//			throw new RuntimeException(e.getMessage());
//		}
	}

	////////////////  ����ִ��
	PreparedStatement batstmt = null;
	public void prepareBatch(String sql) throws SQLException {
		//db.setAutoCommit(false);
		batstmt = db.createPrepare(sql);
		
	}
	public void addBatch(Object[] params) throws Exception {
		if (params != null)
			for (int i = 0; i < params.length; i++){
			preparedStatementSet(batstmt, params[i], (i + 1));
			}
		      batstmt.addBatch();
	}
	public int[] executeBatch() throws SQLException {
		return batstmt.executeBatch();
	}

	public void closeBatch() throws SQLException {
		batstmt.close();
		batstmt = null;
	}

	
	
	
	/**
	 * ִ�и�����䣨�������Ϊ�գ�
	 * 
	 * ������ String sql = "insert into task(id,%s) values(seq_task.nextval,%s)"; hashmap.put("url",
	 * "http://www.taobao.com"); hashmap.put("updateTime", new java.util.Date()) // Oracle��ΪDate����
	 * hashmap.put("pageType", maybe_null); db.updateWithNullableParam(sql, hashmap); // Ҫ��ΪMap<String, Object>����
	 * 
	 */
	public int executeUpdate(String sqlStub, Map<String, Object> params) throws Exception {

		if (sqlStub.trim().toLowerCase().indexOf("insert") == 0) {
			return executeInsert(sqlStub, params);
		}

		String expStr = "";
		Object param;
		List<Object> list = new ArrayList<Object>();
		int i = 0;
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		for (Object field : keys) {
			param = params.get(field);
			if (param != null) {
				if (i > 0) {
					expStr += ", ";
				}
				expStr += field + " = " + convertParamToQuestion(param);
				list.add(convertParamType(param));
				i++;
			}
		}

		String sql = String.format(sqlStub, expStr);
		return executeUpdate(sql, list.toArray());
	}

	public int executeUpdate(String sqlStub, Map<String, Object> vparams, Map<String, Object> wparams) throws Exception {
		List<Object> list = new ArrayList<Object>();
		String valueStr = "";
		String whereStr = "1=1";
		Object param;
		int i = 0;

		////////////////////////////////////////////// values
		Object[] keys = vparams.keySet().toArray();
		for (Object field : keys) {
			param = vparams.get(field);
			if (param != null) {
				if (i > 0) {
					valueStr += ", ";
				}
				valueStr += field + " = " + convertParamToQuestion(param);
				list.add(convertParamType(param));
				i++;
			}
		}
		////////////////////////////////////////////// where
		keys = wparams.keySet().toArray();
		for (Object field : keys) {
			param = wparams.get(field);
			if (param != null) {
				whereStr += " and ";
				whereStr += field + " = " + convertParamToQuestion(param);
				list.add(convertParamType(param));
				i++;
			}
		}

		String sql = String.format(sqlStub, valueStr, whereStr);
		return executeUpdate(sql, list.toArray());
	}

	//ִ�в������
	public int executeInsert(String sqlStub, Map<String, Object> params) throws Exception {
		String fieldStr = "";
		String paramStr = "";
		Object param;
		java.util.List<Object> l = new java.util.ArrayList<Object>();
		int i = 0;
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		for (Object field : keys) {
			param = params.get(field);
			if (param != null) {
				if (i > 0) {
					fieldStr += ",";
					paramStr += ",";
				}
				fieldStr += field;
				paramStr += convertParamToQuestion(param);
				l.add(convertParamType(param));
				i++;
			}
		}

		String sql = String.format(sqlStub, fieldStr, paramStr);
		return executeUpdate(sql, l.toArray());
	}


	private String convertParamToQuestion(Object param) {
		if (param instanceof java.util.Date)
			return "to_date(?,'" + SQL_DATE_FORMAT + "')";
		else
			return "?";
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Object convertParamType(Object param) {
		if (param instanceof java.util.Date)
			return sdf.format((java.util.Date) param);
		else
			return param;
	}

	/**
	 * ����PreparedStatement�Ĳ���
	 * @throws SQLException
	 * 
	 */
	private void preparedStatementSet(PreparedStatement stmt, Object obj, int index) throws Exception {
		String instance = this.objInstanceToStr(obj);
		if (instance.equals("String")) {
			stmt.setString(index, (String) obj);
		} else if (instance.equals("Integer")) {
			stmt.setInt(index, (Integer) obj);
		} else if (instance.equals("Long")) {
			stmt.setLong(index, (Long) obj);
		} else if (instance.equals("Float")) {
			stmt.setFloat(index, (Float) obj);
		} else if (instance.equals("Double")) {
			stmt.setDouble(index, (Double) obj);
		} else if (instance.equals("Boolean")) {
			stmt.setBoolean(index, (Boolean) obj);
		} else if (instance.equals("Date")) {
			SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
			String t = form.format((Date) obj);
			stmt.setString(index, t);
		} else if (instance.equalsIgnoreCase("java.util.Date")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String t = sdf.format((java.util.Date) obj);
			stmt.setString(index, t);
		}
	}
	
	/**
	 * ����Object���������
	 * �ж϶�������
	 * @param obj
	 * @return ����String���� 
	 */
	private String objInstanceToStr(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof String)
			return "String";
		if (obj instanceof Integer)
			return "Integer";
		if (obj instanceof Long)
			return "Long";
		if (obj instanceof Float)
			return "Float";
		if (obj instanceof Double)
			return "Double";
		if (obj instanceof Boolean)
			return "Boolean";
		if (obj instanceof Date)
			return "Date";
		// �����һ��Date����
		if (obj instanceof java.util.Date)
			return "java.util.Date";
		return "undefined";
	}
}