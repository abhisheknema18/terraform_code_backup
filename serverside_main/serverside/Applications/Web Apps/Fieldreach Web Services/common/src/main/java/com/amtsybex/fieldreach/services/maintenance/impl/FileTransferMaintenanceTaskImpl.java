/**
 * Author:  T Murray
 * Date:    28/05/2014
 * Project: FDE026
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.services.maintenance.impl;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.services.endpoint.common.FileTransferController;
import com.amtsybex.fieldreach.services.maintenance.FileTransferMaintenanceTask;


/**
 * Responsible for managing the record of in progress download/uploads and any temporary
 * files associated with the download/upload process.
 */
public final class FileTransferMaintenanceTaskImpl implements FileTransferMaintenanceTask {
	
	private static Logger log = LoggerFactory.getLogger(FileTransferMaintenanceTaskImpl.class.getName());
	
	private int maxDaysActive;
	
	private ScheduledExecutorService fileTransferMaintenanceScheduler;
	
	@Autowired
	private FileTransferController fileTransferController;
	
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/
	
	
	/**
	 * Default constructor
	 */
	private FileTransferMaintenanceTaskImpl() {
		
	}

	
	/**
	 * Initialise the downloadMaintenanceScheduler so that it will create a single thread 
	 * responsible for executing the maintenance task every hour. 
	 */
	@PostConstruct
	private void init() {
		
		log.debug(">>> init");
		
		this.fileTransferMaintenanceScheduler 
			= Executors.newSingleThreadScheduledExecutor();
		
		this.fileTransferMaintenanceScheduler
			.scheduleAtFixedRate(this, 0, 1, TimeUnit.DAYS);
		
		log.debug("<<< init");
	}
	
	
	/*----------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/
	
	
	public void setMaxDaysActive(int maxDaysActive) {
		
		this.maxDaysActive = maxDaysActive;
	}
	
	
	public int getMaxDaysActive() {
		
		return this.maxDaysActive;
	}
	
	
	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		log.debug(">>> run");
		
		try {
			
			this.performMaintenance();
		}
		catch (Throwable t) {
			 
			log.error("Exception in download maintenance thread.");
			log.error(t.getMessage());
		}
		 
		log.debug("<< run");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.services.maintenance.FileTransferMaintenanceTask#performMaintenance()
	 */
	@Override
	public void performMaintenance() throws Exception {

		log.debug(">>> performMaintenance");
				
		fileTransferController.downloadMaintenance(this.maxDaysActive);
		fileTransferController.uploadMaintenance(this.maxDaysActive);
		
		log.debug("<<< performMaintenance ");
	}
	
	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/
	
	@PreDestroy
	private void destroy() {
		
		log.debug(">>> destroy");
		
		this.fileTransferMaintenanceScheduler.shutdown();
		
		log.debug("<<< destroy");
	}
	
}
