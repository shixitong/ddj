package com.jdy.ddj.common.dao.interceptor;


import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.utils.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.PropertyException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class})})
public class PagePlugin implements Interceptor {
    private Dialect dialectObj;
    private static Logger logger = LoggerFactory.getLogger(PagePlugin.class);
	private static String dialect = "";	//数据库方言
	private static String pageSqlId = ""; //mapper.xml中需要拦截的ID(正则匹配)
	
	public Object intercept(Invocation ivk) throws Throwable {
		if(ivk.getTarget() instanceof RoutingStatementHandler){
			RoutingStatementHandler statementHandler = (RoutingStatementHandler)ivk.getTarget();
			BaseStatementHandler delegate = (BaseStatementHandler) Reflections.getFieldValue(statementHandler, "delegate");
			MappedStatement mappedStatement = (MappedStatement)Reflections.getFieldValue(delegate, "mappedStatement");
			
			if(mappedStatement.getId().matches(pageSqlId)){ //拦截需要分页的SQL
				BoundSql boundSql = delegate.getBoundSql();
				Object parameterObject = boundSql.getParameterObject();//分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
				if(parameterObject==null){
					throw new NullPointerException("parameterObject尚未实例化！");
				}else{
                    //生成查询总数语句,并获取总数
					Connection connection = (Connection) ivk.getArgs()[0];
					String sql = boundSql.getSql();
                    StringBuilder countSql = new StringBuilder();
                    countSql.append("select count(1) from (").append(sql).append(") tmp_count");
					PreparedStatement countStmt = connection.prepareStatement(countSql.toString());
					//BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(),countSql.toString(),boundSql.getParameterMappings(),parameterObject);

                    //使用DefaultParameterHandler来对语句进行预处理
                    DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(mappedStatement,parameterObject,boundSql);
                    defaultParameterHandler.setParameters(countStmt);
					ResultSet rs = countStmt.executeQuery();
					long count = 0;
					if (rs.next()) {
						count = rs.getLong(1);
					}
					rs.close();
					countStmt.close();
					//System.out.println(count);
					RequestBean requestBean = null;
					if(parameterObject instanceof RequestBean){	//参数就是Page实体
                        requestBean = (RequestBean) parameterObject;
						requestBean.setCount(count);
					}else if(parameterObject instanceof Map){
                        requestBean = (RequestBean)((Map) parameterObject).get("requestBean");
                        requestBean.setCount(count);
                    }else{	//参数为某个实体，该实体拥有Page属性
                        Field pageField = Reflections.getAccessibleField(parameterObject, "requestBean");
                        if(pageField!=null){
                            requestBean = (RequestBean) Reflections.getFieldValue(parameterObject, "requestBean");
                            if(requestBean ==null)
                                requestBean = new RequestBean();

                            requestBean.setCount(count);
                            Reflections.setFieldValue(parameterObject, "requestBean", requestBean); //通过反射，对实体对象设置分页对象
                        }else{
                            throw new NoSuchFieldException(parameterObject.getClass().getName()+"不存在 requestBean 属性！");
                        }
                    }
                    String pageSql = dialectObj.getLimitString(sql, requestBean.getOffset(), requestBean.getPageSize());
                    Reflections.setFieldValue(boundSql, "sql", pageSql);//将分页sql语句反射回BoundSql.
                    if(logger.isDebugEnabled()){
                        logger.debug("生成分页SQL : " + boundSql.getSql());
                    }
				}
			}
		}

		return ivk.proceed();
	}


    /**
     * 根据条件创建方言处理器
     */
	private void generateDialect(){
        Dialect.Type databaseType =  Dialect.Type.valueOf(dialect);
        switch(databaseType){
            case DB2:
                dialectObj = new DB2Dialect();
                break;
            case ORACLE:
                dialectObj = new OracleDialect();
                break;
            default:
                dialectObj = new DefaultDialect();
        }
	}
	
	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
	}

	public void setProperties(Properties p) {
		dialect = p.getProperty("dialect");
		if (StringUtils.isEmpty(dialect)) {
			try {
				throw new PropertyException("dialect property is not found!");
			} catch (PropertyException e) {
				e.printStackTrace();
                logger.error("没有mybatis方言属性,拦截错误:",e);
			}
		}
		pageSqlId = p.getProperty("pageSqlId");
		if (StringUtils.isEmpty(pageSqlId)) {
			try {
				throw new PropertyException("pageSqlId property is not found!");
			} catch (PropertyException e) {
				e.printStackTrace();
                logger.error("没有mybatis分页拦截方法属性,拦截错误:",e);
			}
		}
        generateDialect();
        if(dialectObj == null){
            logger.error("初始化分页插件方言错误!");
            throw  new RuntimeException("初始化分页插件方言错误!");
        }
	}
	
}
