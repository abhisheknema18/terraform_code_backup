/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	24/07/2014
 * 
 * Modified by T Murray
 * FDP1165
 * 20/11/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.amtsybex.fieldreach.xmlloader.audit.message.AuditMessage;

/**
 * Utility class to provide common functions and constants for use throughout
 * the entire XML loader application
 */
public class XmlLoaderUtils {

	private static final Logger log = LoggerFactory.getLogger(XmlLoaderUtils.class.getName());

	/*-------------------------------------------
	 - Constants
	 --------------------------------------------*/

	// Application Constants

	public static final String XML_LOADER_APP_CONTEXT_NAME = "applicationContext-fxl.xml";
	public static final String APP_NAME = "XML Loader";

	// Script Result File Constants

	public static final String SCRIPT_RESULT_XSD = "dsResultSet.xsd";

	public static final String DEFAULT_STATUS = "UNCHECKED";

	public static final String MAIN_SCRIPT_RUN_TYPE = "MS";

	// Audit Database Constants

	public static final String AUDIT_DATASOURCE_BEAN_NAME = "fxl_auditDataSource";

	public static final int AUDIT_MILESTONE_STATUS_INPROGRESS = 1;
	public static final int AUDIT_MILESTONE_STATUS_PROCESSED = 2;
	public static final int AUDIT_MILESTONE_STATUS_ERROR = 3;

	public static final int AUDIT_MILESTONE_PICKUP = 1;
	public static final int AUDIT_MILESTONE_VALIDATOR = 2;
	public static final int AUDIT_MILESTONE_LOADER = 3;
	public static final int AUDIT_MILESTONE_DISPATCHER = 4;

	// Dispatcher Constants

	public static final String DISPATCHER_ERROR = "ERROR";
	public static final String DISPATCHER_ARCHIVE = "ARCHIVE";
	
	public enum FileTypes {
		VALID, INVALID
	}

	// private static final Parser parser = new Parser();

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	/**
	 * Get the current date in a format that can be inserted into the audit
	 * database.
	 * 
	 * @return
	 */
	public static java.sql.Date getAuditDbCurrentDate() {

		if (log.isDebugEnabled())
			log.debug(">>> getAuditDbCurrentDate");

		java.sql.Date retval = null;

		Date now = new Date();
		retval = new java.sql.Date(now.getTime());

		if (log.isDebugEnabled())
			log.debug("<<< getAuditDbCurrentDate retval=XXX");

		return retval;
	}

	/**
	 * Get the current time in a format that can be inserted into the audit
	 * database.
	 * 
	 * @return
	 */
	public static java.sql.Time getAuditDbCurrentTime() {

		if (log.isDebugEnabled())
			log.debug(">>> getAuditDbCurrentTime");

		java.sql.Time retval = null;

		Date now = new Date();
		retval = new java.sql.Time(now.getTime());

		if (log.isDebugEnabled())
			log.debug("<<< getAuditDbCurrentTime retval=XXX");

		return retval;
	}

	/**
	 * Construct an AuditMessage object that can be used to update the audit
	 * database.
	 * 
	 * @param id
	 *            Id to identify the message.
	 * 
	 * @param targetInstance
	 *            Instance of Fieldreach the message will be loaded into.
	 * 
	 * @param file
	 *            The file to obtain the data for the message from.
	 * 
	 * @return AuditMessage populated with information on the file to update the
	 *         internal audit database.
	 */
	public static AuditMessage generateAuditMessage(String id, String targetInstance, File file) {

		if (log.isDebugEnabled()) {

			log.debug(">>> generateAuditMessage id=" + id + " targetInstance=" + targetInstance + " file=XXX");
		}

		AuditMessage msg = new AuditMessage();

		msg.setId(id);
		msg.setFilename(file.getName());
		msg.setFileUri(file.getAbsolutePath());
		msg.setTargetInstance(targetInstance);

		if (log.isDebugEnabled())
			log.debug("<<< generateAuditMessage");

		return msg;
	}

	/**
	 * Use the supplied AuditMessage object to generate a sub directory that the
	 * message should be placed in.
	 * 
	 * @param msg
	 * @return
	 */
	public static String generateSubDir(AuditMessage msg) {

		if (log.isDebugEnabled())
			log.debug(">>> generateSubDir msg=XXX");

		String subDir = "";

		if (msg.getTargetInstance() != null && !msg.getTargetInstance().equalsIgnoreCase("null")) {

			subDir = msg.getTargetInstance() + File.separator;
		}

		if (msg.getDispatcherDestination().equals(XmlLoaderUtils.DISPATCHER_ARCHIVE)) {

			// When dispatching to archive create a workgroupcode/date directory
			// structure
			subDir = subDir + msg.getWorkgroup() + File.separator + msg.getCompleteDate();
		}

		if (log.isDebugEnabled())
			log.debug("<<< generateSubDir");

		return FilenameUtils.normalizeNoEndSeparator(subDir);
	}

	/**
	 * Get the schema object that will be used to validate script result files.
	 * 
	 * @throws SAXException
	 */
	public static Schema getScriptResultSchema() throws SAXException {

		if(log.isDebugEnabled())
			log.debug(">>> getScriptResultSchema");

		// Load schema from classpath.
		SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		String xsdPath;
		xsdPath = "xsd/" + XmlLoaderUtils.SCRIPT_RESULT_XSD;
		
		Source schemaFile = null;
		Schema schema = null;
		InputStream xsdPathStream = null;
		try {
				xsdPathStream = XmlLoaderUtils.class.getClassLoader().getResourceAsStream(xsdPath);
				if(xsdPathStream != null) {
		            schemaFile = new StreamSource(xsdPathStream);
		            schema = factory.newSchema(schemaFile);
				}
			if (log.isDebugEnabled())
				log.debug("<<< getScriptResultSchema");

		} 
		finally {
					try {
						if(xsdPathStream != null) {
							xsdPathStream.close();
						}
					} catch (IOException e) {
						log.error("Unexpected exception occurred "+e);
					}
		}
		return schema;
	}
	
	
}
