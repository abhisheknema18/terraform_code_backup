/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.dao;

import java.util.List;

import com.amtsybex.fieldreach.extract.audit.model.Redelivery;


/**
 * Interface to support interaction with the redelivery table in the 
 * Extract Adapter audit database.
 */
public interface RedeliveryDao {

	/**
	 * Get all records from the redelivery table.
	 * 
	 * @return List of Redelivery objects.
	 */
	public List<Redelivery> find();

	/**
	 * Get all records from the redelivery table where the attempts column is <
	 * the supplied value.
	 * 
	 * @param maxAttempts
	 * 
	 * @return List of Redelivery objects.
	 */
	public List<Redelivery> findAttemptsLessThan(int maxAttempts);
	
	/**
	 * Get all records from the redelivery table where the attempts column is >=
	 * the supplied value.
	 * 
	 * @param maxAttempts
	 * 
	 * @return List of Redelivery objects.
	 */
	public List<Redelivery> findAttemptsGreaterThan(int maxAttempts);

	/**
	 * Deletes records from the redelivery table with returnIds matching those
	 * supplied.
	 * 
	 * @param ids
	 *            List of returnIds to delete from the redelivery table.
	 */
	public void deleteById(List<String> ids);

}
