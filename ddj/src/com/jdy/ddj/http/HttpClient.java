package com.jdy.ddj.http;

import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClient{
	private static final String PHOTOURL = "photourl";
	static String phoname = null;
	Map<String,String> photomap;
	/*
	 * �����ڵ㷢��POST���� 
	 * 
	 * ��;��
	 * 1������PC����ɹ�������ID����ݵ�����ڵ㣬���浽��ݿ�
	 * 2������Phone����ɹ�������ID������������ۡ���γ�ȵ���ݵ�����ڵ㣬���浽��ݿ�
	 */	
	public static  String sendPostMsg(String headurl,InputStream fileInputStream) {
		try {
			String fileName="";// = readFileAsString("G:\\me\\test像_副本.jpg");
            Map<String,String> photomap2=new HashMap<String, String>();

            URL gatewayUrl = new URL(headurl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) gatewayUrl.openConnection();

			// ������������ http head
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
//			int afternum = jpsstr.getBytes("utf-8").length;
//			System.out.println("client[after encode] send bytes length="+afternum);
//			httpURLConnection.setRequestProperty("Content-Length", ""+ afternum);
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

			// �������������д��json���
			OutputStream outputStream = httpURLConnection.getOutputStream();
//			readFileAsString2("G:\\me\\test像_副本.jpg", outputStream);			
//			outputStream.flush();
//			outputStream.close();
//			httpURLConnection.connect();
			
			String content = readFileAsString(fileInputStream);
//			String json = "{\"content\":\"" +content+ "\"}";
			PrintWriter pw = new PrintWriter(outputStream, true);
			pw.println(content);
			pw.flush();
			pw.close();

			InputStream rspstream = httpURLConnection.getInputStream(); // ��һ�б���Ҫ��������Ϣ���Ͳ���ȥ
			if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
				System.out.println("sendPostMsg2ctrl rsp error, code:" + httpURLConnection.getResponseCode());
				return "";
			}
			
//			Map<String,String> photomap = JsonParser.getInstance().readStream(rspstream, Map.class);
//			if (photomap.containsKey(PHOTOURL)) {
//				phoname = photomap.get(PHOTOURL);
//			}

            photomap2 = JsonParser.getInstance().readStream(rspstream, Map.class);
			if (photomap2!=null&&photomap2.containsKey(PHOTOURL)) {
				System.out.println("post http response:"+photomap2.get(PHOTOURL));
				phoname = photomap2.get(PHOTOURL);
			}
	
		} catch (Exception e) {
			System.out.println("send POST Msg fail: "+e.getMessage()+ ", url:"+headurl);
			return "";
			//�������������ڵ�ʧ��--����������Ϣ������ڵ�ʧ��
//			if(ptype == 1)	
//			{   
//				//LOG.info("index info add to pcQueue ...");
//				BlockQueueService.getInstance().addPcConnectQueueItem(msg);
//				//LOG.info("pcConnectQueue size ..." + ss.size());				
//			}
//			else if (ptype == 2) 
//			{	
//				//LOG.info("index info add to phoneQueue ...");
//				BlockQueueService.getInstance().addPhoneConnectQueueItem(msg);
//				//LOG.info("index info add to phoneQueue ..."+ss.size());
//			}
		}
		return phoname;
	}
	
	
	/*
	 * 发送Get方法给管理节点
	 *  用途：1、发送校验数据probeid+keysession到进行管理平台进行校验
	 */
	public static InputStream sendGetMsg(String headurl) {
        InputStream rspstream=null;
		try {
			// 建立链接
			URL gatewayUrl = new URL(headurl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) gatewayUrl.openConnection();

			// 设置连接属性 http head
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

			//不需要http body
			httpURLConnection.connect();
			 rspstream = httpURLConnection.getInputStream();
			return  rspstream;
		} catch (Exception e) {
			System.out.println("send GET Msg fail: "+e.getMessage()+ ", url:"+headurl);
			return null;
		}
	}
	
	public String saveIMG(InputStream instream){
		String name = "icon999.jpg";
//		BASE64Decoder decoder =new BASE64Decoder();
		try{
//	        byte[] buffer=decoder.decodeBuffer(instream);
//	        System.out.println("jpg[after decode] buffer length="+buffer.length);
	        System.out.println("jpg[origin] buffer length="+instream.available());
	        FileOutputStream fos=new FileOutputStream("g:/img22/"+name);
	        inputCopy2Output(instream, fos);
//	        fos.write(buffer);
	        fos.flush();
	        fos.close();
	        fos=null;
        }
        catch (IOException e) {
        	System.out.println("[ImgFS] save img stream exception: " + e.getMessage());
			return null;
		}
		return name;
	}
	
	public static long inputCopy2Output(InputStream from, OutputStream to)
			throws IOException {
		byte[] buf = new byte[1024];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}
	
	public static String readFileAsString(InputStream fis) throws Exception {
        BufferedInputStream in = new BufferedInputStream(fis);
        byte buffer[] = new byte[256];
        StringBuffer picStr=new StringBuffer();
        BASE64Encoder base64=new BASE64Encoder();
        int beforenum = 0;
        int onenum = in.read(buffer);
        while (onenum>= 0){
            picStr.append(base64.encode(buffer));//
            beforenum+=onenum;
            onenum = in.read(buffer);
        }
        fis.close();
        in.close();
        System.out.println("read file [orginal/encodebuffer]=  ["+beforenum+"/"+picStr.length()+"]");
        return picStr.toString();
    }
	
	@SuppressWarnings("restriction")
	public static void readFileAsString2(String fileName, OutputStream os) throws Exception {
		BASE64Encoder base64=new BASE64Encoder();
        FileInputStream fis = new FileInputStream(fileName);
        base64.encode(fis, os);
        fis.close();
        fis=null;
    }
	
	/**
	* ͨ��ƴ�ӵķ�ʽ�����������ݣ�ʵ�ֲ������Լ��ļ�����
	* @param actionUrl
	* @param params
	* @param files
	* @return
	* @throws java.io.IOException
	*/
	public static String post(String actionUrl, Map<String, String> params,
			Map<String, File> files) throws IOException {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // ������ʱ��
		conn.setDoInput(true);// ��������
		conn.setDoOutput(true);// �������
		conn.setUseCaches(false); // ������ʹ�û���
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		
		// ������ƴ�ı����͵Ĳ���
		if(params!=null&&params.size()>0){
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
                if(entry!=null&&entry.getValue()!=""){
                    sb.append(entry.getValue());
                }
				sb.append(LINEND);
			}
			outStream.write(sb.toString().getBytes());
		}

		
		// �����ļ����
		if (files != null && files.size()>0){
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ file.getKey() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}
		}

		// ��������־
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// �õ���Ӧ��
		int res = conn.getResponseCode();
		StringBuilder sb2 = new StringBuilder();
		if (res == 200) {
			InputStream in = conn.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		outStream.close();
		conn.disconnect();
		return sb2.toString();
	} 
	
	public static void main(String args[]){
		HttpClient client = new HttpClient();
//		client.sendPostMsg("http://localhost:8080/hello?type=aa");
		//client.sendPostMsg("http://localhost:19083/img?icon=0");
//		client.sendGetMsg("http://115.28.25.120:19083/img?icon=0&photourl=pic38.jpg");
		client.sendGetMsg("http://localhost:19083/img?icon=0&photourl=pic1.jpg");
		
//		File file1 = new File("G:\\me\\test��_����.jpg");
//		File file2 = new File("G:\\me\\���¸�������.jpg");
//		File file3 = new File("G:\\me\\large_3514b177.jpg");
//		File file4 = new File("G:\\me\\������_��ְ����.doc");
//		Map<String, File> files  =  new HashMap<String, File>();
//		files.put("test��_����_pic1.jpg", file1);
//		files.put("���¸�������_pic2.jpg", file2);
//		files.put("large_3514b177_pic3.jpg", file3);
//		files.put("������_��ְ����_doc1.doc", file4);
//		try {
//			client.post("http://localhost:8080/hello", null, files);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("client send jpg succeed.");
	}
}
