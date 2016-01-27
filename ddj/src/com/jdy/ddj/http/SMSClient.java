package com.jdy.ddj.http;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

/**
* <p>Title:SMSClient</p>
* <p>Description:短信平台client接口</p>
* <p>Company:fnic</p> 
* @author xushangyu
* @date 2015年7月10日
*/
public class SMSClient {
	private static Logger LOG = Logger.getLogger(SMSClient.class);
	private static final String SQN = "6SDK-EMY-6688-KKVPN";
	private static final String KEY = "441413";
	private static final String PASSWD = "441413";
	
	private String smsprefix = "即刻办公提醒您，您的验证码是";
	private com.jdy.ddj.http.Client client=null;
	
	private static SMSClient instance = new SMSClient();
	private SMSClient(){
		try {
			client=new Client(SQN,KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static SMSClient getInstance() {
		return instance;
	}
	

	/**
	* <p>Title:registEx</p>
	* <p>Description:短信平台注册</p>
	* @param
	* @return
	*/
	public void registEx() {
		try {
			int i = client.registEx(PASSWD);
			LOG.info("sms register code: "+i);
//			System.out.println("testTegistEx:" + i);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	* <p>Title:sendSMS</p>
	* <p>Description:下发验证码短信</p>
	* @param
	* @return
	*/
	public synchronized  void sendSMS(String phone, String chkcode) {
		try {
			int i = client.sendSMS(new String[] {phone}, smsprefix+chkcode, "",5);// 带扩展码
			if(i!=0){
				LOG.error("phone["+phone+"]'s checkcode["+chkcode+"] send fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
