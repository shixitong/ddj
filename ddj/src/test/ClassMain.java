package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdy.ddj.http.SMSClient;

public class ClassMain {

	
	
	public static void main(String[] args) {



		String smrz4 = ClassMain.sendPost("http://120.55.97.176:8899/ddj/order/getDetail.do",
                "gdid=JGD12031415501300000&usertype=5");
		System.out.println(smrz4);
		
		

		
	}
	
	/**
	 * 根据地址获取经纬度
	 * @return
	 */
	public static String getDT(String address){
		//拼接地址到参数上
		String param = "address="+address +"&output=json&ak=702632E1add3d4953d0f105f27c294b9&callback=showLocation";
		
		//访问百度地图的接口来获取数据
		String result = ClassMain.sendGet("http://api.map.baidu.com/geocoder/v2/", param);
		//用经度分割返回的网页代码 
		String s=","+"\""+"lat"+"\""+":";  
        String strs[]=result.split(s, 2);  
        String s1="\""+"lng"+"\""+":";  
       String a[]=strs[0].split(s1, 2);
       String r = "lng="+a[1];
       s1="}"+","+"\"";  
      String a1[]=strs[1].split(s1, 2);  
       r += ":lat" + a1[0];  
		return r;
	}
	
	/**
	 * POST方法请求
	 * 
	 */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置模仿浏览器请求的设置
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
           // conn.setRequestProperty("Accept-Charset", "ISO-8859-1");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送请求参数
            out.print(new String(param.getBytes("utf-8"),"UTF-8"));
            // flush输出流的缓冲
            out.flush();
            //定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
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
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            //打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            //设置通用的请求
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            //接收请求返回的数据
            Map<String, List<String>> map = connection.getHeaderFields();
            //
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 GET 请求出现异常！" + e);
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
        return result;
    }
    
    /**
     * Socket 请求
     */
    public String SendSocket(String host,int port,String content){
    	Map<String, Object> map = new HashMap<String, Object>();
    	boolean flag = false;
    	if(host == null){
    		map.put("flag", flag);
			return map.toString();
    	}
    		
    	try {
    		Socket socket = new Socket(host,port);
    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		StringBuffer sb = new StringBuffer();
    		sb.append("POST /ServletBindUrl HTTP/1.1\r\n")
    			.append("Host:"+ host + "\r\n")
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
}
