package com.jdy.ddj.controller.web;


import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * KindEditor JSP
 *
 * 本JSP程序是演示程序，建议不要直接在实际项目中使用。
 * 如果您确定直接使用本程序，使用之前请仔细确认相关安全设置。
 *
 */

@Controller
@RequestMapping("/upload/json/")
public class UploadJsonKindEditor{



    @RequestMapping("index.do")
    public void flowTest(HttpServletRequest request,HttpServletResponse response,Model model)throws Exception{

        try{
                PrintWriter out= response.getWriter();
                //文件保存目录路径
                // PageContext pageContext=request.getServletContext().getPageContext();
                String savePath =request.getSession().getServletContext().getRealPath("/") + "attached/";

                //文件保存目录URL
                String saveUrl  = request.getContextPath() + "/attached/";


        //定义允许上传的文件扩展名
                HashMap<String, String> extMap = new HashMap<String, String>();
                extMap.put("image", "gif,jpg,jpeg,png,bmp");
                extMap.put("flash", "swf,flv");
                extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
                extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

        //最大文件大小
                long maxSize = 1000000;

                response.setContentType("text/html; charset=UTF-8");

                if(!ServletFileUpload.isMultipartContent(request)){
                    out.println(getError("请选择文件。"));

                }
        //检查目录
                File uploadDir = new File(savePath);
                if(!uploadDir.isDirectory()){
                    out.println(getError("上传目录不存在。"));
                }
        //检查目录写权限
                if(!uploadDir.canWrite()){
                    out.println(getError("上传目录没有写权限。"));
                }

                String dirName = request.getParameter("dir");
                if (dirName == null) {
                    dirName = "image";
                }
                if(!extMap.containsKey(dirName)){
                    out.println(getError("目录名不正确。"));

                }
        //创建文件夹
                savePath += dirName + "/";
                saveUrl += dirName + "/";
                File saveDirFile = new File(savePath);
                if (!saveDirFile.exists()) {
                    saveDirFile.mkdirs();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String ymd = sdf.format(new Date());
                savePath += ymd + "/";
                saveUrl += ymd + "/";
                File dirFile = new File(savePath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

            MultipartHttpServletRequest mul = (MultipartHttpServletRequest) request;
            MultiValueMap<String,MultipartFile> files2= mul.getMultiFileMap();
            List<MultipartFile> files=files2.get("imgFile");

            for(int i=0;i<files.size();i++){
                CommonsMultipartFile mf=(CommonsMultipartFile) files.get(i);
                String fileName = mf.getOriginalFilename();
                long fileSize = mf.getSize();
                if (!mf.isEmpty()) {
                    //检查文件大小
                    if(mf.getSize() > maxSize){
                        out.println(getError("上传文件大小超过限制。"));
                        return;
                    }
                    //检查扩展名
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
                        out.println(getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
                        return;
                    }

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
                    try{
                        File uploadedFile = new File(savePath, newFileName);
                        mf.transferTo(uploadedFile);
                    }catch(Exception e){
                        out.println(getError("上传文件失败。"));
                        e.printStackTrace();
                        return;
                    }

                    JSONObject obj = new JSONObject();
                    obj.put("error", 0);
                    obj.put("url", saveUrl + newFileName);
                    out.println(obj.toJSONString());
                }
                }
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    private String getError(String message) {
        JSONObject obj = new JSONObject();
        obj.put("error", 1);
        obj.put("message", message);
        return obj.toJSONString();
    }



}

