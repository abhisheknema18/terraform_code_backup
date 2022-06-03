/**
 * Author:  T Goodwin & T Murray
 * Date:    08/02/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import com.amtsybex.fieldreach.security.utils.SecurityUtils;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.test.BaseTestController;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

// Base class for all test cases
public class CommonBase {

	static Logger log = LoggerFactory.getLogger(CommonBase.class.getName());

	protected BaseTestController testController;

	protected Properties applicationProperties;
	protected String baseURL;
	protected String deviceid;
	protected String appCode;

	protected String unrevokedUserCode;
	protected String unrevokedPassword;

	protected String revokedUserCode;
	protected String revokedPassword;
	protected String invalidUserCode;
	protected String invalidPassword = "xcasf";
	protected String newPass;

	protected String uploadScriptsSourceDirectory;
	protected String legalUploadFileName;

	protected String validScriptId;
	protected String invalidScriptId;
	protected String validScriptChecksum;

	protected String scriptConfigFile;
	protected String wmConfigFile;
	protected String workGroupScriptConfigFile;
	protected String workGroupWMConfigFile;

	protected String scriptChecksum;
	protected String wmChecksum;
	protected String workGroupScriptChecksum;
	protected String workGroupWMChecksum;

	// Asset Hierarchy download properties

	protected String DU01FileName;
	protected String DU01Checksum;
	protected long DU01Size;

	protected String assetDatabaseInvalid;

	protected String assetDatabaseFileMaxSizeFileName;

	protected String assetDatabaseDownloadDir;

	// Phase III

	// Script reference files

	protected String scriptReferenceScriptCode;
	protected String scriptReferenceFile;
	protected String scriptReferenceFileNotInResource;
	protected String scriptReferenceFileMissing;

	// Validation Types

	protected String validationTypeList;
	protected String validationTypeNonExistingList;
	protected String singlevalidationTypeList;

	// Script profiles

	protected String scriptProfileI;
	protected String scriptProfileIPassword;
	protected String scriptProfileIScriptList;

	protected String scriptProfileC;
	protected String scriptProfileCPassword;
	protected String scriptProfileCScriptList;

	// PHASE IV

	protected String configFileDir;

	protected String uploadWorkOrderDir;
	protected String existingWO;
	protected String invalidWO;
	protected String WONoUserCodeOrWGCode;
	protected String validWO1;
	protected String validWO2;
	
	protected String validRELWO1;
	protected String validRELWO2;

	protected String woDC;

	// Work Issued

	protected String workissuedUserCode;
	protected String workissuedUserPassword;
	protected String workIssuedWorkGroupCode;

	protected int recordsExpectedU;
	protected int recordsExpectedW;
	protected int recordsExpectedMW;

	// Retrieve Work Order

	protected String WOWorkIssuedAndFileSystem;
	protected String WONotWorkIssued;
	protected String WOWorkIssuedAndNotFileSystem;
	protected String WODownloadInvalidStatus;

	// Fieldreach transaction processing

	protected String uploadTransDir;
	protected String validWorkStatusTrans;
	protected String invalidWorkStatusTrans;
	protected String nonWorkStatusTrans;
	protected String alreadyProcessedTrans;
	protected String notInWorkIssuedTrans;
	protected String validWorkStatusTransCloseWork;
	protected String validWorkStatusTransCantDoWork;

	// script result search

	protected String scriptResultSearch_equipNo;
	protected String scriptResultSearch_fromDate;
	protected String scriptResultSearch_toDate;

	protected String scriptResultSearch_scriptCodeSingle;
	protected String scriptResultSearch_scriptCodeMulti;
	protected String scriptResultSearch_resultStatusSingle;
	protected String scriptResultSearch_resultStatusMulti;
	protected String scriptResultSearch_userCodeSingle;
	protected String scriptResultSearch_userCodeMulti;
	protected String scriptResultSearch_workgroupCodeSingle;
	protected String scriptResultSearch_workgroupCodeMulti;
	
	protected String scriptResultSearch_viewId;
	protected String scriptResultSearch_workOrderNo;
	protected String scriptResultSearch_resAssocCodeReturnId;
	
	// Expect script result search results

	protected Map<Integer, Integer> scriptResultSearchExpectedResults;

	protected int scriptSearchExpectedResults4;
	protected int scriptSearchExpectedResults5;
	protected int scriptSearchExpectedResults6;
	protected int scriptSearchExpectedResults7;

	// Script Result retrieval

	protected String retrieveScriptResultDir;

	protected String retrieveScriptResultExtractionError;
	protected String retrieveScriptResultValidResult;
	protected String binaryResponseResOrderNo;
	protected String nonBinaryResponseResOrderNo;
	protected String retrieveScriptResultUnanswered;
	protected String retrieveScriptResultCannotEdit;
	protected String retrieveScriptResultCanEdit;
	
	// FDP1177 TM 21/01/2016
	protected String canEditUserCode;
	protected String canEditPassword;
	// End FDP1177
	
	// Extract Engine

	protected int extractResultReturnIdMissing;
	protected int extractResultNoScriptRoles;
	protected int extractResultNoScriptRoleTypes;
	protected int extractResultNoEventType;

	protected int extractResultNoScriptVersion;
	protected int extractResultNoScript;
	protected int extractResultNoItem;
	protected int extractResultNoUser;
	protected int extractResultNoWorkgroup;
	protected int extractResultNoScriptResults;
	protected int extractResult;

	protected String extractResultIgnoreRoles;
	
	
	//FDP1183 CM 18/02/2016
	protected int extractScriptIdNotExist;
	protected int extractScriptIdExists;
	//FDP1183 CM End

	// Work Order Attachment Download

	protected String woAttachmentValidWoNo;
	protected String woAttachmentValidDistrict;

	protected String woAttachmentInvalidWoNo;
	protected String woAttachmentInvalidDistrict;

	protected String woAttachmentValidFileName;
	protected String woAttachmentInvalidFileName;
	protected String woAttachmentMaxSizeFileName;

	protected String retrieveWorkOrderAttachmentDir;

	// User Properties

	protected String userNotInFieldreachCode;
	protected String userNotInFieldreachPassword;

	protected String userInPrimaryAndSecondaryCode;
	protected String userInPrimaryAndSecondaryPassword;

	protected String userNotInPrimaryCode;
	protected String userNotInPrimaryPassword;

	protected String userExpiredCode;
	protected String userExpiredPassword;

	// Config List properties

	protected String configList_workgroup;
	protected List<String> configList_expectedConfigs1;
	protected List<String> configList_expectedChecksums1;

	protected List<String> configList_expectedConfigs2;
	protected List<String> configList_expectedChecksums2;

	// Script list properties

	protected String scriptList_profileIWG;
	protected String scriptList_profileCWG;

	protected List<String> scriptList_expectedScriptsI;
	protected List<String> scriptList_expectedScriptsC;

	protected int scriptList_onlineScriptCount;

	// Support File list properties

	protected List<String> supportList_expectedSupportFiles;
	protected List<String> suportList_expectedChecksums;

	// Download support file properties

	protected String supportFileValidFileName;
	protected String supportFileInvalidFileName;
	protected String supportFileMaxSizeFileName;
	protected String retrieveSupportFileDir;

	// upload support file properties

	protected String supportFileUploadDir;
	protected String supportFileUploadFile;

	protected String applicationIdentifier;

	protected static ApplicationContext ctx = null;

	// Work Bank Database download properties

	protected String wbDU01FileName;
	protected String wbDU01Checksum;
	protected long wbDU01Size;

	protected String workbankDatabaseInvalid;

	protected String workbankDatabaseFileMaxSizeFileName;

	protected String workbankDatabaseDownloadDir;

	//FDE034 CM 23/09/2015
	protected String scriptQuestionDefValidScriptId;
	protected String scriptQuestionDefValidSeqNo;
	protected String scriptQuestionDefInvalidScriptId;
	protected String scriptQuestionDefInvalidSeqNo;
	protected String scriptQuestionDefAttachmentSeqNo;
	protected String scriptQuestionDefAttachmentValidScriptId;
	protected String scriptQuestionDefBitmapSeqNo;
	protected String scriptQuestionDefCalculationSeqNo;
	protected String scriptQuestionDefEmbeddedSpreadSheetSeqNo;
	protected String scriptQuestionDefEBMSeqNo;
	protected String scriptQuestionDefExtApplicationSeqNo;
	protected String scriptQuestionDefHeadingSeqNo;
	protected String scriptQuestionDefSignatureSeqNo;
	protected String scriptQuestionDefVideoSeqNo;
	protected String scriptQuestionDefVoiceSeqNo;
	protected String scriptQuestionDefPhotoSeqNo;
	protected String scriptQuestionDefConditionSeqNo;
	protected String scriptQuestionDefStringSeqNo;
	protected String scriptQuestionDefNumericSeqNo;
	protected String scriptQuestionDefSinglePickSeqNo;
	protected String scriptQuestionDefSinglePickRBSeqNo;
	protected String scriptQuestionDefSinglePickRBReturnId;
	protected String scriptQuestionDefMultiPickSeqNo;
	protected String scriptQuestionDefMultiPickRBSeqNo;
	protected String scriptQuestionDefMultiPickRBReturnId;
	protected String scriptQuestionDefDecimalSeqNo;
	protected String scriptQuestionDefLevelSeqNo;
	protected String scriptQuestionDefDateSeqNo;
	protected String scriptQuestionDefTimeSeqNo;
	protected String scriptQuestionDefYesNoSeqNo;
	protected String scriptQuestionDefInstructionSeqNo;
	protected String scriptQuestionDefGPSSeqNo;
	protected String scriptQuestionDefTaskListSeqNo;
	protected String scriptQuestionDefFormattedInputSeqNo;
	protected String scriptQuestionDefCSVSeqNo;
	protected String scriptQuestionDefMultiPickRBSciptID;
	//FDE034 CM End
	
	// Personal View properties

	protected String personalViewValidUser;
	protected int personalViewValidUserViewCount;
	protected String personalViewNonSysUser;
	protected String personalViewValidUserNoViews;
	
	// Update Question Response Properties
	
	protected int updateResponseReturnId;
	
	protected List<Integer> updateResponseInvalidResOrder;
	protected List<Integer> updateResponseInvalidSeqNo;
	
	protected List<Integer> updateResponseValidResOrder;
	protected List<Integer> updateResponseValidSeqNo;
	
	// Next Status
	
	protected int nextStatusReturnId;
	protected String currentStatus;
	protected List<String> expectedNextStatusValues;
	
	protected String updateNonSysStatus;
	protected String updateSysStatus;
	
	
	
	
	
	
	//FIELDREACHINT variables!!!
	// Workorder update

	protected String updateWODoesNotExist;
	protected String updateExistingWorkOrderComplete;
	protected String updateExistingWOValidReallocate;
	protected String updateExistingWOInvalid;
	protected String updateExistingWOValid;
	protected String updateReissueCancelled;
	protected String updateReissueCantDo;

	// Cancel Work Order

	protected String WOCancelNotExist;
	protected String WOCancelInvalidStatus;
	protected String WOCancel;
	protected String updateDistrictCodeMissing;
	
	// Attachment registration properties

	protected String attachmentRegUploadDir;

	protected String attachmentRegValidWO;
	protected String attachmentRegInvalidWO;
	protected String attachmentRegCantDoWO;
	protected String attachmentRegCloseWO;
	protected String attachmentRegCancelledWO;

	protected String attachmentRegWORFile;
	protected String attachmentRegDOCFile;
	protected String attachmentRegDescription;
	
	// Update Result Status
	
	protected int updateStatusReturnId;
	
	// Create Result Note
	
	protected int createNoteReturnId;
	protected int createNoteSeqNo;
	protected int createNoteResOrderNo;
	
	
	public CommonBase() {

		try {

			// Load web service configuration.
			try {

				Resource appresource = new ClassPathResource("app.properties");
				applicationProperties = PropertiesLoaderUtils
						.loadProperties(appresource);

			} catch (FileNotFoundException e) {

				log.error("Unable to find the properties file app.properties");

			} catch (IOException e) {

				log.error("Error reading from the properties file app.properties");
			}

			if (ctx == null) {

				ctx = new ClassPathXmlApplicationContext(new String[] {
						"application-webServices.xml",
						"applicationContext-db.xml",
						"applicationContext-hibernate.xml",
						//"springSecurityContext.xml", //TODO SPRINGBOOT all these tests will now fail authentication checks.
						"applicationContext-extractEngine.xml",
						"application-junit.xml" });
			}

			// Get the test controller from the application-JUnit.xml
			// application context file.

			if (this.testController == null)
				this.testController = (BaseTestController) ctx
						.getBean("testController");

			// Get the set of test properties associated with this controller
			// and load them.
			Resource resource = new ClassPathResource(
					testController.getTestPropertiesFile());
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			
			// Load properties

			baseURL = props.getProperty("test.baseURL");
			deviceid = props.getProperty("test.deviceId");
			appCode = props.getProperty("test.appCode");
					
			unrevokedUserCode = props.getProperty("test.unrevokedUserCode1");
			unrevokedPassword = props.getProperty("test.unrevokedPassword1");
			revokedUserCode = props.getProperty("test.revokedUserCode");
			revokedPassword = props.getProperty("test.revokedPassword");
			invalidUserCode = props.getProperty("test.invalidUserCode");
			newPass = props.getProperty("test.newPassword");

			uploadScriptsSourceDirectory = props.getProperty("test.uploadDir");
			legalUploadFileName = props.getProperty("test.legalUploadFileName");

			validScriptId = props.getProperty("test.validScriptId");
			invalidScriptId = props.getProperty("test.invalidScriptId");
			validScriptChecksum = props.getProperty("test.validScriptChecksum");

			scriptConfigFile = props.getProperty("test.scriptConfigFile");
			wmConfigFile = props.getProperty("test.wmConfigFile");
			workGroupScriptConfigFile = props
					.getProperty("test.workGroupScriptConfigFile");
			workGroupWMConfigFile = props
					.getProperty("test.workGroupWMConfigFile");

			scriptChecksum = props.getProperty("test.scriptChecksum");
			wmChecksum = props.getProperty("test.wmChecksum");
			workGroupScriptChecksum = props
					.getProperty("test.workGroupScriptChecksum");
			workGroupWMChecksum = props.getProperty("test.workGroupWMChecksum");

			// Asset Hierarchy download

			DU01FileName = props.getProperty("test.AH.DU01FileName");
			DU01Checksum = props.getProperty("test.AH.DU01Checksum");
			DU01Size = Long.parseLong(props.getProperty("test.AH.DU01Size"));

			assetDatabaseInvalid = props.getProperty("test.AH.invalid");

			assetDatabaseFileMaxSizeFileName = props
					.getProperty("test.AH.maxSizeFileName");

			assetDatabaseDownloadDir = props.getProperty("test.AH.downloadDir");

			// Script reference

			scriptReferenceScriptCode = props
					.getProperty("test.scriptReferenceScriptCode");
			scriptReferenceFile = props.getProperty("test.scriptReferenceFile");
			scriptReferenceFileNotInResource = props
					.getProperty("test.scriptReferenceFileNotInResource");
			scriptReferenceFileMissing = props
					.getProperty("test.scriptReferenceFileMissing");

			// Validation Type List

			validationTypeList = props.getProperty("test.validationTypeList");
			validationTypeNonExistingList = props
					.getProperty("test.validationTypeNonExistingList");
			singlevalidationTypeList = props
					.getProperty("test.singlevalidationTypeList");

			// Script profiles

			scriptProfileI = props.getProperty("test.scriptProfileI");
			scriptProfileIPassword = props
					.getProperty("test.scriptProfileIPassword");
			scriptProfileIScriptList = props
					.getProperty("test.scriptProfileIScriptList");

			scriptProfileC = props.getProperty("test.scriptProfileC");
			scriptProfileCPassword = props
					.getProperty("test.scriptProfileCPassword");

			scriptProfileCScriptList = props
					.getProperty("test.scriptProfileCScriptList");

			// PHASE IV

			configFileDir = applicationProperties
					.getProperty("fileType.config");

			uploadWorkOrderDir = props.getProperty("test.uploadWorkOrderDir");
			existingWO = props.getProperty("test.existingWO");
			invalidWO = props.getProperty("test.invalidWO");
			WONoUserCodeOrWGCode = props
					.getProperty("test.WONoUserCodeOrWGCode");

			validWO1 = props.getProperty("test.validWO1");
			validWO2 = props.getProperty("test.validWO2");
			

			validRELWO1 = props.getProperty("test.validRELWO1");
			validRELWO2 = props.getProperty("test.validRELWO2");

			woDC = props.getProperty("test.woDC");

			// Work Issued

			workissuedUserCode = props.getProperty("test.workissuedUserCode");
			workissuedUserPassword = props.getProperty("test.workissuedUserPassword");
			workIssuedWorkGroupCode = props
					.getProperty("test.workIssuedWorkGroupCode");

			recordsExpectedU = Integer.parseInt(props
					.getProperty("test.recordsExpectedU"));
			recordsExpectedW = Integer.parseInt(props
					.getProperty("test.recordsExpectedW"));
			recordsExpectedMW = Integer.parseInt(props
					.getProperty("test.recordsExpectedMW"));

			// Workorder download

			WOWorkIssuedAndFileSystem = props
					.getProperty("test.WOWorkIssuedAndFileSystem");
			WONotWorkIssued = props.getProperty("test.WONotWorkIssued");
			WOWorkIssuedAndNotFileSystem = props
					.getProperty("test.WOWorkIssuedAndNotFileSystem");
			WODownloadInvalidStatus = props
					.getProperty("test.WODownloadInvalidStatus");

			// Fieldreach transaction processing

			uploadTransDir = props.getProperty("test.uploadTransDir");
			validWorkStatusTrans = props
					.getProperty("test.validWorkStatusTrans");
			invalidWorkStatusTrans = props
					.getProperty("test.invalidWorkStatusTrans");
			nonWorkStatusTrans = props.getProperty("test.nonWorkStatusTrans");
			alreadyProcessedTrans = props
					.getProperty("test.alreadyProcessedTrans");
			notInWorkIssuedTrans = props
					.getProperty("test.notInWorkIssuedTrans");
			validWorkStatusTransCloseWork = props
					.getProperty("test.validWorkStatusTransCloseWork");
			validWorkStatusTransCantDoWork = props
					.getProperty("test.validWorkStatusTransCantDoWork");

			// Script Result Search

			scriptResultSearch_equipNo = props
					.getProperty("test.scriptresultsearch.equipno");
			scriptResultSearch_fromDate = props
					.getProperty("test.scriptresultsearch.fromDate");
			scriptResultSearch_toDate = props
					.getProperty("test.scriptresultsearch.toDate");

			scriptResultSearch_scriptCodeSingle = props
					.getProperty("test.scriptresultsearch.scriptCodeSingle");
			scriptResultSearch_scriptCodeMulti = props
					.getProperty("test.scriptresultsearch.scriptCodeMulti");

			scriptResultSearch_resultStatusSingle = props
					.getProperty("test.scriptresultsearch.resultStatusSingle");
			scriptResultSearch_resultStatusMulti = props
					.getProperty("test.scriptresultsearch.resultStatusMulti");

			scriptResultSearch_userCodeSingle = props
					.getProperty("test.scriptresultsearch.userCodeSingle");
			scriptResultSearch_userCodeMulti = props
					.getProperty("test.scriptresultsearch.userCodeMulti");

			scriptResultSearch_workgroupCodeSingle = props
					.getProperty("test.scriptresultsearch.workgroupCodeSingle");
			scriptResultSearch_workgroupCodeMulti = props
					.getProperty("test.scriptresultsearch.workgroupCodeMulti");

			scriptResultSearch_viewId = props
					.getProperty("test.scriptresultsearch.viewId");			
			scriptResultSearch_workOrderNo = props
					.getProperty("test.scriptresultsearch.workOrderNo");	
			scriptResultSearch_resAssocCodeReturnId = props
					.getProperty("test.scriptresultsearch.resAssocCodeReturnId");	
			
			
			// script result search expected result counts

			scriptResultSearchExpectedResults = new HashMap<Integer, Integer>();

			scriptResultSearchExpectedResults.put(3113,
					Integer.parseInt(props.getProperty("test.3113")));
			scriptResultSearchExpectedResults.put(3114,
					Integer.parseInt(props.getProperty("test.3114")));
			scriptResultSearchExpectedResults.put(3115,
					Integer.parseInt(props.getProperty("test.3115")));
			scriptResultSearchExpectedResults.put(3116,
					Integer.parseInt(props.getProperty("test.3116")));
			scriptResultSearchExpectedResults.put(3117,
					Integer.parseInt(props.getProperty("test.3117")));
			scriptResultSearchExpectedResults.put(3118,
					Integer.parseInt(props.getProperty("test.3118")));
			scriptResultSearchExpectedResults.put(3119,
					Integer.parseInt(props.getProperty("test.3119")));
			scriptResultSearchExpectedResults.put(3120,
					Integer.parseInt(props.getProperty("test.3120")));
			scriptResultSearchExpectedResults.put(3121,
					Integer.parseInt(props.getProperty("test.3121")));
			scriptResultSearchExpectedResults.put(3122,
					Integer.parseInt(props.getProperty("test.3122")));
			scriptResultSearchExpectedResults.put(3123,
					Integer.parseInt(props.getProperty("test.3123")));
			scriptResultSearchExpectedResults.put(3124,
					Integer.parseInt(props.getProperty("test.3124")));
			scriptResultSearchExpectedResults.put(3125,
					Integer.parseInt(props.getProperty("test.3125")));
			scriptResultSearchExpectedResults.put(3126,
					Integer.parseInt(props.getProperty("test.3126")));
			scriptResultSearchExpectedResults.put(3127,
					Integer.parseInt(props.getProperty("test.3127")));
			scriptResultSearchExpectedResults.put(3128,
					Integer.parseInt(props.getProperty("test.3128")));
			scriptResultSearchExpectedResults.put(3129,
					Integer.parseInt(props.getProperty("test.3129")));
			scriptResultSearchExpectedResults.put(3130,
					Integer.parseInt(props.getProperty("test.3130")));
			scriptResultSearchExpectedResults.put(3131,
					Integer.parseInt(props.getProperty("test.3131")));
			scriptResultSearchExpectedResults.put(3132,
					Integer.parseInt(props.getProperty("test.3132")));
			scriptResultSearchExpectedResults.put(3133,
					Integer.parseInt(props.getProperty("test.3133")));
			scriptResultSearchExpectedResults.put(3134,
					Integer.parseInt(props.getProperty("test.3134")));
			scriptResultSearchExpectedResults.put(3135,
					Integer.parseInt(props.getProperty("test.3135")));
			scriptResultSearchExpectedResults.put(3136,
					Integer.parseInt(props.getProperty("test.3136")));
			scriptResultSearchExpectedResults.put(3137,
					Integer.parseInt(props.getProperty("test.3137")));
			scriptResultSearchExpectedResults.put(3138,
					Integer.parseInt(props.getProperty("test.3138")));
			scriptResultSearchExpectedResults.put(3139,
					Integer.parseInt(props.getProperty("test.3139")));
			scriptResultSearchExpectedResults.put(3140,
					Integer.parseInt(props.getProperty("test.3140")));
			scriptResultSearchExpectedResults.put(3141,
					Integer.parseInt(props.getProperty("test.3141")));
			scriptResultSearchExpectedResults.put(3142,
					Integer.parseInt(props.getProperty("test.3142")));
			scriptResultSearchExpectedResults.put(3143,
					Integer.parseInt(props.getProperty("test.3143")));
			scriptResultSearchExpectedResults.put(3144,
					Integer.parseInt(props.getProperty("test.3144")));
			scriptResultSearchExpectedResults.put(3145,
					Integer.parseInt(props.getProperty("test.3145")));
			scriptResultSearchExpectedResults.put(3146,
					Integer.parseInt(props.getProperty("test.3146")));
			scriptResultSearchExpectedResults.put(3147,
					Integer.parseInt(props.getProperty("test.3147")));
			scriptResultSearchExpectedResults.put(3148,
					Integer.parseInt(props.getProperty("test.3148")));
			scriptResultSearchExpectedResults.put(3149,
					Integer.parseInt(props.getProperty("test.3149")));
			scriptResultSearchExpectedResults.put(3150,
					Integer.parseInt(props.getProperty("test.3150")));
			scriptResultSearchExpectedResults.put(3150,
					Integer.parseInt(props.getProperty("test.3150")));
			scriptResultSearchExpectedResults.put(3150,
					Integer.parseInt(props.getProperty("test.3150")));
			scriptResultSearchExpectedResults.put(3151,
					Integer.parseInt(props.getProperty("test.3151")));
			scriptResultSearchExpectedResults.put(3152,
					Integer.parseInt(props.getProperty("test.3152")));
			scriptResultSearchExpectedResults.put(3151,
					Integer.parseInt(props.getProperty("test.3151")));
			scriptResultSearchExpectedResults.put(3152,
					Integer.parseInt(props.getProperty("test.3152")));
			scriptResultSearchExpectedResults.put(3153,
					Integer.parseInt(props.getProperty("test.3153")));
			scriptResultSearchExpectedResults.put(3154,
					Integer.parseInt(props.getProperty("test.3154")));
			scriptResultSearchExpectedResults.put(3155,
					Integer.parseInt(props.getProperty("test.3155")));
			scriptResultSearchExpectedResults.put(3156,
					Integer.parseInt(props.getProperty("test.3156")));
			scriptResultSearchExpectedResults.put(3157,
					Integer.parseInt(props.getProperty("test.3157")));
			scriptResultSearchExpectedResults.put(3158,
					Integer.parseInt(props.getProperty("test.3158")));
			scriptResultSearchExpectedResults.put(3159,
					Integer.parseInt(props.getProperty("test.3159")));
			scriptResultSearchExpectedResults.put(3160,
					Integer.parseInt(props.getProperty("test.3160")));
			scriptResultSearchExpectedResults.put(3161,
					Integer.parseInt(props.getProperty("test.3161")));
			scriptResultSearchExpectedResults.put(3162,
					Integer.parseInt(props.getProperty("test.3162")));
			scriptResultSearchExpectedResults.put(3163,
					Integer.parseInt(props.getProperty("test.3163")));
			scriptResultSearchExpectedResults.put(3164,
					Integer.parseInt(props.getProperty("test.3164")));
			scriptResultSearchExpectedResults.put(3165,
					Integer.parseInt(props.getProperty("test.3165")));
			scriptResultSearchExpectedResults.put(3166,
					Integer.parseInt(props.getProperty("test.3166")));
			scriptResultSearchExpectedResults.put(3167,
					Integer.parseInt(props.getProperty("test.3167")));
			scriptResultSearchExpectedResults.put(3168,
					Integer.parseInt(props.getProperty("test.3168")));
			scriptResultSearchExpectedResults.put(3169,
					Integer.parseInt(props.getProperty("test.3169")));
			scriptResultSearchExpectedResults.put(3170,
					Integer.parseInt(props.getProperty("test.3170")));
			scriptResultSearchExpectedResults.put(3171,
					Integer.parseInt(props.getProperty("test.3171")));
			scriptResultSearchExpectedResults.put(3172,
					Integer.parseInt(props.getProperty("test.3172")));
			scriptResultSearchExpectedResults.put(3173,
					Integer.parseInt(props.getProperty("test.3173")));
			scriptResultSearchExpectedResults.put(3174,
					Integer.parseInt(props.getProperty("test.3174")));
			scriptResultSearchExpectedResults.put(3175,
					Integer.parseInt(props.getProperty("test.3175")));
			scriptResultSearchExpectedResults.put(3176,
					Integer.parseInt(props.getProperty("test.3176")));
			scriptResultSearchExpectedResults.put(3177,
					Integer.parseInt(props.getProperty("test.3177")));
			scriptResultSearchExpectedResults.put(3178,
					Integer.parseInt(props.getProperty("test.3178")));
			scriptResultSearchExpectedResults.put(3179,
					Integer.parseInt(props.getProperty("test.3179")));
			scriptResultSearchExpectedResults.put(3180,
					Integer.parseInt(props.getProperty("test.3180")));
			scriptResultSearchExpectedResults.put(3181,
					Integer.parseInt(props.getProperty("test.3181")));
			scriptResultSearchExpectedResults.put(3182,
					Integer.parseInt(props.getProperty("test.3182")));
			scriptResultSearchExpectedResults.put(3183,
					Integer.parseInt(props.getProperty("test.3183")));
			scriptResultSearchExpectedResults.put(3184,
					Integer.parseInt(props.getProperty("test.3184")));
			scriptResultSearchExpectedResults.put(3185,
					Integer.parseInt(props.getProperty("test.3185")));
			scriptResultSearchExpectedResults.put(3186,
					Integer.parseInt(props.getProperty("test.3186")));
			scriptResultSearchExpectedResults.put(3187,
					Integer.parseInt(props.getProperty("test.3187")));
			scriptResultSearchExpectedResults.put(3188,
					Integer.parseInt(props.getProperty("test.3188")));
			scriptResultSearchExpectedResults.put(3189,
					Integer.parseInt(props.getProperty("test.3189")));
			scriptResultSearchExpectedResults.put(3190,
					Integer.parseInt(props.getProperty("test.3190")));
			scriptResultSearchExpectedResults.put(3191,
					Integer.parseInt(props.getProperty("test.3191")));
			scriptResultSearchExpectedResults.put(3192,
					Integer.parseInt(props.getProperty("test.3192")));
			scriptResultSearchExpectedResults.put(3193,
					Integer.parseInt(props.getProperty("test.3193")));
			scriptResultSearchExpectedResults.put(3194,
					Integer.parseInt(props.getProperty("test.3194")));
			scriptResultSearchExpectedResults.put(3195,
					Integer.parseInt(props.getProperty("test.3195")));
			scriptResultSearchExpectedResults.put(3196,
					Integer.parseInt(props.getProperty("test.3196")));
			scriptResultSearchExpectedResults.put(3197,
					Integer.parseInt(props.getProperty("test.3197")));
			scriptResultSearchExpectedResults.put(3198,
					Integer.parseInt(props.getProperty("test.3198")));
			scriptResultSearchExpectedResults.put(3199,
					Integer.parseInt(props.getProperty("test.3199")));
			scriptResultSearchExpectedResults.put(31100,
					Integer.parseInt(props.getProperty("test.31100")));
			scriptResultSearchExpectedResults.put(31101,
					Integer.parseInt(props.getProperty("test.31101")));
			scriptResultSearchExpectedResults.put(31102,
					Integer.parseInt(props.getProperty("test.31102")));
			scriptResultSearchExpectedResults.put(31103,
					Integer.parseInt(props.getProperty("test.31103")));
			scriptResultSearchExpectedResults.put(31104,
					Integer.parseInt(props.getProperty("test.31104")));
			scriptResultSearchExpectedResults.put(31105,
					Integer.parseInt(props.getProperty("test.31105")));
			scriptResultSearchExpectedResults.put(31106,
					Integer.parseInt(props.getProperty("test.31106")));
			scriptResultSearchExpectedResults.put(31107,
					Integer.parseInt(props.getProperty("test.31107")));
			scriptResultSearchExpectedResults.put(31108,
					Integer.parseInt(props.getProperty("test.31108")));
			scriptResultSearchExpectedResults.put(31109,
					Integer.parseInt(props.getProperty("test.31109")));
			scriptResultSearchExpectedResults.put(31110,
					Integer.parseInt(props.getProperty("test.31110")));
			scriptResultSearchExpectedResults.put(31111,
					Integer.parseInt(props.getProperty("test.31111")));
			scriptResultSearchExpectedResults.put(31115,
					Integer.parseInt(props.getProperty("test.31115")));
			scriptResultSearchExpectedResults.put(31116,
					Integer.parseInt(props.getProperty("test.31116")));
			scriptResultSearchExpectedResults.put(31117,
					Integer.parseInt(props.getProperty("test.31117")));
			
			// script result retrieval

			retrieveScriptResultDir = props
					.getProperty("test.retrieveScriptResultDir");

			retrieveScriptResultExtractionError = props
					.getProperty("test.retrieveScriptResultExtractionError");
			retrieveScriptResultValidResult = props
					.getProperty("test.retrieveScriptResultValidResult");
			binaryResponseResOrderNo = props
					.getProperty("test.binaryResponseResOrderNo");
			nonBinaryResponseResOrderNo = props
					.getProperty("test.nonBinaryResponseResOrderNo");
			retrieveScriptResultUnanswered = props
					.getProperty("test.extractResultUnanswered");
			retrieveScriptResultCannotEdit = props
					.getProperty("test.extractResultCannotEdit");
			retrieveScriptResultCanEdit = props
					.getProperty("test.extractResultCanEdit");
			
			// FDP1177 TM 21/01/2016
			canEditUserCode = props
					.getProperty("test.canEditUserCode");
			canEditPassword = props
					.getProperty("test.canEditPassword");
			// End FDP1177
			
			// extract engine

			extractResultReturnIdMissing = Integer.parseInt(props
					.getProperty("test.extractResultReturnIdMissing"));
			extractResultNoScriptRoles = Integer.parseInt(props
					.getProperty("test.extractResultNoScriptRoles"));
			extractResultNoScriptRoleTypes = Integer.parseInt(props
					.getProperty("test.extractResultNoScriptRoleTypes"));
			extractResultNoEventType = Integer.parseInt(props
					.getProperty("test.extractResultNoEventType"));

			extractResultNoScriptVersion = Integer.parseInt(props
					.getProperty("test.extractResultNoScriptVersion"));
			extractResultNoScript = Integer.parseInt(props
					.getProperty("test.extractResultNoScript"));
			extractResultNoItem = Integer.parseInt(props
					.getProperty("test.extractResultNoItem"));
			extractResultNoUser = Integer.parseInt(props
					.getProperty("test.extractResultNoUser"));
			extractResultNoWorkgroup = Integer.parseInt(props
					.getProperty("test.extractResultNoWorkgroup"));
			extractResultNoScriptResults = Integer.parseInt(props
					.getProperty("test.extractResultNoScriptResults"));
			extractResult = Integer.parseInt(props
					.getProperty("test.extractResult"));

			extractResultIgnoreRoles = props
					.getProperty("test.extractResultIgnoreRoles");

			
			//FDP1183 CM 18/02/2016
			extractScriptIdNotExist = Integer.parseInt(props
					.getProperty("test.extractScriptIdNotExist"));
			
			extractScriptIdExists = Integer.parseInt(props
					.getProperty("test.extractScriptIdExists"));
			//FDP1183 CM End
			
			
			// Work Order attachment download

			woAttachmentValidWoNo = props
					.getProperty("test.workorderAttachment.wono.valid");
			woAttachmentValidDistrict = props
					.getProperty("test.workorderAttachment.district.valid");

			woAttachmentInvalidWoNo = props
					.getProperty("test.workorderAttachment.wono.invalid");
			woAttachmentInvalidDistrict = props
					.getProperty("test.workorderAttachment.district.invalid");

			woAttachmentValidFileName = props
					.getProperty("test.workorderAttachment.filename.valid");
			woAttachmentInvalidFileName = props
					.getProperty("test.workorderAttachment.filename.invalid");
			woAttachmentMaxSizeFileName = props
					.getProperty("test.workorderAttachment.filename.maxsize");

			retrieveWorkOrderAttachmentDir = props
					.getProperty("test.workorderAttachment.downloadDir");

			// User Properties

			userNotInFieldreachCode = props
					.getProperty("test.userNotInFieldreachCode");
			userNotInFieldreachPassword = props
					.getProperty("test.userNotInFieldreachPassword");
			userNotInPrimaryCode = props
					.getProperty("test.userNotInPrimaryCode");
			userNotInPrimaryPassword = props
					.getProperty("test.userNotInPrimaryPassword");
			userExpiredCode = props.getProperty("test.userExpiredCode");
			userExpiredPassword = props.getProperty("test.userExpiredPassword");
			userInPrimaryAndSecondaryCode = props
					.getProperty("test.userInPrimaryAndSecondaryCode");
			userInPrimaryAndSecondaryPassword = props
					.getProperty("test.userInPrimaryAndSecondaryPassword");

			// Config List properties

			configList_workgroup = props
					.getProperty("test.configlist.workgroupCode");

			String expectedConfigs1 = props
					.getProperty("test.configlist.expectedConfigs1");
			String expectedChecksums1 = props
					.getProperty("test.configlist.expectedChecksums1");

			configList_expectedConfigs1 = Arrays.asList(expectedConfigs1
					.split(","));
			configList_expectedChecksums1 = Arrays.asList(expectedChecksums1
					.split(","));

			String expectedConfigs2 = props
					.getProperty("test.configlist.expectedConfigs2");
			String expectedChecksums2 = props
					.getProperty("test.configlist.expectedChecksums2");

			configList_expectedConfigs2 = Arrays.asList(expectedConfigs2
					.split(","));
			configList_expectedChecksums2 = Arrays.asList(expectedChecksums2
					.split(","));

			// Script List properties

			scriptList_profileIWG = props
					.getProperty("test.scriptlist.profileIWG");
			scriptList_profileCWG = props
					.getProperty("test.scriptlist.profileCWG");

			String expectedScriptsI = props
					.getProperty("test.scriptlist.profileIScriptList");
			String expectedScriptsC = props
					.getProperty("test.scriptlist.profileCScriptList");

			scriptList_expectedScriptsI = Arrays.asList(expectedScriptsI
					.split(","));
			scriptList_expectedScriptsC = Arrays.asList(expectedScriptsC
					.split(","));

			scriptList_onlineScriptCount = Integer.parseInt(props
					.getProperty("test.scriptlist.onlineScriptCount"));

			// Support File List properties

			String expectedSupportFiles = props
					.getProperty("test.supportFileList.expectedFiles");
			String expectedChecksums = props
					.getProperty("test.supportFileList.expectedChecksums");

			supportList_expectedSupportFiles = Arrays
					.asList(expectedSupportFiles.split(","));
			suportList_expectedChecksums = Arrays.asList(expectedChecksums
					.split(","));

			// Download support file properties

			supportFileValidFileName = props
					.getProperty("test.supportFile.filename.valid");
			supportFileInvalidFileName = props
					.getProperty("test.supportFile.filename.invalid");
			supportFileMaxSizeFileName = props
					.getProperty("test.supportFile.filename.maxsize");
			retrieveSupportFileDir = props
					.getProperty("test.supportFile.downloadDir");

			// Upload support file properties

			supportFileUploadDir = props
					.getProperty("test.supportFile.uploadDir");
			supportFileUploadFile = props
					.getProperty("test.supportFile.leagaluploadFile");

			// Application identifier

			try {

				applicationIdentifier = props
						.getProperty("test.applicationIdentifier");

				if (applicationIdentifier.trim().equals(""))
					applicationIdentifier = null;
				else
					applicationIdentifier = applicationIdentifier.toLowerCase();

			} catch (Exception e) {

				applicationIdentifier = null;
			}

			// Work Bank Database download properties

			wbDU01FileName = props.getProperty("test.WB.DU01FileName");
			wbDU01Checksum = props.getProperty("test.WB.DU01Checksum");
			wbDU01Size = Long.parseLong(props.getProperty("test.WB.DU01Size"));

			workbankDatabaseInvalid = props.getProperty("test.WB.invalid");

			workbankDatabaseFileMaxSizeFileName = props
					.getProperty("test.WB.maxSizeFileName");

			workbankDatabaseDownloadDir = props
					.getProperty("test.WB.downloadDir");

			//FDE034 CM 2015 
			scriptQuestionDefValidScriptId = props.getProperty("test.scriptQuestionDefValidScriptId");
			scriptQuestionDefValidSeqNo = props.getProperty("test.scriptQuestionDefValidSeqNo");
			scriptQuestionDefInvalidScriptId = props.getProperty("test.scriptQuestionDefInvalidScriptId");
			scriptQuestionDefInvalidSeqNo = props.getProperty("test.scriptQuestionDefInvalidSeqNo");
			scriptQuestionDefAttachmentSeqNo = props.getProperty("test.scriptQuestionDefAttachmentSeqNo");
			scriptQuestionDefAttachmentValidScriptId = props.getProperty("test.scriptQuestionDefAttachmentValidScriptId");
			scriptQuestionDefBitmapSeqNo = props.getProperty("test.scriptQuestionDefBitmapSeqNo");
			scriptQuestionDefCalculationSeqNo = props.getProperty("test.scriptQuestionDefCalculationSeqNo");
			scriptQuestionDefEmbeddedSpreadSheetSeqNo = props.getProperty("test.scriptQuestionDefEmbeddedSpreadSheetSeqNo");
			scriptQuestionDefEBMSeqNo = props.getProperty("test.scriptQuestionDefEBMSeqNo");
			scriptQuestionDefExtApplicationSeqNo = props.getProperty("test.scriptQuestionDefExtApplicationSeqNo");
			scriptQuestionDefHeadingSeqNo = props.getProperty("test.scriptQuestionDefHeadingSeqNo");
			scriptQuestionDefSignatureSeqNo = props.getProperty("test.scriptQuestionDefSignatureSeqNo");
			scriptQuestionDefVideoSeqNo = props.getProperty("test.scriptQuestionDefVideoSeqNo");
			scriptQuestionDefVoiceSeqNo = props.getProperty("test.scriptQuestionDefVoiceSeqNo");
			scriptQuestionDefPhotoSeqNo = props.getProperty("test.scriptQuestionDefPhotoSeqNo");
			scriptQuestionDefConditionSeqNo = props.getProperty("test.scriptQuestionDefConditionSeqNo");
			scriptQuestionDefNumericSeqNo = props.getProperty("test.scriptQuestionDefNumericSeqNo");
			scriptQuestionDefStringSeqNo = props.getProperty("test.scriptQuestionDefStringSeqNo");
			scriptQuestionDefSinglePickSeqNo = props.getProperty("test.scriptQuestionDefSinglePickSeqNo");
			scriptQuestionDefSinglePickRBSeqNo = props.getProperty("test.scriptQuestionDefSinglePickRBSeqNo");
			scriptQuestionDefSinglePickRBReturnId = props.getProperty("test.scriptQuestionDefSinglePickRBReturnId");
			scriptQuestionDefMultiPickSeqNo = props.getProperty("test.scriptQuestionDefMultiPickSeqNo");
			scriptQuestionDefMultiPickRBSeqNo = props.getProperty("test.scriptQuestionDefMultiPickRBSeqNo");
			scriptQuestionDefMultiPickRBReturnId = props.getProperty("test.scriptQuestionDefMultiPickRBReturnId");
			scriptQuestionDefDecimalSeqNo = props.getProperty("test.scriptQuestionDefDecimalSeqNo");
			scriptQuestionDefLevelSeqNo = props.getProperty("test.scriptQuestionDefLevelSeqNo");
			scriptQuestionDefDateSeqNo = props.getProperty("test.scriptQuestionDefDateSeqNo");
			scriptQuestionDefTimeSeqNo = props.getProperty("test.scriptQuestionDefTimeSeqNo");
			scriptQuestionDefYesNoSeqNo = props.getProperty("test.scriptQuestionDefYesNoSeqNo");
			scriptQuestionDefInstructionSeqNo = props.getProperty("test.scriptQuestionDefInstructionSeqNo");
			scriptQuestionDefGPSSeqNo = props.getProperty("test.scriptQuestionDefGPSSeqNo");
			scriptQuestionDefTaskListSeqNo = props.getProperty("test.scriptQuestionDefTaskListSeqNo");
			scriptQuestionDefFormattedInputSeqNo = props.getProperty("test.scriptQuestionDefFormattedInputSeqNo");
			scriptQuestionDefCSVSeqNo = props.getProperty("test.scriptQuestionDefCSVSeqNo");
			scriptQuestionDefMultiPickRBSciptID = props.getProperty("test.scriptQuestionDefMultiPickRBSciptID");
			//FDE034 CM End 
			
			// Personal View properties
			
			 personalViewValidUser = props
						.getProperty("test.pvValidUser");
			 personalViewValidUserViewCount = Integer.parseInt(props
						.getProperty("test.pvValidUserViewCount")); 
			personalViewNonSysUser = props
					.getProperty("test.pvNonSystemUser");
			personalViewValidUserNoViews = props
					.getProperty("test.pvValidUserNoViews");
			
			// Update Question Response properties
			
			updateResponseReturnId = Integer.parseInt(props
						.getProperty("test.updateResponseReturnId")); 
			
			String[] temp = props
					.getProperty("test.updateResponseInvalidResOrders").split(",");
			
			updateResponseInvalidResOrder = new ArrayList<Integer>();
			updateResponseInvalidSeqNo = new ArrayList<Integer>();
			
			
			for(String val : temp) {
				
				String[] temp2 = val.split("-");
				
				updateResponseInvalidSeqNo.add(Integer.parseInt(temp2[0]));
				updateResponseInvalidResOrder.add(Integer.parseInt(temp2[1]));
			}
			
			temp = props
					.getProperty("test.updateResponseValidResOrders").split(",");

			updateResponseValidResOrder = new ArrayList<Integer>();
			updateResponseValidSeqNo = new ArrayList<Integer>();
			
			for(String val : temp) {
				
				String[] temp2 = val.split("-");
				
				updateResponseValidResOrder.add(Integer.parseInt(temp2[0]));
				updateResponseValidSeqNo.add(Integer.parseInt(temp2[1]));
			}
			
			
			// Next Status 
			
			nextStatusReturnId = Integer.parseInt(props.getProperty("test.nextstatus.returnid"));
			
			currentStatus = props.getProperty("test.nextstatus.currentStatus");
			
			expectedNextStatusValues = Arrays.asList(props.getProperty("test.nextstatus.expectedStatusList").split(","));
			
			updateNonSysStatus = props.getProperty("test.updatestatus.nonSystemStatus");
			updateSysStatus = props.getProperty("test.updatestatus.SystemStatus");
			
			
			
			
			
			//FIELDREACHINT VARIABLES!!!

			// workorder update

			updateWODoesNotExist = props
					.getProperty("test.updateWODoesNotExist");
			updateExistingWorkOrderComplete = props
					.getProperty("test.updateExistingWOComplete");
			updateExistingWOValidReallocate = props
					.getProperty("test.updateExistingWOValidReallocate");
			updateExistingWOInvalid = props
					.getProperty("test.updateExistingWOInvalid");
			updateExistingWOValid = props
					.getProperty("test.updateExistingWOValid");
			updateDistrictCodeMissing = props
					.getProperty("test.updateDistrictCodeMissing");
			updateReissueCancelled = props
					.getProperty("test.updateReissueCancelled");
			updateReissueCantDo= props
					.getProperty("test.updateReissueCantDo");


			// Cancel work order

			WOCancelNotExist = props.getProperty("test.WOCancelNotExist");
			WOCancelInvalidStatus = props
					.getProperty("test.WOCancelInvalidStatus");
			WOCancel = props.getProperty("test.WOCancel");
			
			// Attachment registration properties

			attachmentRegUploadDir = props
					.getProperty("test.attachmentReg.uploadDir");

			attachmentRegValidWO = props
					.getProperty("test.attachmentReg.validWO");
			attachmentRegInvalidWO = props
					.getProperty("test.attachmentReg.invalidWO");
			attachmentRegCantDoWO = props
					.getProperty("test.attachmentReg.cantDoWO");
			attachmentRegCloseWO = props
					.getProperty("test.attachmentReg.closeWO");
			attachmentRegCancelledWO = props
					.getProperty("test.attachmentReg.cancelledWO");

			attachmentRegWORFile = props
					.getProperty("test.attachmentReg.WORFileName");
			attachmentRegDOCFile = props
					.getProperty("test.attachmentReg.DOCFileName");
			attachmentRegDescription = props
					.getProperty("test.attachmentReg.description");
			
			// Update Result Status
			updateStatusReturnId = Integer.parseInt(props.getProperty("test.updatestatus.returnid"));
			updateNonSysStatus = props.getProperty("test.updatestatus.nonSystemStatus");
			updateSysStatus = props.getProperty("test.updatestatus.SystemStatus");
					
			// Create Result Note
			
			createNoteReturnId = Integer.parseInt(props.getProperty("test.createresultnote.returnid"));
			createNoteSeqNo = Integer.parseInt(props.getProperty("test.createresultnote.sequenceno"));
			createNoteResOrderNo = Integer.parseInt(props.getProperty("test.createresultnote.resoderno"));		
			
			
		} catch (FileNotFoundException e) {

			log.error("Unable to find the properties file "
					+ testController.getTestPropertiesFile());

		} catch (IOException e) {

			log.error("Error reading from the properties file "
					+ testController.getTestPropertiesFile());
		}

	}

	/**
	 * Method to get an RFC 2612 formatted date.
	 * 
	 * @return RFC 2612 formatted date.
	 */
	protected String getDateHeader() {

		String dateHeader = org.apache.http.client.utils.DateUtils.formatDate(new Date());

		return dateHeader;
	}

	/**
	 * Method to get todays date as a non RFC 2612 formatted date.
	 * 
	 * @return Todays date in non RFC 2612 formatted date (yyyyMMdd).
	 */
	protected String getNonRFC2616Date() {

		Calendar currentDate = Calendar.getInstance();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		return formatter.format(currentDate.getTime());
	}

	/**
	 * Method to return an RFC 2612 formatted date n days before todays date.
	 * 
	 * @param days
	 *            The number of days you want the date to be before todays.
	 * 
	 * @return An RFC 2612 formatted date n days before todays date.
	 */
	protected String getDateBefore(int days) {

		long backDateMS = System.currentTimeMillis() - ((long) days) * 24 * 60
				* 60 * 1000;

		Date backDate = new Date();
		backDate.setTime(backDateMS);
		String dateHeader = org.apache.http.client.utils.DateUtils.formatDate(backDate);

		return dateHeader;
	}

	/**
	 * Method to return an RFC 2612 formatted date n days after todays date.
	 * 
	 * @param days
	 *            The number of days you want the date to be after todays.
	 * 
	 * @return An RFC 2612 formatted date n days after todays date.
	 */
	protected String getDateAfter(int days) {

		long backDateMS = System.currentTimeMillis() + ((long) days) * 24 * 60
				* 60 * 1000;
		Date backDate = new Date();
		backDate.setTime(backDateMS);
		String dateHeader = org.apache.http.client.utils.DateUtils.formatDate(backDate);

		return dateHeader;
	}

	/**
	 * Method to generate a checksum for the data passed in and compare it to a
	 * known checksum.
	 * 
	 * @param checksum
	 *            The known checksum for the data being passed in.
	 * 
	 * @param encodedData
	 *            Base64 encoded data to be decoded and have an MD5 checksum
	 *            generated against.
	 * 
	 * @return If the generated checksum matches the checksum passed in true is
	 *         returned. False is returned if the checksums do not match.
	 */
	protected boolean verifyChecksum(String checksum, String encodedData) {

		boolean result = false;

		byte[] decodedData = Base64.decodeBase64(encodedData);
		String generatedMD5 = Common.generateMD5Checksum(decodedData);

		if (checksum.toUpperCase().equals(generatedMD5.toUpperCase())) {

			result = true;
		}

		return result;
	}

	
	/**
	 * Method to generate an AuthToken from details supplied.
	 * 
	 * @param plaintextuserPass
	 *            Password of the user to generate the AuthToken for. The
	 *            password is provided in plaintext form
	 * 
	 * @param dateHeader
	 *            The date to be supplied in the header along with the
	 *            AuthToken. This token is for Versioned web services.
	 * 
	 * @return
	 */
	protected String getAuthToken(String plaintextuserPass, String dateHeader) {

		return getAuthToken(plaintextuserPass, dateHeader, null);
	}

	protected String getAuthToken(String plaintextuserPass, String dateHeader,
			String algorithm) {

		log.debug(">>> getAuthToken plain=" + plaintextuserPass + " date="
				+ dateHeader + " alg=" + algorithm);

		String signature = null;
		String secretKey = null;

		try {

			if (algorithm == null || "SHA256".equalsIgnoreCase(algorithm))
				secretKey = SecurityUtils
						.generateBase64SHA256HashForString(plaintextuserPass);
			else if ("Fieldreach".equalsIgnoreCase(algorithm))
				secretKey = encryptFRPassword(plaintextuserPass);

			signature = SecurityUtils.getSignature(secretKey, dateHeader);

		} catch (Exception ex) {

			log.error("Error in getAuthToken " + ex.getMessage() + " "
					+ Utils.getStackTrace(ex));
		}

		log.debug("<<< getAuthToken sig=" + signature);

		return signature;
	}

	/**
	 * Generate Authorisation header from userCode and plaintext Header and Date
	 * header supplied
	 * 
	 * @param userCode
	 * @param plainTextUserPassword
	 * @param dateHeader
	 * @return Authorisation header made up of signed Date header string and
	 *         encrypted password
	 */
	protected String getAuthorisationHeaderValue(String userCode,
			String plainTextUserPassword, String dateHeader) {

		log.debug(">>> getAuthorisationHeaderValue user=" + userCode
				+ " plain=" + plainTextUserPassword + " date=" + dateHeader);

		String authHeader = null;

		try {

			String authToken = getAuthToken(plainTextUserPassword, dateHeader);
			String aesPassword = encodeAESPassword(plainTextUserPassword, false);
			authHeader = "FWS " + userCode + ":" + authToken + ":"
					+ aesPassword;

		} catch (Exception ex) {

			log.error("Error in getAuthorisationHeaderValue " + ex.getMessage()
					+ " " + Utils.getStackTrace(ex));
		}

		log.debug("<<< getAuthorisationHeaderValue authHeader=" + authHeader);

		return authHeader;
	}
	
	/**
	 * FDE044 - MC 
	 * @param userCode
	 * @param plainTextUserPassword
	 * @param dateHeader
	 * @return
	 */
	protected String getIWSAuthorisationHeaderValue(String userCode,
			String plainTextUserPassword, String dateHeader) {

		log.debug(">>> getIWSAuthorisationHeaderValue user=" + userCode
				+ " plain=" + plainTextUserPassword + " date=" + dateHeader);

		String authHeader = null;

		try {

			String authToken = getAuthToken(plainTextUserPassword, dateHeader);
			String aesPassword = encodeAESPassword(plainTextUserPassword, true);
			authHeader = "IWS " + userCode + ":" + authToken + ":"
					+ aesPassword;

		} catch (Exception ex) {

			log.error("Error in getIWSAuthorisationHeaderValue " + ex.getMessage()
					+ " " + Utils.getStackTrace(ex));
		}

		log.debug("<<< getIWSAuthorisationHeaderValue authHeader=" + authHeader);

		return authHeader;
	}

	/**
	 * Encode Values using AES with internal key
	 * 
	 * @param value
	 *            - value to be encrypted
	 * @return AES encrypted version of value using internal key
	 */
	//TODO SPRINGBOOT - just returning null here as all these tests will need to be reevaluated
	protected String encodeAESPassword(String value, boolean useConfiguredKey) {

		// Key for AES.
		return null;
	}

	/**
	 * Encode Values using AES using given key
	 * 
	 * @param value
	 *            - value to be encrypted
	 * @return AES encrypted version of value using given key
	 */
	private String encodeAESPassword(String value, byte[] _key) {

		final String AES_ALGORITHM = "AES";

		try {

			SecretKeySpec skeySpec = new SecretKeySpec(_key, "AES");
			Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			String encrypt = (new Base64()).encodeAsString(cipher.doFinal(value
					.getBytes()));

			return encrypt;

		} catch (NoSuchAlgorithmException ex) {

			log.warn("Error in encode AES " + ex.getMessage());

		} catch (IllegalBlockSizeException ex) {

			log.warn("Error in encode AES " + ex.getMessage());

		} catch (BadPaddingException ex) {

			log.warn("Error in encode AES " + ex.getMessage());

		} catch (InvalidKeyException ex) {

			log.warn("Error in encode AES " + ex.getMessage());

		} catch (NoSuchPaddingException ex) {

			log.warn("Error in encode AES " + ex.getMessage());
		}

		return null;
	}

	/**
	 * Encode a given value using Fieldreach algorithm
	 * 
	 * @param plainTextPassword
	 * @return given value encoded using Fieldreach algorithm
	 */
	//TODO SPRINGBOOT - need to reevaluate all these tests, just returning null here to fix compile errors
	protected String encryptFRPassword(String plainTextPassword) {

		return null;
	}

}
