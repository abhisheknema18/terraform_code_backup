/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	08/01/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core;

import com.amtsybex.fieldreach.extract.ExtractCandidate;

/**
 * Interface for classes that wish to be responsible for dispatching messages
 * extracted by the Fieldreach extract adapter.
 */
public interface ExtractAdapterDispatcher {

	/**
	 * Dispatch the extract candidate to the configured location.
	 * 
	 * @param extract
	 *            An ExtractCandidate object containing details of the extracted
	 *            script result and the extracted XML.
	 */
	public void dispatch(ExtractCandidate extract);
	
	/**
	 * Set the status a script result will be set to if an error occurs during
	 * the extraction process.
	 * 
	 * @param errorStatus
	 */
	public void setErrorStatus(String errorStatus);
	
	/**
	 * Set the location on the file system that extracted messages will be dispatched to
	 * if the application is conifgured for file system dispatch.
	 * 
	 * @param dirDest
	 */
	public void setDirDestination(String dirDest);
}
