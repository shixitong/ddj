package com.jdy.ddj.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Tianding on 15-7-14.
 */
public class XboxWebUtils{

    private static final Logger logger = LoggerFactory.getLogger(XboxWebUtils.class);

    public static String HttpUrl=XboxUtils.xbox.getProperty("xbox.url");

    /***
     * POST方法请求
     * @param url
     * @param obj
     * @return
     */
    public static String sendPost(String url,JSONObject obj){
        BufferedReader in = null;
        String result = "";
        URL url2 = null;
        HttpURLConnection connection = null;

        try {
            //创建连接
            url2=new URL(HttpUrl+url);
            connection = (HttpURLConnection) url2 .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
//            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Content-Type","application/json");

           // connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//            connection.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

            out.writeBytes(obj.toString());
            out.flush();

          /// connection.getInputStream();

            //定义BufferedReader输入流来读取URL的响应
            new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            //String line;
           // while ((line = in.readLine()) != null) {
            //    result += line;
           // }
          //  out.close();




            //把json转为实体
        } catch (Exception e) {
            logger.error("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(connection!=null){
                   // connection.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * GET方法请求
     *
     *
     */
    public static String  sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString =HttpUrl+url + "?" + param;
            URL realUrl = new URL(urlNameString);
            //打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            //设置通用的请求
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            //把json转为实体
            return  result;
        } catch (Exception e) {
            logger.error("发送 GET 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return "";
    }



    /**
     * Socket 请求
     */
    public static String SendSocket(int port,String content){
        Map<String, Object> map = new HashMap<String, Object>();
        boolean flag = false;


        try {
            Socket socket = new Socket(HttpUrl,port);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuffer sb = new StringBuffer();
            sb.append("POST /ServletBindUrl HTTP/1.1\r\n")
                    .append("Host:"+ HttpUrl + "\r\n")
                    .append("Content-Type:application/x-www-form-urlencoded\r\n")
                    .append("Content-Length:"+content.length()+"\r\n")
                    .append(content+"\r\n");
            out.write(sb.toString());
            out.flush();
            //打印响应
            String resultStr = "";
            String line = "";
            while ((line = in.readLine())!=null){
                resultStr += line;
            }
            System.out.println(resultStr);
        } catch (Exception e) {
            map.put("flag", flag);
        }finally{
            map.put("flag", flag);
        }
        return map.toString();
    }


    /**
     * 封装map参数
     * @param map
     * @return
     */
    public static String  getParamByMap(Map map){
        String param="";
        try{
            //封装请求参数
            if(map != null){
                Set<String> sets=map.keySet();
                for (String key:sets){
                    if(map.get(key) != null){
                        param+=key+"="+URLEncoder.encode(map.get(key).toString(),"utf-8")+"&";
                    }else{
                        param+=key+"=''&";
                    }
                }
                param=param.substring(0,param.length()-1);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("封装map参数失败");
        }
        return param;
    }
    public static  List<NameValuePair>  getParamByMap2(Map map){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try{
            //封装请求参数
            if(map != null){
                Set<String> sets=map.keySet();
                for (String key:sets){
                    if(map.get(key) != null){
                        params.add(new BasicNameValuePair(key, map.get(key).toString()));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("封装map参数失败");
        }
        return params;
    }


    /**
     * 发送http请求
     * @param url
     * @param map
     * @return
     */
    public static String sendPostHttp(String url,Map map){

        String result="";
          /*建立HTTPost对象*/
        HttpPost httpRequest = new HttpPost(HttpUrl+url);

        try
        {
          /* 添加请求参数到请求对象*/
            httpRequest.setEntity(new UrlEncodedFormEntity(getParamByMap2(map), HTTP.UTF_8));
          /*发送请求并等待响应*/
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
          /*若状态码为200 ok*/
            if(httpResponse.getStatusLine().getStatusCode() == 200)
            {
            /*读返回数据*/
                result = EntityUtils.toString(httpResponse.getEntity());
                //mTextView1.setText(strResult);
            }
            else
            {
               // mTextView1.setText("Error Response: "+httpResponse.getStatusLine().toString());
            }
        }
        catch (ClientProtocolException e)
        {
           // mTextView1.setText(e.getMessage().toString());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            //mTextView1.setText(e.getMessage().toString());
            e.printStackTrace();
        }
        catch (Exception e)
        {
           // mTextView1.setText(e.getMessage().toString());
            e.printStackTrace();
        }
        return result;
    }


   

}
