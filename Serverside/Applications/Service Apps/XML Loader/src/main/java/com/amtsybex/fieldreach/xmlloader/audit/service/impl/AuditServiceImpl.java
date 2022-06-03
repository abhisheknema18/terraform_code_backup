/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.service.impl;

import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao;
import com.amtsybex.fieldreach.xmlloader.audit.dao.AuditMilestonesDao;
import com.amtsybex.fieldreach.xmlloader.audit.dao.FileErrorsDao;
import com.amtsybex.fieldreach.xmlloader.audit.dao.MilestoneValuesDao;
import com.amtsybex.fieldreach.xmlloader.audit.dao.StatusValuesDao;
import com.amtsybex.fieldreach.xmlloader.audit.message.AuditMessage;
import com.amtsybex.fieldreach.xmlloader.audit.model.AuditFiles;
import com.amtsybex.fieldreach.xmlloader.audit.model.AuditMilestones;
import com.amtsybex.fieldreach.xmlloader.audit.model.FileErrors;
import com.amtsybex.fieldreach.xmlloader.audit.model.MilestoneValues;
import com.amtsybex.fieldreach.xmlloader.audit.model.StatusValues;
import com.amtsybex.fieldreach.xmlloader.audit.service.AuditService;
import com.amtsybex.fieldreach.xmlloader.utils.XmlLoaderUtils;

/**
 * Class to support interaction with the XML loader audit database.
 */
public class AuditServiceImpl implements AuditService {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class.getName());

	@Autowired(required = true)
	@Qualifier("fxl_auditStatusValuesDao")
	StatusValuesDao statusValuesDao;

	@Autowired(required = true)
	@Qualifier("fxl_auditMilestoneValuesDao")
	MilestoneValuesDao milestoneValuesDao;

	@Autowired(required = true)
	@Qualifier("fxl_auditAuditMilestonesDao")
	AuditMilestonesDao auditMilestonesDao;

	@Autowired(required = true)
	@Qualifier("fxl_auditAuditFilesDao")
	AuditFilesDao auditFilesDao;

	@Autowired(required = true)
	@Qualifier("fxl_auditFileErrorsDao")
	FileErrorsDao fileErrorsDao;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * findAuditFiles ()
	 */
	@Override
	public List<AuditFiles> findAuditFiles() throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findAuditFiles");

		List<AuditFiles> retval = this.auditFilesDao.find();

		if (log.isDebugEnabled())
			log.debug("<<< findAuditFiles");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * findAuditFile (java.lang.String)
	 */
	@Override
	public AuditFiles findAuditFile(String id) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findAuditFiles");

		AuditFiles retval = this.auditFilesDao.find(id);

		if (log.isDebugEnabled())
			log.debug("<<< findAuditFiles");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * findAuditMilestones()
	 */
	@Override
	public List<AuditMilestones> findAuditMilestones() throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findAuditMilestones");

		List<AuditMilestones> retval = this.auditMilestonesDao.find();

		if (log.isDebugEnabled())
			log.debug("<<< findAuditMilestones");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * findStatusValues ()
	 */
	@Override
	public List<StatusValues> findStatusValues() throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findStatusValues");

		List<StatusValues> retval = this.statusValuesDao.find();

		if (log.isDebugEnabled())
			log.debug("<<< findStatusValues");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * findMilestoneValues()
	 */
	@Override
	public List<MilestoneValues> findMilestoneValues() throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findMilestoneValues");

		List<MilestoneValues> retval = this.milestoneValuesDao.find();

		if (log.isDebugEnabled())
			log.debug("<<< findMilestoneValues");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * findFileErrors ()
	 */
	@Override
	public List<FileErrors> findFileErrors() throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> findFileErrors");

		List<FileErrors> retval = this.fileErrorsDao.find();

		if (log.isDebugEnabled())
			log.debug("<<< findFileErrors");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * performAuditDbMaintenance(int)
	 */
	@Override
	public void performAuditDbMaintenance(int maxRecordAge) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> performAuditDbMaintenance");

		// 1) Delete records that are older than the specified age.

		// Calculate the 'expiry' date.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -maxRecordAge);
		Date maxDate = cal.getTime();

		// Get the ids of the records to be deleted by querying the
		// audit_milestones table.
		List<Integer> deleteIds = this.auditMilestonesDao.getMaintenanceDeletionCandidates(maxDate);

		// Now delete records from the audit_files, audit_milestones and
		// file_errors tables.
		if (!deleteIds.isEmpty()) {

			this.auditMilestonesDao.deleteById(deleteIds);
			this.fileErrorsDao.deleteById(deleteIds);
			this.auditFilesDao.deleteById(deleteIds);
		}

		if (log.isDebugEnabled())
			log.debug("<<< performAuditDbMaintenance");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#generateId()
	 */
	@Override
	public String generateId() {

		if (log.isDebugEnabled())
			log.debug(">>> generateId");

		String id = UUID.randomUUID().toString();

		while (this.auditFilesDao.idExists(id))
			id = UUID.randomUUID().toString();

		if (log.isDebugEnabled())
			log.debug("<<< generateId retval=" + id);

		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logPickupSuccess
	 * (com.amtsybex.fieldreach.xmlloader.message.internal.ScriptResultMessage)
	 */
	@Override
	public void logPickupSuccess(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logPickupSuccess msg=XXX");

		AuditMilestones milestone = new AuditMilestones();

		milestone.setId(msg.getId());
		milestone.setStatus(XmlLoaderUtils.AUDIT_MILESTONE_STATUS_INPROGRESS);
		milestone.setMilestone(XmlLoaderUtils.AUDIT_MILESTONE_PICKUP);
		milestone.setMilestoneDate(XmlLoaderUtils.getAuditDbCurrentDate());
		milestone.setMilestoneTime(XmlLoaderUtils.getAuditDbCurrentTime());

		AuditFiles auditFile = this.createAuditRecord(msg);

		auditFile.setMilestone(milestone);
		auditFile.setWorkgroup("");

		this.auditFilesDao.save(auditFile);

		if (log.isDebugEnabled())
			log.debug("<<< logPickupSuccess");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logPickupError
	 * (com.amtsybex.fieldreach.xmlloader.message.internal.ScriptResultMessage)
	 */
	@Override
	public void logPickupError(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logPickupError msg=XXX");

		AuditMilestones milestone = new AuditMilestones();

		milestone.setId(msg.getId());
		milestone.setStatus(XmlLoaderUtils.AUDIT_MILESTONE_STATUS_ERROR);
		milestone.setMilestone(XmlLoaderUtils.AUDIT_MILESTONE_PICKUP);
		milestone.setMilestoneDate(XmlLoaderUtils.getAuditDbCurrentDate());
		milestone.setMilestoneTime(XmlLoaderUtils.getAuditDbCurrentTime());

		AuditFiles auditFile = this.createAuditRecord(msg);

		auditFile.setMilestone(milestone);
		
		auditFile.setWorkgroup("");

		// Update error flag.
		if (auditFile.getFileError() == null) {

			FileErrors error = new FileErrors();
			error.setId(msg.getId());
			error.setErrorDetail(msg.getErrorText());

			auditFile.setFileError(error);

		} else {

			FileErrors error = auditFile.getFileError();
			error.setErrorDetail(msg.getErrorText());

			auditFile.setFileError(error);
		}

		this.auditFilesDao.save(auditFile);

		if (log.isDebugEnabled())
			log.debug("<<< logPickupError");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * isDuplicateFile (java.lang.String)
	 */
	@Override
	public boolean isDuplicateFile(String filename) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> isDuplicateFile filename=" + filename);

		boolean retval = this.auditFilesDao.isDuplicate(filename);

		if (log.isDebugEnabled())
			log.debug("<<< isDuplicateFile retval=" + retval);

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logValidationSuccess(com.amtsybex.fieldreach.xmlloader.audit.message.
	 * AuditMessage)
	 */
	@Override
	public void logValidationSuccess(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logValidationSuccess msg=XXX");

		// Find the record for the file processed by the validator.
		AuditFiles auditRecord = this.auditFilesDao.find(msg.getId());

		if (auditRecord == null)
			auditRecord = this.createAuditRecord(msg);

		// Update milestone.
		AuditMilestones milestone = auditRecord.getMilestone();

		milestone.setMilestone(XmlLoaderUtils.AUDIT_MILESTONE_VALIDATOR);
		milestone.setMilestoneDate(XmlLoaderUtils.getAuditDbCurrentDate());
		milestone.setMilestoneTime(XmlLoaderUtils.getAuditDbCurrentTime());

		auditRecord.setMilestone(milestone);
		
		auditRecord.setWorkgroup(msg.getWorkgroup());

		// Save Changes
		this.auditFilesDao.save(auditRecord);

		if (log.isDebugEnabled())
			log.debug("<<< logValidationSuccess");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logValidationError(com.amtsybex.fieldreach.xmlloader.audit.message.
	 * AuditMessage)
	 */
	@Override
	public void logValidationError(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logValidationError msg=XXX");

		this.logError(msg, XmlLoaderUtils.AUDIT_MILESTONE_VALIDATOR);

		if (log.isDebugEnabled())
			log.debug("<<< logValidationError");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logLoadSuccess(com.amtsybex.fieldreach.xmlloader.audit.message.
	 * AuditMessage)
	 */
	@Override
	public void logLoadSuccess(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logLoadSuccess msg=XXX");

		// Find the record for the file processed by the loader.
		AuditFiles auditRecord = this.auditFilesDao.find(msg.getId());

		if (auditRecord == null)
			auditRecord = this.createAuditRecord(msg);

		// Update milestone.
		AuditMilestones milestone = auditRecord.getMilestone();

		milestone.setMilestone(XmlLoaderUtils.AUDIT_MILESTONE_LOADER);
		milestone.setMilestoneDate(XmlLoaderUtils.getAuditDbCurrentDate());
		milestone.setMilestoneTime(XmlLoaderUtils.getAuditDbCurrentTime());

		auditRecord.setMilestone(milestone);

		// Save Changes
		this.auditFilesDao.save(auditRecord);

		if (log.isDebugEnabled())
			log.debug("<<< logLoadSuccess");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#logLoadError
	 * (com.amtsybex.fieldreach.xmlloader.audit.message.AuditMessage)
	 */
	@Override
	public void logLoadError(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logLoadError msg=XXX");

		this.logError(msg, XmlLoaderUtils.AUDIT_MILESTONE_LOADER);

		if (log.isDebugEnabled())
			log.debug("<<< logLoadError");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logDispatcherArchive
	 * (com.amtsybex.fieldreach.xmlloader.message.internal.ScriptResultMessage)
	 */
	@Override
	public void logDispatchArchive(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatchSuccess msg=XXX");

		// Find the record for the file processed by the dispatcher.
		AuditFiles auditRecord = this.auditFilesDao.find(msg.getId());

		if (auditRecord == null)
			auditRecord = this.createAuditRecord(msg);

		// Update milestone.
		AuditMilestones milestone = auditRecord.getMilestone();

		milestone.setMilestone(XmlLoaderUtils.AUDIT_MILESTONE_DISPATCHER);
		milestone.setStatus(XmlLoaderUtils.AUDIT_MILESTONE_STATUS_PROCESSED);
		milestone.setMilestoneDate(XmlLoaderUtils.getAuditDbCurrentDate());
		milestone.setMilestoneTime(XmlLoaderUtils.getAuditDbCurrentTime());

		auditRecord.setMilestone(milestone);
		auditRecord.setDispatched(true);

		// Save Changes
		this.auditFilesDao.save(auditRecord);

		if (log.isDebugEnabled())
			log.debug("<<< logDispatchSuccess");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logDispatcherError
	 * (com.amtsybex.fieldreach.xmlloader.message.internal.ScriptResultMessage)
	 */
	@Override
	public void logDispatchError(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatchError msg=XXX");

		AuditFiles auditRecord = this.auditFilesDao.find(msg.getId());

		if (auditRecord == null)
			auditRecord = this.createAuditRecord(msg);

		// Update milestone.
		AuditMilestones milestone = auditRecord.getMilestone();
		auditRecord.setMilestone(milestone);

		auditRecord.setDispatched(true);

		// Save changes.
		this.auditFilesDao.save(auditRecord);

		if (log.isDebugEnabled())
			log.debug("<<< logDispatchError");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * logDispatcherError
	 * (com.amtsybex.fieldreach.xmlloader.message.internal.ScriptResultMessage)
	 */
	@Override
	public void logDispatcherError(AuditMessage msg) throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatcherError msg=XXX");

		this.logError(msg, XmlLoaderUtils.AUDIT_MILESTONE_DISPATCHER);

		if (log.isDebugEnabled())
			log.debug("<<< logDispatcherError");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.service.AuditService#
	 * getInProgressFiles()
	 */
	@Override
	public List<AuditFiles> getInProgressFiles() throws HibernateException {

		if (log.isDebugEnabled())
			log.debug(">>> getInProgressFiles");

		List<AuditFiles> inProgress;

		inProgress = this.auditFilesDao.findInProgress();

		if (log.isDebugEnabled())
			log.debug("<<< getInProgressFiles");

		return inProgress;
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Update records in the audit_files, audit_milestone and file_errors tables
	 * indicating that a file has been processed unsuccessfully.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files, audit_milestone
	 *            and file_errorstables.
	 * 
	 * @param milestoneId
	 *            milestoneid of the compoent the error occured in.
	 * 
	 */
	private void logError(AuditMessage msg, int milestoneId) {

		if (log.isDebugEnabled())
			log.debug(">>> logError msg=XXX milestoneId=" + milestoneId);

		// Find the record for the file processed by the validator.
		AuditFiles auditRecord = this.auditFilesDao.find(msg.getId());

		if (auditRecord == null)
			auditRecord = this.createAuditRecord(msg);

		// Update milestone.
		AuditMilestones milestone = auditRecord.getMilestone();

		milestone.setMilestone(milestoneId);
		milestone.setStatus(XmlLoaderUtils.AUDIT_MILESTONE_STATUS_ERROR);
		milestone.setMilestoneDate(XmlLoaderUtils.getAuditDbCurrentDate());
		milestone.setMilestoneTime(XmlLoaderUtils.getAuditDbCurrentTime());

		auditRecord.setMilestone(milestone);

		// Update error flag.
		if (auditRecord.getFileError() == null) {

			FileErrors error = new FileErrors();
			error.setId(msg.getId());

			error.setErrorDetail(msg.getErrorText());

			auditRecord.setFileError(error);

		} else {

			FileErrors error = auditRecord.getFileError();
			error.setErrorDetail(msg.getErrorText());

			auditRecord.setFileError(error);
		}

		// Save changes.
		this.auditFilesDao.save(auditRecord);

		if (log.isDebugEnabled())
			log.debug("<<< logError");
	}

	/**
	 * Create a new instance of an AuditFiles class using the information
	 * supplied.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files table.
	 * 
	 * @return
	 */
	private AuditFiles createAuditRecord(AuditMessage msg) {

		if (log.isDebugEnabled())
			log.debug(">>> createAuditRecord msg=XXX");

		AuditFiles auditFile = new AuditFiles();

		auditFile.setId(msg.getId());
		auditFile.setFilename(msg.getFilename());
		auditFile.setTargetInstance(msg.getTargetInstance());
		auditFile.setDispatched(false);
		auditFile.setFileUri(msg.getFileUri());
		auditFile.setWorkgroup(msg.getWorkgroup());

		if (log.isDebugEnabled())
			log.debug("<<< createAuditRecord");

		return auditFile;
	}

}
