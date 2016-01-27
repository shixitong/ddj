package com.jdy.ddj.common.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 文件存储服务
 *
 * @author Winter Lau
 * @date 2010-9-2 上午11:35:56
 */
public class StorageService  {

    private  static Properties properties=new Properties();

    private  static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");

    static{
        try {
            properties = PropertiesLoader.loadProperties("conf/file.properties");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("附件功能初始化错误...");
        }
    }

    public final static StorageService OA_STORAGE = new StorageService("oaUser");

    public final static StorageService ImportOrExportTemplate = new StorageService("ImportOrExportTemplate");

    private String file_path;

    private StorageService(String ext) {
        this.file_path = getWebrootPath()
                + File.separator + "uploads" + File.separator + ext + File.separator + folder()
                + File.separator;
    }


    private String folder(){
        return dateFormat.format(new Date());
    }

    public final static String getWebrootPath() {
/*        String root = StorageService.class.getResource("/").getFile();
        try {
            root = new File(root).getParentFile().getParentFile()
                    .getCanonicalPath();
            root += File.separator;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        String root = properties.getProperty("uploadDir");
        return root;
    }


    public  String getBasePath() {
        return file_path;
    }


    /***
     * 通过指定路径读取文件
     * @param path 文件路径
     * @return
     * @throws java.io.FileNotFoundException
     */
    public File readFileByPath(String path) throws FileNotFoundException {
        return new File(path);
    }

}
