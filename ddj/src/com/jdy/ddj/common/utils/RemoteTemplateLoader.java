package com.jdy.ddj.common.utils;
/**
 *
 */

import freemarker.cache.URLTemplateLoader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 自定义远程模板加载器，用来加载远程机器上存放的模板文件，比如FTP，Handoop等上的模板文件
 *
 * @author Administrator
 */
public class RemoteTemplateLoader extends URLTemplateLoader {
    //远程模板文件的存储路径（目录）
    private String remotePath;

    public RemoteTemplateLoader(String remotePath) {
        if (remotePath == null) {
            throw new IllegalArgumentException("remotePath is null");
        }
        this.remotePath = canonicalizePrefix(remotePath);
        if (this.remotePath.indexOf('/') == 0) {
            this.remotePath = this.remotePath.substring(this.remotePath.indexOf('/') + 1);
        }
    }

    @Override
    protected URL getURL(String name) {
        name = name.replace("_zh_CN","");//去掉freemarker默认将name加上_zh_CN。
        String fullPath = this.remotePath + name;
        /*if ((this.remotePath.equals("/")) && (!isSchemeless(fullPath))) {
            return null;
        }
        if (this.remotePath.indexOf("streamFile") == -1 && this.remotePath.indexOf("webhdfs") != -1)//这个是针对不直接使用文件流形式进行访问和读取文件而使用的格式
        {
            fullPath = fullPath + "?op=OPEN";
        }*/
        URL url = null;
        try {
            url = new URL(fullPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    private static boolean isSchemeless(String fullPath) {
        int i = 0;
        int ln = fullPath.length();

        if ((i < ln) && (fullPath.charAt(i) == '/')) i++;

        while (i < ln) {
            char c = fullPath.charAt(i);
            if (c == '/') return true;
            if (c == ':') return false;
            i++;
        }
        return true;
    }
}
