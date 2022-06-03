/**
 * Author:  T Murray
 * Date:    19/03/2012
 * Project: FDE016
 * 
 * Modified:
 * 	FDE034 Trevor Murray 11/08/2015
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.resource.common;

public class DatabaseDetails {

	private String name;
	private long size;
	private String checksum;

	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/
	
	public DatabaseDetails()
	{
		
	}

	
	/**
	 * 
	 * @param name
	 * @param size
	 * @param checksum
	 */
	public DatabaseDetails(String name, long size, 
			               String checksum) {

		this.name = name;
		this.size = size;
		this.checksum = checksum;
	}


	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public long getSize() {
		return size;
	}


	public void setSize(long size) {
		this.size = size;
	}


	public String getChecksum() {
		return checksum;
	}


	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	

}
