/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	27/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult;

import java.io.IOException;


/**
 * Interface for dispatching script result files into an archive/error directory.
 */
public interface Dispatch {


	/**
	 * Move the specified file to the configured archive directory.
	 * 
	 * Any temporary file extensions (.tmp) will also be removed.
	 * 
	 * @param file
	 * 
	 * @throws IOException
	 */
	public void dispatchToArchive(String file) throws IOException;
	
	/**
	 * Move the specified file to the configured archive directory.
	 * A sub directory will be created in the archive folder with
	 * the name supplied and the file placed there.
	 * 
	 * Any temporary file extensions (.tmp) will also be removed.
	 * 
	 * @param subDir
	 * 
	 * @param file
	 * 
	 * @throws IOException
	 */
	public void dispatchToArchive(String subDir, String file) throws IOException;
	
	/**
	 * Move the specified file to the configured error directory.
	 * 
	 * Any temporary file extensions (.tmp) will also be removed.
	 * 
	 * @param file
	 * The reason for dispatch to the error directory.
	 * 
	 * @throws IOException
	 */
	public void dispatchToError(String file, String error) throws IOException;
	
	/**
	 * Move the specified file to the configured error directory.
	 * A sub directory will be created in the error folder with
	 * the name supplied and the file placed there.
	 * 
	 * Any temporary file extensions (.tmp) will also be removed.
	 * 
	 * @param subDir
	 * 
	 * @param file
	 * 
	 * @param error
	 * The reason for dispatch to the error directory.
	 * 
	 * @throws IOException
	 */
	public void dispatchToError(String subDir, String file, String error) throws IOException;
	
}