package com.jdy.ddj.listener;


import com.jdy.ddj.utils.XboxUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;

/**初始化系统信息配置信息
 * @author Recky(lutianding)
 */
public class ContextStartupListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent arg0) {
		//加载属性文件
		XboxUtils.xbox = new Properties();
		InputStream in = ContextStartupListener.class.getClassLoader().getResourceAsStream( "../classes/conf/system.properties");
		try {
            XboxUtils.xbox.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
