/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	23/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core;

import java.util.Map;

import com.amtsybex.fieldreach.backend.instance.Instance;

/**
 * Interface for classes that wish to be responsible for managing the extraction
 * process.
 */
public interface ExtractAdapterController extends Runnable {

	/**
	 * Method to set the map of Fieldreach instances.
	 * 
	 * @param instances
	 */
	public void setFieldreachInstances(Map<String, Instance> instances);

}
