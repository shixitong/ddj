package com.jdy.ddj.controller;


import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.utils.CommonResult;
import com.jdy.ddj.utils.ImgFS;
import com.jdy.ddj.utils.ResManager;
import com.jdy.ddj.utils.XboxUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tianding
 */
@Controller
public class ImagesController {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ImagesController.class);
    private static ImgFS img = ImgFS.getInstance();


    /**
     * 保存图片 android
     * @param request
     * @param response
     */
    @RequestMapping("/saveImg2.do")
    public void saveImg2(HttpServletRequest request,HttpServletResponse response) {
        InputStream in = null;
        String PphotoName="";
        CommonResult commonResult=new CommonResult();
        Map<String,String> map=new HashMap<String,String>();
        String savePath =request.getSession().getServletContext().getRealPath("/") + "images/";
        JSONObject jsonObject=null;
        //获取输入
        try{
            in = request.getInputStream();
            PphotoName=img.saveIMG(savePath,in);
            map.put("photoName", PphotoName);
            commonResult.setResult(map);
            jsonObject=new JSONObject(commonResult);
            jsonObject.put("result",map);
        }catch (Exception e) {
        	logger.error(e.getMessage());
            commonResult.setCode(XboxUtils.STATUS_ERROR);
            e.printStackTrace();
        }finally {
            ResManager.getOut(response).print(jsonObject);
            ResManager.close();
        }
    }

    /**
     * 保存图片 
     * @param request
     * @param response
     */
    @RequestMapping("/saveImg.do")
    public void saveImg(HttpServletRequest request,HttpServletResponse response) {
        String PphotoName="";
        CommonResult commonResult=new CommonResult();
        Map<String,String> map=new HashMap<String,String>();
        String savePath =request.getSession().getServletContext().getRealPath("/") + "images/";
        System.out.println(savePath);
        JSONObject jsonObject=null;
        //获取输入
        try{
            MultipartHttpServletRequest mul = (MultipartHttpServletRequest) request;
            MultiValueMap<String,MultipartFile>  files2= mul.getMultiFileMap();
            List<MultipartFile> files=files2.get("fileImage");

            for(int i=0;i<files.size();i++){
                CommonsMultipartFile mf=(CommonsMultipartFile) files.get(i);
                String[] strs = mf.getOriginalFilename().split("\\.");
                PphotoName="pic"+DateUtils.getLongDateNum()+"."+strs[strs.length-1];
                mf.transferTo(new File(savePath+PphotoName ));
            }
            map.put("photoName", PphotoName);
            commonResult.setResult(map);
            jsonObject=new JSONObject(commonResult);
            jsonObject.put("result",map);
        }catch (Exception e) {
        	logger.error(e.getMessage());
            commonResult.setCode(XboxUtils.STATUS_ERROR);
            e.printStackTrace();
        }finally {
            ResManager.getOut(response).print(jsonObject);
            ResManager.close();
        }
    }


    /**
     * 读取图片
     * @param request
     * @param photoName
     * @param response
     */
    @RequestMapping("/getImg.do")
    public void getImg(HttpServletRequest request,
                          @RequestParam(value = "photoName",required =true)String photoName,
                          HttpServletResponse response){
        try{
            if(StringUtils.isNotEmpty(photoName)){
                String getPath =request.getSession().getServletContext().getRealPath("/") + "images/"+photoName;
                img.getIMG(getPath, response.getOutputStream());
            }
        }catch (Exception e){
        	logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

/*

    @RequestMapping("/saveImg3.do")
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        String savePath =request.getSession().getServletContext().getRealPath("/") + "images/";

        try {
            request.setCharacterEncoding("UTF-8");
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items = upload.parseRequest(request);
            Iterator itr = items.iterator();
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()) {
                    System.out.println("表单参数名:" + item.getFieldName() + "，表单参数值:" + item.getString("UTF-8"));
                } else {
                    if (item.getName() != null && !item.getName().equals("")) {
                        System.out.println("上传文件的大小:" + item.getSize());
                        System.out.println("上传文件的类型:" + item.getContentType());
                        // item.getName()返回上传文件在客户端的完整路径名称
                        System.out.println("上传文件的名称:" + item.getName());

                        File tempFile = new File(item.getName());

                        File file = new File(savePath, tempFile.getName());
                        item.write(file);
                        request.setAttribute("upload.message", "上传文件成功！");
                    }else{
                        request.setAttribute("upload.message", "没有选择上传文件！");
                    }
                }
            }
        }catch(FileUploadException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("upload.message", "上传文件失败！");
        }

    }
*/


}

