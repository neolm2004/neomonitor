package org.neolm.neomonitor.system;

import org.springframework.beans.factory.InitializingBean;

/**
 * @Title ApplicationCache.java
 * @Description ª∫¥Ê≥ÈœÛ¿‡
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public abstract class ApplicationCache implements InitializingBean {

	public abstract void refresh() ;

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		refresh();
	}

}
