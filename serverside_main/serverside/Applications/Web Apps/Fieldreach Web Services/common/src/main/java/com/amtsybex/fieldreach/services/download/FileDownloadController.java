/**
 * Author:  T Murray
 * Date:    09/04/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 */
package com.amtsybex.fieldreach.services.download;

import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;

public interface FileDownloadController {

	/**
	 * Initiate a multi part download for a file located on the file system.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param file
	 *            The URI of the file to be downloaded. The value of this
	 *            parameter should be absolute and include the path to file.
	 * 
	 * @return InitiateDownloadResponse object populated with initialisation
	 *         information to enable to multipart download.
	 */
	public InitiateDownloadResponse initiateFileSystemDownload(
			String frInstance, String file);

	/**
	 * Initiate a multi part download for a file located in the fieldreach
	 * ScriptResultsBlb table
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param returnid
	 *            Returnid of the file to be downloaded.
	 * 
	 * @param resorderno
	 *            ResOrder number of the file to be downloaded.
	 * 
	 * @return InitiateDownloadResponse object populated with initialisation
	 *         information to enable to multipart download.
	 */
	public InitiateDownloadResponse initiateScriptResultsBlbDownload(
			String frInstance, int returnid, int resorderno);

	/**
	 * Retrieve the part number of the download specified.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param identifier
	 *            Identifier of the active download to download the part for.
	 * 
	 * @param partNo
	 *            Part number to retrieve for the active download specified.
	 * 
	 * @return DownloadPartResponse the part of the active download requested.
	 */
	public DownloadPartResponse downloadPart(String frInstance,
			String identifier, int partNo);
}
