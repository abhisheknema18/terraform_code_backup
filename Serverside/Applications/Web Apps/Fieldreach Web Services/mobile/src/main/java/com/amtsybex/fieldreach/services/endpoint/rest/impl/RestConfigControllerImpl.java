/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 *  
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.ConfigListResponse.APPLICATION_VND_FIELDSMART_CONFIG_LIST_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.ConfigListResponse.APPLICATION_VND_FIELDSMART_CONFIG_LIST_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.GetConfigResponse.APPLICATION_VND_FIELDSMART_CONFIG_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.GetConfigResponse.APPLICATION_VND_FIELDSMART_CONFIG_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WORKGROUP_CODE_DESCRIPTION;

import java.io.File;
import java.io.FilenameFilter;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.ConfigurationFiles;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.backend.service.ConfigurationService;
import com.amtsybex.fieldreach.services.endpoint.rest.RestConfigController;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.messages.response.ConfigListResponse;
import com.amtsybex.fieldreach.services.messages.response.GetConfigResponse;
import com.amtsybex.fieldreach.services.messages.types.ConfigFile;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "User & Config")
public class RestConfigControllerImpl extends BaseControllerImpl implements RestConfigController {

	private static final Logger log = LoggerFactory.getLogger(RestConfigControllerImpl.class.getName());

	@Autowired
	private UserService userService;
	
	@Autowired
	private ConfigurationService configurationService;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestConfigController#
	 * getConfigFile(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@GetMapping(value = "/config/{id:.*}", 
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CONFIG_1_JSON, APPLICATION_VND_FIELDSMART_CONFIG_1_XML})
	@ApiOperation(value = "Get Configuration File", 
			notes = "This service provides client applications with a means to retrieve a specified configuration file. " +
					"The web service will return the contents of the requested config file encoded in base 64")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FileNotFoundException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - if the id parameter is not supplied"),

	})
	public ResponseEntity<GetConfigResponse> getConfigFile(@RequestHeader HttpHeaders httpHeaders,
		   @ApiParam(value = "The name of the config file that should be retrieved") @PathVariable("id") String configFileName) {

		if (log.isDebugEnabled())
			log.debug(">>> getConfigFile fileName=" + Common.CRLFEscapeString(configFileName));

		GetConfigResponse getConfigresponse = new GetConfigResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		getConfigresponse.setError(errorMessage);

		try {

			// FDE019 05/09/2012 TM
			// Moved debug of header to Utility Method
			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			// End FDE019

			// FDE019 05/09/2012 TM
			// Modified Request URL so there is no need to append .xml extension
			// to is parameter
			
		
			 
			getConfigresponse.setConfigFileSource(this.getConfigFileContents(httpHeaders, configFileName));


		}  catch (ResourceNotFoundException e) {

			log.error("Resource file not found " + Common.CRLFEscapeString(configFileName) + " type="
					+ Common.CRLFEscapeString(Utils.CONFIG_FILE_TYPE));
			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription("Resource File not found");

		}
		 catch (Exception ex) {

			log.error("Exception in get Config File " + ex.getMessage());
			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(ex.getMessage());

		} finally {
			
			getConfigresponse.setSuccess(errorMessage.getErrorCode() == null);
		}

		if (log.isDebugEnabled())
			log.debug("<<< getConfigFile");

		return ResponseEntity.ok(getConfigresponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestConfigController#
	 * getConfigList(javax.servlet.http.HttpServletRequest)
	 */
	@GetMapping(value = "/config/list",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CONFIG_LIST_1_JSON, APPLICATION_VND_FIELDSMART_CONFIG_LIST_1_XML})
	@ApiOperation(value = "Get Configuration File List",
			notes = "This service provides client applications with a means to retrieve a list of configuration files " +
					"that are available to download for a particular workgroup")
	
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<ConfigListResponse> getConfigList( @RequestHeader HttpHeaders httpHeaders) throws AuthenticationException {

	    String workgroupCode= null;
		if (log.isDebugEnabled())
			log.debug(">>> getConfigList");

		// Initialise response
		ConfigListResponse configListResponse = new ConfigListResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		configListResponse.setError(errorMessage);

		// Get application identifier
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);
		try {
		    HPCUsers user = this.getUserDetailsFromUserPrincipal(applicationIdentifier);
	        workgroupCode = user.getId().getWorkgroupCode();
			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			if (workgroupCode == null)
				workgroupCode = "";

			// Search database for workgroup code
			HPCWorkGroups objWorkgroup = userService.findHPCWorkGroup(
					applicationIdentifier, workgroupCode);

			// Determine config list.
			List<ConfigFile> dbConfigList = getAllConfigs(applicationIdentifier, objWorkgroup);

			// Set config list in the response.
			configListResponse.setConfigList(dbConfigList);

		}
		catch(AuthenticationException ex) {
            log.warn(ex.getMessage());
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), ex);
        }
		catch (FRInstanceException e) {

			log.error("error in /config/list: " + e.getMessage()
					+ Utils.getStackTrace(e));

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} finally {
			
			configListResponse.setSuccess(errorMessage.getErrorCode() == null);
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< getConfigList");

		return ResponseEntity.ok(configListResponse);
	}

	/**
	 * Get all Configuration files
	 * 
	 * @param applicationIdentifier
	 * 
	 * @param objWorkgroup
	 * 
	 * @return List<ConfigFile>
	 * 
	 * @throws FRInstanceException
	 */
	private List<ConfigFile> getAllConfigs(String applicationIdentifier, HPCWorkGroups objWorkgroup)
			throws FRInstanceException {
		
		List<ConfigFile> dbConfigList = new ArrayList<>();
		List<ConfigurationFiles> viewConfigList = configurationService.getConfigurationFiles(applicationIdentifier);
		
		if(null == viewConfigList || viewConfigList.isEmpty()) {
			return dbConfigList;
		}
		
		String workGroupCode = StringUtils.EMPTY;
		if(null != objWorkgroup) {
			workGroupCode = objWorkgroup.getWorkgroupCode();
		}

		String userWgWmConfigFileName = Utils.WMCONFIG_FILE_NAME + Utils.SCRIPT_FILE_SEPARATOR+ workGroupCode + Utils.CONFIG_FILE_EXTENSION;
		String userWgScriptConfigFileName =  Utils.SCRIPTCONFIG_FILE_NAME + Utils.SCRIPT_FILE_SEPARATOR+ workGroupCode + Utils.CONFIG_FILE_EXTENSION;
		
		boolean isWgWmConfig  = false;
		boolean isWgScriptConfig = false;
		
		if(!StringUtils.isEmpty(workGroupCode)) {
			
			isWgWmConfig = viewConfigList.stream().anyMatch(cf -> cf.getConfigFileName().equals(userWgWmConfigFileName));
			isWgScriptConfig = viewConfigList.stream().anyMatch(cf -> cf.getConfigFileName().equals(userWgScriptConfigFileName));
		}
		
		String wfFileName =  isWgWmConfig ? userWgWmConfigFileName :Utils.WMCONFIG_FILE_NAME + Utils.CONFIG_FILE_EXTENSION;
		String scipfConfigFilename =  isWgScriptConfig ? userWgScriptConfigFileName : Utils.SCRIPTCONFIG_FILE_NAME + Utils.CONFIG_FILE_EXTENSION;
		
		Predicate<ConfigurationFiles> isFieldReachConfig = (cf) -> {
			
			if(!cf.getConfigFileName().endsWith(Utils.CONFIG_FILE_EXTENSION)) {
				return false;
			}
			if(cf.getConfigFileName().startsWith( Utils.WMCONFIG_FILE_NAME) && !cf.getConfigFileName().equals(wfFileName)) {
				return false;
			}
			else if(cf.getConfigFileName().startsWith( Utils.SCRIPTCONFIG_FILE_NAME) && !cf.getConfigFileName().equals(scipfConfigFilename)) {
				return false;
			}
			return true;
		};
		
		dbConfigList  = viewConfigList.stream().filter(isFieldReachConfig).map(this::getConfigFile).collect(Collectors.toList());
		
		return dbConfigList;
	}
	
	/**
	 * Prepare configFile object using ConfigurationFiles details 
	 * 
	 * @param fileDetails
	 *        Configuration details
	 *        
	 * @return
	 */
	private ConfigFile getConfigFile(ConfigurationFiles fileDetails) {
		
		ConfigFile config = new ConfigFile();
		config.setFileName(fileDetails.getConfigFileName());
		config.setCheckSum(fileDetails.getCheckSum());
		return config;
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/



	/**
	 * Get the requested config file contents from the database 
	 * 
	 * @param httpHeaders
	 *        http headers
	 *        
	 * @param configFileName
	 *        Filename to fetch the contents 
	 *        
	 * @return String
	 *        Actual file contantes in base64encoded format 
	 *        
	 * @throws FRInstanceException
	 *         will be thrown if error while fetching file from database
	 *         
	 * @throws ResourceNotFoundException
	 *        Will be thrown if requested file does not exists in the database 
	 */
	private String getConfigFileContents(HttpHeaders httpHeaders, String configFileName) throws FRInstanceException, ResourceNotFoundException {
		
		if (log.isDebugEnabled()) {

			log.debug(">>> getConfigFileContents fileName=" + Common.CRLFEscapeString(configFileName));
		}
		
		ConfigurationFiles configurationFile =  configurationService.getConfigurationFilesByName(Utils.extractApplicationIdentifier(httpHeaders), configFileName);
		if(null == configurationFile) {
			
			log.error("Configuration file does not exists "+configFileName);
			throw new ResourceNotFoundException("Configuration file does not exists "+configFileName);
		}
		
		byte[] configFileBytes = null;
		try {
			
			Blob fileBlob = configurationFile.getConfigFile();
			configFileBytes = fileBlob.getBytes(1, (int) fileBlob.length());
			fileBlob.free();
		} catch (SQLException e) {
			
			log.error("SQLEXception while fetching bytes from blob {}", e);
			throw new FRInstanceException(e.getMessage());
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< getConfigFileContents");
		
		return  Common.encodeBase64(configFileBytes);
	}

}
