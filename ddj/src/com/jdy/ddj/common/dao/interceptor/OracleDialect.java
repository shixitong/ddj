package com.jdy.ddj.common.dao.interceptor;



/**
 *
 */

public class OracleDialect extends Dialect {

	public String getLimitString(String sql, int offset, int limit) {

		sql = sql.trim();

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		pagingSelect.append("select * from (select haf.*,rownum rn from (  ");
		pagingSelect.append(sql);

		pagingSelect.append(") haf) haf_tmp where haf_tmp.rn > ");
        pagingSelect.append(offset);
        pagingSelect.append(" and haf_tmp.rn <=  ");
        pagingSelect.append(offset + limit);

		return pagingSelect.toString().replaceAll("\n","");
	}
}
