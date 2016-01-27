package com.jdy.ddj.controller;


import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.http.HttpClient;
import com.jdy.ddj.utils.ResManager;
import com.jdy.ddj.utils.XboxWebUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/oa/service/attach/")
public class UserAttachmentController {
    private Logger logger = LoggerFactory.getLogger(UserAttachmentController.class);


    
    
    /**
     * 附件上传保存（表单post）
     */
    @RequestMapping(value = "upload.do")
    public void upload(
            HttpSession session, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "classify",required = false,defaultValue = "") String classify,
            @RequestParam(value = "maxSize",required = false,defaultValue = "0") long maxSize
    ) throws Exception {

        JSONObject json = new JSONObject();
        String PphotoName="";
        String savePath =request.getSession().getServletContext().getRealPath("/") + "images/";

        try {
            JSONArray array = new JSONArray();

            MultipartHttpServletRequest mul = (MultipartHttpServletRequest) request;
            List<MultipartFile> files= mul.getFiles("file");
            for(int i=0;i<files.size();i++){
                CommonsMultipartFile mf=(CommonsMultipartFile) files.get(i);


                long fileSize = mf.getSize() / 1024;

                if(maxSize==0 || maxSize>10240){
                    maxSize = 10240;
                }

                if(fileSize > maxSize){
                    throw new Exception("附件大小超过系统限额：" + maxSize + "KB");
                }


                String[] strs = mf.getOriginalFilename().split("\\.");
                PphotoName="pic"+ DateUtils.getLongDateNum()+"."+strs[strs.length-1];
                mf.transferTo(new File(savePath+PphotoName ));

                JSONObject obj = new JSONObject();
                obj.put("fileSize", fileSize+"KB");
                obj.put("originalName",PphotoName);
                obj.put("id",PphotoName);

                array.put(obj);
            }

            //打印上传成功信息
            json.put("success",true);
            json.put("data",array);
            ResManager.getTextOut(response).print(json);

        }catch (Exception e){
            json.put("success",false);
            json.put("error",e.getMessage());
            ResManager.getTextOut(response).print(json);
        }finally {
            ResManager.close();
        }
    }


    public static String getImageStr(InputStream in) {
        byte[] data = null;
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }



    /**
     * 使用uploadify上传
     */
    @RequestMapping(value = "uploadifyUpload.do")
    public void uploadifyUpload(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipartHttpServletRequest mul = (MultipartHttpServletRequest) request;
        List<MultipartFile> files= mul.getFiles("att");
        for(int i=0;i<files.size();i++){
            CommonsMultipartFile mf=(CommonsMultipartFile) files.get(i);

            //打印上传成功信息
            ResManager.getOut(response).print("");
            ResManager.close();
            break;
        }
    }

    /**
     * 附件分类操作
     */
    @RequestMapping("classify.do")
    public void classify(
            HttpSession session,
            HttpServletResponse response,
            @RequestParam(value = "attId") String attId,
            @RequestParam(value = "classify") String classify
    ) throws Exception{

        ResManager.getOut(response).print("{\"success\": true}");
        ResManager.close();
    }

    /**
     * 附件下载
     */
    @RequestMapping(value = "download.do")
    private void download(
            HttpSession session,HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "id") String id
    ){

        try {
            if(StringUtils.isNotBlank(id)){
                String url=XboxWebUtils.HttpUrl+"img?icon=0&photourl="+id;
                    InputStream is = HttpClient.sendGetMsg(url);
                    IOUtils.copy(is, response.getOutputStream());
                    is.close();
                    response.flushBuffer();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 保存关联
     */
    @RequestMapping("ref/save.do")
    public void saveRef(
            HttpSession session,
            HttpServletResponse response,
            @RequestParam(value = "attId") String attId,
            @RequestParam(value = "refId") String refId,
            @RequestParam(value = "refKey",required = false,defaultValue = "") String refKey
    ) throws Exception{

        ResManager.getOut(response).print("{\"success\": true,\"id\":\""+""+"\"}");
        ResManager.close();
    }



    /**
     * 附件管理-列表
     */
    @RequestMapping(value="list.do")
    public void attList(
            HttpSession session,HttpServletRequest request,HttpServletResponse response,
            @RequestParam(value = "admin",required = false,defaultValue = "false") boolean isAdmin,
            @RequestParam(value = "deleteStatus",required = false,defaultValue = "0") int deleteStatus,
            @RequestParam(value = "owner",required = false,defaultValue = "") String owner,
            @RequestParam(value = "classify",required = false,defaultValue = "") String classify,
            @RequestParam(value = "usedStatus",required = false,defaultValue = "0") int usedStatus,
            @RequestParam(value = "countUsed",required = false,defaultValue = "false") boolean countUsed,
            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows
    ) throws Exception{


        //输出的json对象
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        json.put("total",0);

        json.put("rows",array);

        ResManager.getOut(response).print(json);
        ResManager.close();
    }



}
