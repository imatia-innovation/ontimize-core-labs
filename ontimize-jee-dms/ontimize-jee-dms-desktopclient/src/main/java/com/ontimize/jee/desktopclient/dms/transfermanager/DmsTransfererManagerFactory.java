package com.ontimize.jee.desktopclient.dms.transfermanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.ontimize.jee.common.exceptions.DmsRuntimeException;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 *
 * This class implements all funcionality to manage downloaders. It saves a list with all downloaders and methods to know status and list all of them.
 *
 */
public class DmsTransfererManagerFactory {

	private static final Logger				logger		= LoggerFactory.getLogger(DmsTransfererManagerFactory.class);

	// The unique instance of this class
	private static IDmsTransfererManager	sInstance	= null;

	/**
	 * Get an instance of this class
	 *
	 * @return the instance of this class
	 */
	public static IDmsTransfererManager getInstance() {
		if (DmsTransfererManagerFactory.sInstance == null) {
			try {
				DmsTransfererManagerFactory.sInstance = BeansFactory.getBean(IDmsTransfererManager.class);
			} catch (NoSuchBeanDefinitionException ex) {
				DmsTransfererManagerFactory.logger.debug("IDmsTransferManager not defined, using default", ex);
				DmsTransfererManagerFactory.sInstance = new DmsTransfererManagerGui();
			}
			if (DmsTransfererManagerFactory.sInstance == null) {
				throw new DmsRuntimeException("Can not initializate transfermanager");
			}
			DmsTransfererManagerFactory.sInstance.init();
		}
		return DmsTransfererManagerFactory.sInstance;
	}
}
