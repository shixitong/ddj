package com.jdy.ddj.common.dao.interceptor;

public abstract class Dialect {

	public static enum Type{
		MYSQL,
		ORACLE,
        DB2
	}
	
	public abstract String getLimitString(String sql, int skipResults,int pageSize);
	
}
