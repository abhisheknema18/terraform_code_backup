/**
 * Author:  T Murray
 * Date:    28/05/2014
 * Project: FDE026
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.services.maintenance;


public interface FileTransferMaintenanceTask extends Runnable {

	
	/**
	 * Responsible for removing inactive downloads and uploads from the Fieldreach database.
	 * This method will inspect all downloads stored in the inProgressDownloads table
	 * and any entries found to be older than the configured maximum number of days a 
	 * download can remain active for will be removed.
	 * 
	 * The same process will alsoo occur on the InProgressUploads table, deleting all
	 * records that have expired and all associated records in the UploadPArts table.
	 * 
	 * @throws Exception
	 */
	public void performMaintenance() throws Exception;
	
}
