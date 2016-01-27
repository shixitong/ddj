package com.jdy.ddj.common.dao.interceptor;



/**
 * 
 * @author tony.li
 *
 */

public class DB2Dialect extends Dialect {

	public String getLimitString(String sql, int offset, int limit) {

		sql = sql.trim();

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		pagingSelect.append("select * from (select haf.*,rownumber() over() as rowid from (  ");
		pagingSelect.append(sql);

		pagingSelect.append(") haf) haf_tmp where haf_tmp.rowid > ");
        pagingSelect.append(offset);
        pagingSelect.append(" and haf_tmp.rowid <=  ");
        pagingSelect.append(offset + limit);

		return pagingSelect.toString().replaceAll("\n","");
	}
}
