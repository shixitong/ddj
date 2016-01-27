package com.jdy.ddj.utils;

import com.jdy.ddj.common.utils.DateUtils;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;

import java.io.*;

public class ImgFS {
	private static Logger LOG = Logger.getLogger(ImgFS.class);
	private static BASE64Decoder decoder;
	public static String IMGPATH="/upload/images/";

	//singleton
	private static ImgFS instance = new ImgFS();
	private ImgFS(){
		decoder=new BASE64Decoder();
	}
	
	public static ImgFS getInstance() {
		return instance;
	}
	
	/**
	* <p>Title:saveIMG</p>
	* <p>Description:解码并保存图片</p>
	*        iconfg=1用户头像
	* @return 返回本地保存的图片名字
	*/
	public String saveIMG(String savePath,InputStream instream){

		String name="pic"+ DateUtils.getLongDateNum()+".jpg";
		String fullpath=savePath +name;
		try{
	        byte[] buffer=decoder.decodeBuffer(instream);
	        System.out.println("jpg[after decode] buffer length="+buffer.length);
	        FileOutputStream fos=new FileOutputStream(fullpath);
	        fos.write(buffer);
	        fos.flush();
	        fos.close();
        }catch (IOException e) {
        	e.printStackTrace();
		}
		return name;
	}
    public String saveIMG2(String savePath,InputStream instream){

        String name="pic"+ DateUtils.getLongDateNum()+".jpg";
        String fullpath=savePath +name;
        try{
          //  byte[] buffer=decoder.decodeBuffer(instream);
          //  byte[] photo = Base64.encode(buffer);
            byte[] bytes=input2byte(instream);

         //   System.out.println("jpg[after decode] buffer length="+buffer.length);
            FileOutputStream fos=new FileOutputStream(fullpath);
            fos.write(bytes);
            fos.flush();
            fos.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }


    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }



    /**
	* <p>Title:getIMG</p>
	* <p>Description:获取图片并编码</p>
	* @param
	* @return
	*/
	public void getIMG(String getPath, OutputStream outstream){
		try{
	        FileInputStream fis = new FileInputStream(getPath);
	        inputCopy2Output(fis, outstream);
	        fis.close();
		}
		catch (IOException e) {
        	LOG.error("[ImgFS] get img stream exception: " + e.getMessage());
		}
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
}
