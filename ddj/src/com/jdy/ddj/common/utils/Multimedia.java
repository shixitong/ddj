package com.jdy.ddj.common.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 多媒体相关信息
 * @author liudong
 */
public abstract class Multimedia {

	public static boolean isImageFile(String fn){
		String ext = FilenameUtils.getExtension(fn);
		if(StringUtils.isBlank(ext)) return false;
		return ArrayUtils.contains(img_exts, ext.toLowerCase());
	}
	
	public static boolean isAudioFile(String fn){
		String ext = FilenameUtils.getExtension(fn);
		if(StringUtils.isBlank(ext)) return false;
		return ArrayUtils.contains(audio_exts, ext.toLowerCase());
	}
	
	public static boolean isVideoFile(String fn){
		String ext = FilenameUtils.getExtension(fn);
		if(StringUtils.isBlank(ext)) return false;
		return ArrayUtils.contains(video_exts, ext.toLowerCase());
	}
	
	public static boolean isFlashFile(String fn){
		String ext = FilenameUtils.getExtension(fn);
		if(StringUtils.isBlank(ext)) return false;
		return ArrayUtils.contains(flash_exts, ext.toLowerCase());
	}
	
	public static boolean isDocumentFile(String fn) {
		String ext = FilenameUtils.getExtension(fn);
		if(StringUtils.isBlank(ext)) return false;
		return ArrayUtils.contains(docs_exts, ext.toLowerCase());
	}
	
	/**
	 * 所有支持的MIME TYPES,其他格式的图片不做支持
	 */
	public final static String[] img_exts = new String[]{"gif","jpg","jpeg","png","bmp"};

	/**
	 * 所有支持的视频MIME TYPES
	 */
	public final static String[] video_exts = new String[]{"avi","rm","3gp","wmv","mpg","asf"};

	/**
	 * 所有支持的音频MIME TYPES
	 */
	public final static String[] audio_exts = new String[]{"wma","mp3","arm","mid","aac","imy"};
	
	/**
	 * Flash动画
	 */
	public final static String[] flash_exts = new String[]{"swf"};

	/**
	 * 文档类型
	 */
	public final static String[] docs_exts = new String[]{"txt","htm","html","pdf","doc","rtf","xls","ppt"};

	public final static HashMap<String, String> mime_types = new HashMap<String, String>()
	{{
		put("jar","application/java-archive");
		put("jad","text/vnd.sun.j2me.app-descriptor");
		put("sis","application/vnd.symbian.install");
		put("sisx","x-epoc/x-sisx-app");
		put("thm","application/vnd.eri.thm");
		put("nth","application/vnd.nok-s40theme");
		put("zip","application/zip");
		put("rar","application/octet-stream");
		put("cab","application/octet-stream");
		
		put("gif","image/gif");
		put("jpg","image/jpeg");
		put("jpeg","image/jpeg");
		put("png","image/png");
		put("bmp","image/bmp");

		put("avi","video/x-msvideo");
		put("rm","application/vnd.rn-realmedia"); 
		put("3gp","video/3gpp");
		put("wmv","video/x-ms-wmv");
		put("mpg","video/mpg");
		put("asf","video/x-ms-asf");
		put("flv","video/x-flv");
		put("mp4","video/mp4");

		put("wma","audio/x-ms-wma"); 
		put("mp3","audio/mp3");
		put("arm","audio/amr");
		put("mid","audio/x-midi");
		put("aac","audio/aac");
		put("imy","audio/imelody");

		put("swf", "application/x-shockwave-flash");

		put("txt","text/plain");
		put("htm","text/html");
		put("html","text/html");
		put("pdf","application/pdf");
		put("doc","application/msword");
		put("rtf","application/msword");
		put("docx","application/msword");
		put("xls","application/vnd.ms-excel");
		put("ppt","application/vnd.ms-powerpoint");
		put("xlsx","application/vnd.ms-excel");
		put("pptx","application/vnd.ms-powerpoint");
		put("chm","application/octet-stream");
	}};

}
