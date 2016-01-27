package com.jdy.ddj.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public final class V5Utils {

    private V5Utils() {
    }

    /**
     * 使用MD5加密字符串
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        if (str == null) {
            return "";
        }
        return DigestUtils.md5Hex(str);
    }

    /**
     * 随即返回字符串
     *
     * @param charCount 字符串长度
     * @return
     */
    public static String randAlpha(int charCount) {
        return RandomStringUtils.randomAlphanumeric(charCount);
    }

    /**
     * 返回[from,to)之间的一个随机整数
     *
     * @param from 起始值
     * @param to   结束值
     * @return [from, to)之间的一个随机整数
     */
    public static int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

    public static void setFileDownloadHeader(HttpServletRequest request,
                                             HttpServletResponse response,
                                             File file,
                                             String saveName) throws UnsupportedEncodingException {
        if(file==null){
            throw new RuntimeException("传入数据异常");
        }
        String fileName = file.getName();
        //String encodedfileName = new String(saveName.getBytes(), "ISO8859-1");
        response.setContentLength((int) file.length());
        String ext = FilenameUtils.getExtension(fileName);
        String mine_type = Multimedia.mime_types.get(ext);
        if(mine_type != null){
            response.setContentType(mine_type);
        }
        String ua = request.getHeader("user-agent");

        String new_filename = URLEncoder.encode(saveName, "UTF8");
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的

        if(ua != null && ua.indexOf("Firefox")>=0){
            response.setHeader("Content-Disposition","attachment; filename*=\"UTF-8''" + new_filename +"\"");
        }else{
            response.setHeader("Content-Disposition", "attachment; filename=" + new_filename);
        }

    }

    public static void setFileDownloadHeader(HttpServletResponse response,
                                             HttpServletRequest request,
                                             String fileName,
                                             String saveName) throws UnsupportedEncodingException {

        String ext = FilenameUtils.getExtension(fileName);
        String mine_type = Multimedia.mime_types.get(ext);

        if (mine_type != null) {
            response.setContentType(mine_type);
        }
        String ua = request.getHeader("user-agent");

        String new_filename = URLEncoder.encode(saveName, "UTF8");
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的

        if (ua != null && ua.indexOf("Firefox") >= 0) {
            response.setHeader("Content-Disposition", "attachment; filename*=\"UTF-8''" + new_filename + "\"");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=" + new_filename);
        }
    }

}
