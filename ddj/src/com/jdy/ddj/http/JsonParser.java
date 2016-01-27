package com.jdy.ddj.http;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
* <p>Title:HttpJsonParser</p>
* <p>Description:类的描述</p>
* <p>Company:小鱼快帮</p> 
* @author xushangyu
* @date 2015年7月10日
*/
public class JsonParser {
	private static Logger LOG = Logger.getLogger(JsonParser.class);
	private static JsonParser instance = new JsonParser();
	//线程安全类
	ObjectMapper objectMapper ;
	
	// singleton
	private JsonParser(){
	    objectMapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY);
	    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static JsonParser getInstance() {
		return instance;
	}
	
	public <T> T readStream(InputStream httpstream, Class<T> class_t){
		T tObj = null;
		try {
			tObj = objectMapper.readValue(httpstream, class_t);
		}catch (JsonParseException e) {
			LOG.error("JsonParseException fail: "+e.getMessage());
		} catch (JsonMappingException e) {
			LOG.error("JsonMappingException fail: "+e.getMessage());
		} catch (IOException e) {
			LOG.error("IOException fail: "+e.getMessage());
		}
		
		return tObj;
	}
	
	public <T> T readString(String httpstr, Class<T> class_t){
		T tObj = null;
		try {
			tObj = objectMapper.readValue(httpstr, class_t);
		}catch (JsonParseException e) {
			LOG.error("JsonParseException fail: "+e.getMessage());
		} catch (JsonMappingException e) {
			LOG.error("JsonMappingException fail: "+e.getMessage());
		} catch (IOException e) {
			LOG.error("IOException fail: "+e.getMessage());
		}
		
		return tObj;
	}
	
	public String writeObject(Object obj){
		ByteArrayOutputStream strOutStm = null;
		try {
			strOutStm = new ByteArrayOutputStream();
			objectMapper.writeValue(strOutStm, obj);
		} catch (IOException e) {
			LOG.error("json parse obj2string fail: "+e.getMessage());
		}
		return strOutStm.toString();
	}
	
	public void writeObject(OutputStream output, Object obj){
		try {
			objectMapper.writeValue(output, obj);
		} catch (IOException e) {
			LOG.error("json parse obj2string fail: "+e.getMessage());
		}
	}
	
	public static void main(String[] args){
		JsonParser parser = JsonParser.getInstance();
		parser.readString("", Integer.class);
	}
}
