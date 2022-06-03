/**
 * Author:  T Murray
 * Date:    17/00/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Code Tidy up
 * 
 * Amended:
 * FDE029 TM 19/01/2015
 * Changes to asset database download process and code re-factoring.
 */
package com.amtsybex.fieldreach.services.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.utils.impl.Common;
import org.springframework.http.HttpHeaders;

/**
 * A utility class to provide common functions that may be required during the
 * upload process.
 */
public class Utils {

	private static Logger log = LoggerFactory.getLogger(Utils.class);

	// File types

	public final static String CONFIG_FILE_TYPE = "config";
	public final static String SCRIPT_FILE_TYPE = "script";
	public final static String SUPPORT_FILE_TYPE = "supportResource";
	public final static String REFERENCEONLY_FILE_TYPE = "scriptSupport";
	public final static String WORKORDER_FILE_TYPE = "workorder";
	public static final String ASSETDB_TYPE = "assetDb";
	public static final String WORKBANKDB_TYPE = "workbankDb";
	public static final String RESULTHISTORYDB_TYPE = "resulthistoryDb";

	// Script Files

	public final static String SCRIPT_FILE_EXTENSION = ".xml";
	public final static String SCRIPT_FILE_PREFIX = "SPS.";
	public final static String SCRIPT_FILE_SEPARATOR = "_";

	// Config files

	public final static String CONFIG_FILE_EXTENSION = ".xml";
	public final static String EQCONFIG_FILE_NAME = "EQCONFIG";
	public final static String WMCONFIG_FILE_NAME = "WMCONFIG";
	public final static String SCRIPTCONFIG_FILE_NAME = "SCRIPTCONFIG";

	// Workorder files

	public final static String WORKORDER_FILE_EXTENSION = ".xml";
	public final static String WORKORDER_FILE_PREFIX = "WO.";
	public final static String WORKORDER_TEMP_FILE_EXTENSION = ".tmp";

	public static final String WORKORDER_WORKORDERNO_ELEMENT = "WorkOrderNo";
	public static final String WORKORDER_WORKORDERDESC_ELEMENT = "WorkOrderDesc";
	public static final String WORKORDER_EQUIPNO_ELEMENT = "EquipNo";
	public static final String WORKORDER_EQUIPDESC_ELEMENT = "EquipDesc";
	public static final String WORKORDER_ALTEQUIPREF_ELEMENT = "AltEquipRef";
	public static final String WORKORDER_PLANSTARTDATE_ELEMENT = "PlanStartDate";
	public static final String WORKORDER_REQFINISHDATE_ELEMENT = "ReqFinishDate";
	public static final String WORKORDER_PLANSTARTTIME_ELEMENT = "PlanStartTime";
	public static final String WORKORDER_REQFINISHTIME_ELEMENT = "ReqFinishTime";
	public static final String WORKORDER_USERCODE_ELEMENT = "UserCode";
	public static final String WORKORDER_WOTYPE_ELEMENT = "WoType";
	public static final String WORKORDER_DISTRICTCODE_ELEMENT = "DistrictCode";
	public static final String WORKORDER_ADDITIONALTEXT_ELEMENT = "AdditionalText";
	public static final String WORKORDER_WORKGROUPCODE_ELEMENT = "WorkGroupCode";
	public static final String WORKORDER_WORKSTATUS_ELEMENT = "WorkStatus";
	public static final String WORKORDER_LATITUDE_ELEMENT = "Latitude";
	public static final String WORKORDER_LONGITUDE_ELEMENT = "Longitude";
	public static final String WORKORDER_DEFAULT_DISTRICT = "NA";

	public static final String WORKORDER_DEFAULT_SCHEMA = "workorder.xsd";

	public static final String WORKORDER_ATTACHMENT_DIR = "attachments";

	// Transaction Retention

	public static final String TRANSACTION_WORKSTATUS = "WORKSTATUS";
	public static final String TRANSACTION_HEARTBEAT = "HEARTBEAT"; // FDP1255 TM 01/11/2016

	public static final String FRT_WORKSTATUS_DEST_TYPE_PROP = "frtransaction.workstatus.destinationtype";
	public static final String FRT_WORKSTATUS_DEST_PROP = "frtransaction.workstatus.destination";
	public static final String FRT_WORKSTATUS_RETAIN_LIST = "frtransaction.workstatus.retainstatuslist";

	public static final String FRT_JMS_DEST_TYPE = "JMS";
	public static final String FRT_DIR_DEST_TYPE = "DIR";

	// Asset hierarchy database

	public static final String ASSET_DB_NOT_FOUND = "AssetDBNotFoundException";
	public static final String ASSET_DB_EXT = ".zip";
	public static final String ASSET_DB_SEP = "_";

	public static final String DBCLASS_PARAM = "dbClass";
	public static final String DBCLASS_PLACEHOLDER = "$DBCLASS";

	// Workbank database

	public static final String WBCLASS_PARAM = "wbClass";
	public static final String WBCLASS_PLACEHOLDER = "$WBCLASS";
	
	//FDE043 - MC - ResultHistoryDB
	public static final String RESULTHISTORY_DB_NOT_FOUND = "ResultHistoryDBNotFoundException";
	public static final String SCRIPT_RESULT_EXTRACTION_EXCEPTION = "ScriptResultExtractionException";
	// Exception Messages

	public static final String FILE_NOT_FOUND = "FileNotFoundException";
	public static final String USER_REVOKED = "UserRevokedException";
	public static final String GENERAL_EXCEPTION = "EXCEPTION";
	public static final String MAX_FILESIZE_EXCEEDED = "MaximumFileSizeExceededException";
	public static final String MAX_CHUNKSIZE_EXCEEDED = "MaximumChunkSizeExceededException";
	public static final String INVALID_ID_EXCEPTION = "InvalidUploadIdentifierException";
	public static final String CHECKSUM_EXCEPTION = "ChecksumMismatchException";
	public static final String PART_NO_SEQ_EXCEPTION = "PartNumberSequenceException";
	public static final String INITIATE_IO_EXCEPTION = "InititateUploadIOException";
	public static final String WORKORDER_VALIDATION_EXCEPTION = "WorkOrderValidationException";
	public static final String WORKORDER_SCHEMA_EXCEPTION = "WorkOrderSchemaNotFoundException";
	public static final String IO_EXCEPTION = "IOException";
	public static final String DB_EXCEPTION = "DatabaseException";
	public static final String WORKORDER_NOT_FOUND_EXCEPTION = "WorkOrderNotFoundException";
	public static final String WORKORDER_ALREADY_EXISTS_EXCEPTION = "WorkOrderAlreadyExistsException";
	public static final String WORKORDER_UPDATE_EXCEPTION = "WOUpdateException";
	public static final String WORKORDER_CANCEL_EXCEPTION = "WOCancelException";
	public static final String WORKORDER_RECALL_EXCEPTION = "WORecallException";
	public static final String WORKORDER_DISPATCH_EXCEPTION = "WODispatchException";
	public static final String WORKORDER_DOWNLOAD_EXCEPTION = "DownloadWOException";
	public static final String UNKNOWN_TRANSACTION_EXCEPTION = "UnknownTransactionTypeException";
	public static final String MAX_RESULTS_EXCEEDED_EXCEPTION = "MaxResultsExceededException";
	public static final String FR_INSTANCE_EXCEPTION = "FRInstanceException";
	public static final String SCRIPT_RESULT_EXTRACT_EXCEPTION = "ScriptResultExtractException";
	public static final String SCRIPT_RESULT_NOTFOUND_EXCEPTION = "ScriptResultNotFoundException";
	public static final String INVALID_DOWNLOAD_ID_EXCEPTION = "InvalidDownloadIdentifierException";
	public static final String PART_NUMBER_EXCEPTION = "PartNumberException";
	public static final String PASSWORD_CHANGE_EXCEPTION = "PasswordChangeException";
	public static final String AUTHENTICATION_EXCEPTION = "AuthenticationException";
	public static final String AUTHORIZATION_EXCEPTION = "AuthorizationException";
	public static final String AUTHENTICATION_EXCEPTION_DESCRIPTION = "User cannot be authenticated";
	public static final String WORKBANK_DB_NOT_FOUND = "WorkbankDBNotFoundException";
	public static final String ATTACHMENT_REGISTRATION_EXCEPTION = "AttachmentRegistrationException";
	public static final String APP_ACCESS_EXCEPTION = "AppAccessException";
	public static final String RESULT_STATUS_NOTFOUND_EXCEPTION = "ResultStatusNotFoundException";
	public static final String RESULT_STATUS_UPDATE_EXCEPTION = "StatusUpdateException";
	public static final String RESPONSE_UPDATE_NOT_SUPPORTED_EXCEPTION = "ResponseUpdateNotSupportedException";
	public static final String SCRIPT_ITEM_NOT_FOUND_EXCEPTION = "ScriptItemNotFoundException";
	public static final String QUESTION_DEF_NOT_SUPPORTED_EXCEPTION = "ScriptQuestionDefNotSupportedException";
	public static final String QUESTION_DEF_NOT_FOUND_EXCEPTION = "ScriptQuestionDefNotFoundException";
	public static final String SYSTEM_USER_REVOKED_EXCEPTION = "SystemUserRevokedException";
	public static final String INVALID_WORKGROUP_EXCEPTION = "InvalidWorkgroupException";
	public static final String REVOKED_MOBILE_INVITATION_EXCEPTION = "RevokedMobileInvitationException";
	public static final String EXPIRED_MOBILE_INVITATION_EXCEPTION = "ExpiredMobileInvitationException";
	public static final String INVALID_USER_CODE = "InvalidUserCode";
	
	//FDE053 - MC - System Monitor Exceptions
	public static final String SERVICE_CODE_NOT_FOUND_EXCEPTION = "ServiceCodeNotFoundException";
	public static final String SERVICE_CODE_MONITOR_DISABLED_EXCEPTION = "ServiceCodeMonitoringDisabledException";
	//FDP1183 CM 09/02/2016
	public static final String SCRIPT_DEF_NOT_FOUND_EXCEPTION = "ScriptDefinitionNotFoundException";
	//FDP1183 CM End
	
	public static final String UNKNOWN_FILE_TYPE_EXCEPTION = "UnknownFileTypeException";
	public static final String RESULT_NOTE_CREATION_EXCEPTION = "ResultNoteCreationException";
	
	// Miscellaneous

	public static final String FWS_APP_PROPERTIES = "app.properties";
	public static final String APPLICATION_IDENTIFIER_HEADER = "x-fws-applicationidentifier";
	public static final String SYSTEM_USERCODE = "SYSTEM";
	public static final String DBAREA_PARAM = "dbArea";
	public static final String DBAREA_PLACEHOLDER = "$DBAREA";
	public static final String DISTRICTCODE_PARAM = "districtCode";

	public static final String MOBILE_APPROVALS_APP_CODE = "MA";

	public static final String APPCODE_HEADER = "x-fws-appCode"; // FDP1255 TM 01/11/2016
	public static final String DEVICEID_HEADER = "x-fws-deviceid";
	public static final String APPVERSION_HEADER = "x-fws-appVersion";
	
	public static final String PARTNUMBER_HEADER =  "x-iws-part-number";
	public static final String PARTLENGTH_HEADER =  "x-iws-part-length";
	public static final String PARTCHECKSUM_HEADER =  "x-iws-checksum";
	
	public static final String USER_DOES_NOT_EXISTS  = "User does not exist";
	
	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	/**
	 * extract the application identifier header from the supplied request.
	 * 
	 * @param request
	 *            request to extract the application identifier header from.
	 * 
	 * @return extracted application identifier. null is returned if no header
	 *         could be found.
	 */
	public static String extractApplicationIdentifier(HttpServletRequest request) {

		if (log.isDebugEnabled())
			log.debug(">>> extractApplicationIdentifier");

		String retval = null;

		if (request.getHeader(APPLICATION_IDENTIFIER_HEADER) != null) {

			String[] fields = request.getHeader(APPLICATION_IDENTIFIER_HEADER).split("\\s|:");

			if (log.isDebugEnabled()) {

				log.debug("Header defined :" + Common.CRLFEscapeString(request.getHeader(APPLICATION_IDENTIFIER_HEADER)) + " fieldCount:"
						+ fields.length);
			}

			if (fields.length > 0)
				retval = fields[0];
		}

		if (log.isDebugEnabled())
			log.debug("<<< extractApplicationIdentifier retval=" + Common.CRLFEscapeString(retval));

		return (retval);
	}

	/**
	 * extract the application identifier header from the supplied request.
	 *
	 * @param httpHeaders
	 *            request to extract the application identifier header from.
	 *
	 * @return extracted application identifier. null is returned if no header
	 *         could be found.
	 */
	public static String extractApplicationIdentifier(HttpHeaders httpHeaders) {

		if (log.isDebugEnabled())
			log.debug(">>> extractApplicationIdentifier");

		String retval = null;

		if (httpHeaders.getFirst(APPLICATION_IDENTIFIER_HEADER) != null) {

			String applicationIdentifier = httpHeaders.getFirst(APPLICATION_IDENTIFIER_HEADER);

			if (log.isDebugEnabled()) {

				log.debug("Header defined :" + applicationIdentifier);
			}
			
			retval = applicationIdentifier;
		}

		if (log.isDebugEnabled())
			log.debug("<<< extractApplicationIdentifier retval=" + retval);

		return (retval);
	}


	/**
	 * Get HMacSHA1 signature for stringToSign with secret key secretKey
	 * 
	 * @param secretKey
	 * 
	 * @param stringToSign
	 *            - string that needs to be signed
	 * 
	 * @return string representing base64 version of signature
	 
	public static String getSignature(String secretKey, String stringToSign) {

		String retval = null;

		if (log.isDebugEnabled()) {

			log.debug(">>> getSignature:" + secretKey + " signed:" + stringToSign);
		}

		try {

			// Try to register Sun JCE provider
			try {

				Security.addProvider(new com.sun.crypto.provider.SunJCE());

			} catch (Exception ex) {
			}

			dumpSecurityProviders();

			// Now try to load the Sun JCE
			// and if that fails use the default.
			Mac mac = null;

			try {

				mac = Mac.getInstance(HMAC_SHA1, "SunJCE");

			} catch (Exception ex) {

				// Use default.
				mac = Mac.getInstance(HMAC_SHA1);
			}

			if (log.isDebugEnabled())
				log.debug("Provider in use :" + mac.getProvider().getName());

			byte[] keyBytes = secretKey.getBytes(Common.UTF8_ENCODING);

			if (log.isDebugEnabled()) {

				log.debug("Key bytes :" + dumpByteArray(keyBytes) + " " + new String(keyBytes));
			}

			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1);

			mac.init(signingKey);

			// Signed String must be BASE64 encoded.
			if (log.isDebugEnabled()) {

				log.debug("String to sign bytes :" + dumpByteArray(stringToSign.getBytes(Common.UTF8_ENCODING)));
			}

			byte[] signBytes = mac.doFinal(stringToSign.getBytes(Common.UTF8_ENCODING));

			if (log.isDebugEnabled())
				log.debug("Sign bytes :" + dumpByteArray(signBytes));

			retval = Common.encodeBase64(signBytes);

		} catch (Exception ex) {

			log.error("Create Auth Sig Exception :" + ex.getMessage());
		}

		if (log.isDebugEnabled())
			log.debug("<<< getSignature retval=" + retval);

		return retval;
	}
*/
	/**
	 * Method to write the data passed in to the file system. If the file does
	 * not exist it will be created, if it exists then data will be appended to
	 * the end of it.
	 * 
	 * @param content
	 *            The content to be written to the file specified by the
	 *            filename parameter. This should be an array of bytes.
	 * 
	 * @param fileName
	 *            the name of the file to be written to the file system.
	 * 
	 * @param dir
	 *            The directory you want to write the file to on the file
	 *            system.
	 * 
	 * @return true is returned if the file is successfully written to the file
	 *         system, and false is returned if it is not.
	 */
	public static boolean appendBytesToFile(byte[] content, String fileName, String dir) {

		if (log.isDebugEnabled()) {

			log.debug(">>> appendByteFile content=XXXXXX fileName=" + fileName + " dir=" + dir);
		}

		boolean result = false;

		String completeFileName = dir + "/" + fileName;

		FileOutputStream fstream = null;

		try {

			if (log.isDebugEnabled())
				log.debug("Attempting to append to file: " + completeFileName);

			// Create the target directory if necessary.
			if (Common.createDir(dir)) {

				fstream = new FileOutputStream(completeFileName, true);

				fstream.write(content);

				if (log.isDebugEnabled())
					log.debug("File successfully written to: " + completeFileName);

				result = true;

			} else {

				throw new Exception(
						"Unable to append to file: " + completeFileName + ". " + "Error creating directory.");
			}

		} catch (Exception e) {

			log.error(e.toString());

		} finally {

			// Close the BufferedWriter
			try {

				if (fstream != null) {

					fstream.flush();
					fstream.close();
				}

			} catch (IOException ex) {

				log.error(ex.toString());
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< appendByteFile");

		return result;
	}

	/**
	 * Get stack trace in string format for logging
	 * 
	 * @param t
	 *            throwable to get stack trace from.
	 * 
	 * @return string representation of stack trace
	 */
	public static String getStackTrace(Throwable t) {

		if (log.isDebugEnabled())
			log.debug(">>> getStackTrace t=XXX");

		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();

		if (log.isDebugEnabled())
			log.debug("<<< getStackTrace");

		return stackTrace;
	}

	/**
	 * A method to generate a file name for an upload part temporary file
	 * 
	 * @param id
	 *            Identifier of the upload the upload part is associated with.
	 * 
	 * @param partNo
	 *            The part number of N parts.
	 * 
	 * @return A filename for the temporary upload part file should be written
	 *         to.
	 */
	public static String generateUploadPartFileName(String id, int partNo) {

		if (log.isDebugEnabled()) {

			log.debug(">>> generateUploadPartFileName id=" + id + " partNo=" + partNo);

			log.debug("<<< generateUploadPartFileName");
		}

		return id + "_" + partNo + ".tmp";
	}

	/**
	 * Method to print debug statements to display the headers passed with a
	 * request.
	 * 
	 * @param log
	 *            The Logger object to write the debug statements to.
	 * 
	 * @param request
	 *            The HttpServletRequest to print the headers of.
	 */
	public static void debugHeaders(Logger log, HttpServletRequest request) {

		// Debug of headers..

		if (log.isDebugEnabled()) {

			log.debug(">>> debugHeaders log=XXX request=XXX");

			Enumeration<String> headerNames = request.getHeaderNames();

			while (headerNames.hasMoreElements()) {

				String headerName = (String) headerNames.nextElement();
				if (log.isDebugEnabled())
					log.debug("Header :" + Common.CRLFEscapeString(headerName) + ":" + Common.CRLFEscapeString(request.getHeader(headerName)));
			}

			if (log.isDebugEnabled())
				log.debug("Request URL :" + Common.CRLFEscapeString(request.getRequestURL().toString()));

			log.debug("<<< debugHeaders");
		}
	}

	/**
	 * Method to print debug statements to display the headers passed with a
	 * request.
	 *
	 * @param log
	 *            The Logger object to write the debug statements to.
	 *
	 * @param httpHeaders
	 *            The HttpHeaders to print the headers of.
	 */
	public static void debugHeaders(Logger log, HttpHeaders httpHeaders) {

		// Debug of headers..

		if (log.isDebugEnabled()) {

			log.info(">>> debugHeaders log=XXX request=XXX");

			httpHeaders.forEach((httpHeaderKey, httpHeaderValue) -> {
				if (log.isDebugEnabled())
					log.info("Header :" + httpHeaderKey + ":" + httpHeaderValue.get(0));
			});
			
			log.debug("<<< debugHeaders");
		}
	}

	// FDE019 11/10/2012 TM
	/**
	 * Method to fetch an optional query string parameter from the request
	 * supplied.
	 * 
	 * @param request
	 *            Request to extract for optional parameter.
	 * 
	 * @param paramName
	 *            Name of the optional parameter to extract.
	 * 
	 * @return The value of the optional parameter if it has been supplied. Null
	 *         is returned if the optional parameter was supplied but it is
	 *         empty or null.
	 */
	public static String getOptionalParam(HttpServletRequest request, String paramName) {

		if (log.isDebugEnabled())
			log.debug(">>> getOptionalParam request=XXX paramName=" + paramName);

		String param = null;

		if (request.getParameter(paramName) != null)
			param = request.getParameter(paramName);

		if (param != null && param.trim().equals(""))
			param = null;

		if (log.isDebugEnabled())
			log.debug("<<< getOptionalParam");

		return param;
	}

	/**
	 * Takes a fieldreach database time and determines if it needs to have any
	 * '0' characters prepended. The time should be 6 characters long, and some
	 * databases will remove leading zeros.
	 * 
	 * @param time
	 *            Time to preprend with 0's if required.
	 * 
	 * @return
	 */
	public static String correctFieldreachDBTime(Integer time) {

		if (log.isDebugEnabled())
			log.debug(">>> correctFieldreachDBTime time=" + time);

		String originalTime = time.toString();
		int missingChars = 6 - originalTime.length();

		String correctedTime = originalTime;

		if (missingChars > 0) {

			String prefixChars = "";

			for (int i = 0; i < missingChars; i++)
				prefixChars += "0";

			correctedTime = prefixChars + originalTime;
		}

		if (log.isDebugEnabled())
			log.debug("<<< correctFieldreachDBTime");

		return correctedTime;
	}

	// End FDE019

	// FDE020 TM 28/02/2013

	/**
	 * Convert the blob supplied to a byte array
	 * 
	 * @param blobResponse
	 * 
	 * @return
	 */
	public static byte[] blobToBytes(Blob blobResponse) {

		if (log.isDebugEnabled())
			log.debug(">>> blobToBytes");

		byte[] blobBytes;

		try {

			InputStream inputBS;
			inputBS = blobResponse.getBinaryStream();

			BufferedInputStream bis = new BufferedInputStream(inputBS);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int resultInt = bis.read();

			while (resultInt != -1) {

				byte b = (byte) resultInt;
				buf.write(b);
				resultInt = bis.read();
			}

			blobBytes = buf.toByteArray();

		} catch (SQLException e) {

			log.error("blobToBytes: SQLException" + e.getMessage());

			blobBytes = null;

		} catch (IOException e) {

			log.error("blobToBytes: IOException" + e.getMessage());

			blobBytes = null;
		}

		if (log.isDebugEnabled())
			log.debug("<<< blobToBytes");

		return blobBytes;
	}

	/**
	 * Takes xml string supplied and adds indentation and line breaks to make it
	 * more human readable.
	 * 
	 * @param xml
	 *            Unformatted xml to be formatted.
	 * 
	 * @return The xml string supplied formatted with indentations and line
	 *         breaks. If an error occurs during processing the original
	 *         unformatted XML string is supplied.
	 */
	public static String prettyXML(String xml) {

		if (log.isDebugEnabled())
			log.debug(">>> prettyXML xml=XXX");

		String formattedXML;

		try {

			Document doc = DocumentHelper.parseText(xml);
			StringWriter sw = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xw = new XMLWriter(sw, format);
			xw.write(doc);

			formattedXML = sw.toString();

		} catch (DocumentException e) {

			formattedXML = xml;
			e.printStackTrace();

		} catch (IOException e) {

			formattedXML = xml;
			e.printStackTrace();
		}

		if (log.isDebugEnabled())
			log.debug("<<< prettyXML");

		return formattedXML;
	}

	// END FDE020

	// FDE022 TG 28/12/2013

	/**
	 * Generate an SHA-256 hash of a string.
	 * 
	 * @param inString
	 * @return sha-256(inString)
	 
	public static String generateBase64SHA256HashForString(String inString) {

		if (log.isDebugEnabled())
			log.debug(">>> generateBase64SHA256HashForString inString=XXXXX");

		String sha256Hash = null;

		try {

			byte[] data = inString.getBytes("UTF-8");

			// Generate SHA-256 hash using the MessageDigest class.
			MessageDigest digest = MessageDigest.getInstance(SHA_256);
			digest.update(data);
			byte[] hash = digest.digest();

			sha256Hash = Common.encodeBase64(hash);

		} catch (NoSuchAlgorithmException e) {

			log.error(e.toString());
			sha256Hash = null;

		} catch (UnsupportedEncodingException e) {

			log.error(e.toString());
			sha256Hash = null;
		}

		if (log.isDebugEnabled()) {

			log.debug("<<< generateBase64SHA256HashForString hash=" + sha256Hash);
		}

		return sha256Hash;
	}
*/
	/**
	 * Return time from device in request headers
	 * 
	 * @param request
	 * @return device time via Date header in request or current server time if
	 *         not specified.
	 */
	public static Date getDeviceTime(HttpServletRequest request) {

		if (log.isDebugEnabled())
			log.debug(">>> getDeviceTime request=XXX");

		Calendar deviceTime = Calendar.getInstance();

		if (request.getHeader("Date") != null) {

			// Parse header date.
			// Must conform to RFC 2616
			// eg EEE, dd MMM yyyy HH:mm:ss ZZZ
			Date deviceDate = org.apache.http.client.utils.DateUtils.parseDate(request.getHeader("Date"));
			
			if(deviceDate != null) {
				
				deviceTime.setTime(deviceDate);
				
			}else if(log.isDebugEnabled()) {
				
					log.debug("Device time not valid. use server time");
			}

			if (log.isDebugEnabled())
				log.debug("Device time :" + deviceTime.getTime().toString());
		}

		if (log.isDebugEnabled())
			log.debug("<<< getDeviceTime");

		return deviceTime.getTime();
	}

	// END FDE022

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Print supplied byte array as a string.
	 * 
	 * @param bytes
	 *            Byte array to convert to a string.
	 * 
	 * @return Byte array supplied as a string.
	
	private static String dumpByteArray(byte[] bytes) {

		if (log.isDebugEnabled())
			log.debug(">>> dumpByteArray bytes=XXX");

		if (bytes == null)
			return "";

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {

			sb.append("0x").append((char) (HEX_CHAR[(bytes[i] & 0x00F0) >> 4]))
					.append((char) (HEX_CHAR[bytes[i] & 0x000F])).append(" ");
		}

		if (log.isDebugEnabled())
			log.debug("<<< dumpByteArray");

		return sb.toString();
	}
 */
	/**
	 * Log list of available security providers.
	 
	private static void dumpSecurityProviders() {

		if (log.isDebugEnabled()) {

			log.debug("<<< dumpSecurityProviders");

			Provider aProviders[] = Security.getProviders();

			for (int i = 0; i < aProviders.length; i++) {

				Provider p = aProviders[i];

				log.debug("Provider " + p.getName());

				for (String id : p.stringPropertyNames()) {

					String value = p.getProperty(id);

					if (value.toLowerCase().contains("hmacsha1"))
						log.debug("Has hmacsha1 " + value);

				}
			}

			log.debug("<<< dumpSecurityProviders");
		}
	}
*/
}
