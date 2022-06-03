/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.StatusUpdateException;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao;
import com.amtsybex.fieldreach.extract.audit.dao.ExtractErrorsDao;
import com.amtsybex.fieldreach.extract.audit.dao.RedeliveryDao;
import com.amtsybex.fieldreach.extract.audit.model.ExtractAudit;
import com.amtsybex.fieldreach.extract.audit.model.ExtractErrors;
import com.amtsybex.fieldreach.extract.audit.model.Redelivery;
import com.amtsybex.fieldreach.extract.audit.service.AuditService;
import com.amtsybex.fieldreach.extract.utils.FEAUtils;

/**
 * Class to support interaction with the Extract Adapter audit database.
 */
public class AuditServiceImpl implements AuditService {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class
			.getName());

	@Autowired(required = true)
	@Qualifier("fxl_redeliveryDao")
	RedeliveryDao redeliveryDao;

	@Autowired(required = true)
	@Qualifier("fea_extractErrorsDao")
	ExtractErrorsDao extractErrorsDao;

	@Autowired(required = true)
	@Qualifier("fea_extractAuditDao")
	ExtractAuditDao extractAuditDao;

	@Autowired(required = true)
	@Qualifier("scriptResultsService")
	private ScriptResultsService scriptResultsService;

	private String errorStatus;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.service.AuditService#setErrorStatus
	 * (java.lang.String)
	 */
	@Override
	public synchronized void setErrorStatus(String errorStatus) {

		if (log.isDebugEnabled())
			log.debug(">>> setErrorStatus errorStatus=" + errorStatus);

		this.errorStatus = errorStatus;

		if (log.isDebugEnabled())
			log.debug("<<< setErrorStatus");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.service.AuditService#generateId()
	 */
	@Override
	public synchronized String generateId() {

		if (log.isDebugEnabled())
			log.debug(">>> generateId");

		String id = UUID.randomUUID().toString();

		while (this.extractAuditDao.idExists(id))
			id = UUID.randomUUID().toString();

		if (log.isDebugEnabled())
			log.debug("<<< generateId retval=" + id);

		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.service.AuditService#find(java.
	 * lang.String)
	 */
	@Override
	public synchronized ExtractAudit findExtractAudit(String id) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> find id=" + id);

		ExtractAudit result = this.extractAuditDao.find(id);

		if (log.isDebugEnabled())
			log.debug("<<< find");

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.service.AuditService#
	 * logExtractionCandidate(int, java.lang.String)
	 */
	@Override
	public synchronized String logExtractionCandidate(int returnId, String instance)
			throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logExtractionCandidate returnId=" + returnId
					+ " instance=" + instance);

		String generatedId = this.generateId();

		ExtractAudit audit = new ExtractAudit();

		audit.setId(generatedId);
		audit.setReturnId(returnId);
		audit.setInstance(instance);
		audit.setExtracted(false);
		audit.setAuditDate(FEAUtils.getAuditDbCurrentDate());
		audit.setAuditTime(FEAUtils.getAuditDbCurrentTime());

		audit.setRedelivery(null);
		audit.setError(null);

		this.extractAuditDao.save(audit);

		if (log.isDebugEnabled())
			log.debug("<<< logExtractionCandidate");

		return generatedId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.service.AuditService#logExtractionError
	 * (int, java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void logExtractionError(String id, String errorDetail)
			throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logExtractionError id=" + id + " errorDetail="
					+ errorDetail);

		ExtractAudit audit = this.findExtractAudit(id);

		audit.setAuditDate(FEAUtils.getAuditDbCurrentDate());
		audit.setAuditTime(FEAUtils.getAuditDbCurrentTime());

		ExtractErrors error = new ExtractErrors();
		error.setId(id);
		error.setErrorDetail(errorDetail);

		audit.setError(error);

		this.extractAuditDao.save(audit);

		if (log.isDebugEnabled())
			log.debug("<<< logExtractionError");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.service.AuditService#
	 * logDispatchException(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void logDispatchException(String id, String content)
			throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatchException id=" + id + " content="
					+ content);

		ExtractAudit audit = this.findExtractAudit(id);

		audit.setAuditDate(FEAUtils.getAuditDbCurrentDate());
		audit.setAuditTime(FEAUtils.getAuditDbCurrentTime());

		Redelivery redel;

		if (audit.getRedelivery() == null) {

			redel = new Redelivery();
			redel.setId(id);
			redel.setAttempts(0);

		} else {

			redel = audit.getRedelivery();
			redel.setAttempts(redel.getAttempts() + 1);
		}

		audit.setRedelivery(redel);
		audit.setExtracted(false);

		if (content == null)
			this.extractAuditDao.save(audit);
		else
			this.extractAuditDao.save(audit, content);

		if (log.isDebugEnabled())
			log.debug("<<< logDispatchException");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.service.AuditService#
	 * logRedeliveryException(java.lang.String)
	 */
	@Override
	public synchronized void logRedeliveryException(String id) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatchException id=" + id);

		this.logDispatchException(id, null);

		if (log.isDebugEnabled())
			log.debug("<<< logDispatchException");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.service.AuditService#logDispatchSuccess
	 * (java.lang.String)
	 */
	@Override
	public synchronized void logDispatchSuccess(String id) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatchSuccess id=" + id);

		ExtractAudit audit = this.findExtractAudit(id);

		audit.setAuditDate(FEAUtils.getAuditDbCurrentDate());
		audit.setAuditTime(FEAUtils.getAuditDbCurrentTime());
		audit.setExtracted(true);

		this.extractAuditDao.save(audit);

		List<String> ids = Arrays.asList(id);

		this.redeliveryDao.deleteById(ids);
		this.extractErrorsDao.deleteById(ids);

		if (log.isDebugEnabled())
			log.debug("<<< logDispatchSuccess");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.service.AuditService#
	 * findRedeliveryCandidates(int)
	 */
	@Override
	public synchronized List<Redelivery> findRedeliveryCandidates(int maxAttempts)
			throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findRedeliveryCandidates");

		List<Redelivery> candidates = this.redeliveryDao
				.findAttemptsLessThan(maxAttempts);

		if (log.isDebugEnabled())
			log.debug("<<< findRedeliveryCandidates");

		return candidates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.service.AuditService#
	 * cancelExpiredRedeliveries(int)
	 */
	@Override
	public synchronized void cancelExpiredRedeliveries(int maxAttempts)
			throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findExpiredRedeliveries");

		// Get list of entries from audit database that exceed the maximum
		// number of re-deliveries.
		List<Redelivery> expiredRedeliveries = this.redeliveryDao
				.findAttemptsGreaterThan(maxAttempts);

		List<String> idsToDelete = new ArrayList<String>();

		for (Redelivery expired : expiredRedeliveries) {

			try {

				ExtractAudit audit = this.findExtractAudit(expired.getId());

				this.scriptResultsService.updateResultErrorStatus(
						audit.getInstance(), audit.getReturnId(),
						this.errorStatus);

				audit.setAuditDate(FEAUtils
						.getAuditDbCurrentDate());
				audit.setAuditTime(FEAUtils
						.getAuditDbCurrentTime());
				audit.setExtracted(false);

				ExtractErrors error = new ExtractErrors();
				error.setId(audit.getId());
				error.setErrorDetail("Redelivery of script result failed.");

				audit.setError(error);

				this.extractAuditDao.save(audit);

				idsToDelete.add(audit.getId());

			} catch (StatusUpdateException e) {

				// If error occurs updating Fieldreach database, then
				// the re-delivery record will not be deleted this time.

			} catch (FRInstanceException e) {

				// If error occurs updating Fieldreach database, then
				// the re-delivery record will not be deleted this time.
			}
		}

		// Now delete entries from redeliveries table.
		if (!idsToDelete.isEmpty())
			this.redeliveryDao.deleteById(idsToDelete);

		if (log.isDebugEnabled())
			log.debug("<<< findExpiredRedeliveries");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.service.AuditService#
	 * performAuditDbMaintenance(int)
	 */
	@Override
	public synchronized void performAuditDbMaintenance(int maxRecordAge)
			throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> performAuditDbMaintenance");

		// 1) Delete records that are older than the specified age.
		// Calculate the 'expiry' date.
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -maxRecordAge);
		Date maxDate = cal.getTime();

		// 2) Get the ids of the records to be deleted by querying the
		// extract_audit table.
		
		List<String> deleteIds = this.extractAuditDao
				.getMaintenanceDeletionCandidates(maxDate);

		// 3) Now delete records from the extract_audit, extract_errors
		// and redelivery tables.
		
		if (!deleteIds.isEmpty()) {
			
			this.extractAuditDao.deleteById(deleteIds);
			this.extractErrorsDao.deleteById(deleteIds);
			this.redeliveryDao.deleteById(deleteIds);
			
		}

		if (log.isDebugEnabled())
			log.debug("<<< performAuditDbMaintenance");
	}

}
