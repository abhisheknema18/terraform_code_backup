/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	24/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.core.impl;

import com.amtsybex.fieldreach.common.SingletonRegistry;
import com.amtsybex.fieldreach.interfaces.ServiceApplication;
import com.amtsybex.fieldreach.interfaces.impl.ServiceApplicationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface implementation for core XML loader functions that can be used to
 * control the application.
 * 
 * Implemented as a singleton.
 */
public final class XmlLoaderCoreImpl extends ServiceApplicationImpl {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(XmlLoaderCoreImpl.class.getName());

	private static final String APP_NAME = "Fieldreach XML Loader";
	private static final String SPRING_CONTEXT_NAME = "applicationContext-fxl.xml";

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * Default constructor to create an instance of the XmlLoaderCoreimpl class.
	 */
	protected XmlLoaderCoreImpl() {

		super(APP_NAME, SPRING_CONTEXT_NAME);

		if (log.isDebugEnabled())
			log.debug(">>> XmlLoaderCoreImpl");
		
		SingletonRegistry.addInstance(this);
		
		if (log.isDebugEnabled())
			log.debug("<<< XmlLoaderCoreImpl");
	}

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	/**
	 * Get instance of this class.
	 * 
	 * @return
	 */
	public static ServiceApplication getInstance() {

		if (log.isDebugEnabled())
			log.debug(">>> getInstance");

		XmlLoaderCoreImpl instance = (XmlLoaderCoreImpl) SingletonRegistry
				.getInstance(XmlLoaderCoreImpl.class.getName());

		if (log.isDebugEnabled())
			log.debug("<<< getInstance");

		return instance;
	}

}
