/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core.impl;

import com.amtsybex.fieldreach.common.SingletonRegistry;
import com.amtsybex.fieldreach.interfaces.ServiceApplication;
import com.amtsybex.fieldreach.interfaces.impl.ServiceApplicationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Interface implementation for core Extract Adapter functions that can be used
 * to control the application.
 * 
 * Implemented as a singleton.
 */
@Component
public final class ExtractAdapterCoreImpl extends ServiceApplicationImpl {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(ExtractAdapterCoreImpl.class.getName());

	private static final String APP_NAME = "Fieldreach Extract Adapter";
	private static final String SPRING_CONTEXT_NAME = "applicationContext-fea.xml";

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * Default constructor to create an instance of the ExtractAdapterCoreImpl
	 * class.
	 */
	protected ExtractAdapterCoreImpl() {

		super(APP_NAME, SPRING_CONTEXT_NAME);

		if (log.isDebugEnabled())
			log.debug(">>> ExtractAdapterCoreImpl");
		
		SingletonRegistry.addInstance(this);
		
		if (log.isDebugEnabled())
			log.debug("<<< ExtractAdapterCoreImpl");
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

		ExtractAdapterCoreImpl instance = (ExtractAdapterCoreImpl) SingletonRegistry
				.getInstance(ExtractAdapterCoreImpl.class.getName());

		if (log.isDebugEnabled())
			log.debug("<<< getInstance");

		return instance;
	}

}