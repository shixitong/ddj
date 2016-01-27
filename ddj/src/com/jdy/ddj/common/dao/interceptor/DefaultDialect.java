package com.jdy.ddj.common.dao.interceptor;


public class DefaultDialect extends Dialect {

	public String getLimitString(String sql, int offset, int limit) {

        sql = sql.trim();

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
       // pagingSelect.append("select * from (  ");
        pagingSelect.append(sql);
       // pagingSelect.append(" ) haf_tmp where haf_tmp.id limit ");
        pagingSelect.append(" limit ");
        pagingSelect.append(offset);
        pagingSelect.append(",");
        pagingSelect.append(limit);

        return pagingSelect.toString().replaceAll("\n","");

		//return sql;
	}
}
