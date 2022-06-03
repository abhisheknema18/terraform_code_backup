/**
 * Author:  T Goodwin
 * Date:    29/05/2012
 * Project: FDE018
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
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.ValidationTypeResponse.APPLICATION_VND_FIELDSMART_VALIDATION_TYPE_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.ValidationTypeResponse.APPLICATION_VND_FIELDSMART_VALIDATION_TYPE_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.ValidationType;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.services.endpoint.rest.RestValidationTypeController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.response.ValidationTypeResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.Validation;
import com.amtsybex.fieldreach.services.messages.types.ValidationProperty;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.services.utils.xml.XMLUtils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "User & Config")
public class RestValidationTypeControllerImpl extends BaseControllerImpl
		implements RestValidationTypeController {

	private static final Logger log = LoggerFactory.getLogger(RestValidationTypeControllerImpl.class.getName());
	
	@Autowired
	private ValidationTypeService validationTypeService;

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestValidationTypeController
	 * #getValidationTypeList(javax.servlet.http.HttpServletRequest,
	 * java.lang.String)
	 */
	@Deprecated
	@Override
	@PostMapping(value = "/validationType/list",
	        produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_VALIDATION_TYPE_1_JSON, APPLICATION_VND_FIELDSMART_VALIDATION_TYPE_1_XML},
	        consumes = { APPLICATION_XML, APPLICATION_VND_FIELDSMART_VALIDATION_TYPE_1_XML})
	@ApiOperation(value = "Get Validation Lists", 
			notes = "The web service provides a means to access the validation type and property lists within the Fieldreach database. " +
					"The client must first create a list of those validation types it wants to retrieve and send as a valid ValidationTypeRequest. " +
					"It can also send an optional checksum. " +
					"The web service will retrieve the validation properties of the validation types listed and " +
					"return these in a ValidationTypeResponse message. " +
					"If the device has passed a checksum, and this matches the checksum the web service will " +
					"calculate on the list to be returned, then the web service will only return a success message " +
					"with checksummatch element set to 1 and not return any property data")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include XmlMappingException, IOException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Malformed or missing xml in the request body." )
	})
	@ApiImplicitParams({
        @ApiImplicitParam(name = "validationTypeRequestMessage", required = true, dataType = "com.amtsybex.fieldreach.swagger.model.ValidationTypeRequest", paramType = "body")
	})
	public ResponseEntity<ValidationTypeResponse> getValidationTypeList(@RequestHeader HttpHeaders httpHeaders,
																		@RequestBody String validationTypeRequestMessage) throws Exception {

		if (log.isDebugEnabled())
			log.debug(">>> getValidationTypeList");

		ValidationTypeResponse validationTypeResponse = new ValidationTypeResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		validationTypeResponse.setValidations(null);

		// Returned types.
		List<Validation> lstMsgTypes = new ArrayList<>();

		// Get application identifier
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);

		try {

			// FDE019 05/09/2012 TM
			// Moved Header Debug to Utility method
			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			// End FDE019

			if (log.isDebugEnabled())
				log.debug("Body :" + Common.CRLFEscapeString(validationTypeRequestMessage));

			// Body passed?
			if (validationTypeRequestMessage == null
					|| validationTypeRequestMessage.trim().equals("")) {

				throw new BadRequestException(
						"Empty Body in validationTypeRequest");
			}

			List<String> lstValidationTypes = parseRequestXML(validationTypeRequestMessage);

			if (lstValidationTypes == null)
				throw new BadRequestException("No VALIDATIONTYPES specified");

			// Retrieve list of validation types requested..
			for (String msgValidationType : lstValidationTypes) {

				if (log.isDebugEnabled())
					log.debug("Validation Type :" + Common.CRLFEscapeString(msgValidationType));

				ValidationType objValidationType = validationTypeService
						.getValidationType(applicationIdentifier,
								msgValidationType);

				if (objValidationType != null) {

					validationTypeResponse.setValidations(lstMsgTypes);

					List<com.amtsybex.fieldreach.backend.model.ValidationProperty> lstProperties = validationTypeService
							.getValidationPropertyByValidationTypeWeightScoreDesc(
									applicationIdentifier, msgValidationType);

					// Look for properties of each validation type and add to
					// message
					if (lstProperties != null && lstProperties.size() > 0) {

						if (log.isDebugEnabled()) {

							log.debug("Validation property count :"
									+ lstProperties.size());
						}

						Validation retValidationType = new Validation();

						retValidationType.setValidationType(objValidationType
								.getId());

						retValidationType.setValidationDesc(objValidationType
								.getDescription());

						List<ValidationProperty> lstMsgProperties = new ArrayList<>();

						for (com.amtsybex.fieldreach.backend.model.ValidationProperty objProperty : lstProperties) {

							if (log.isDebugEnabled()) {

								log.debug("Property : "
										+ objProperty.getValidationProperty());
							}

							ValidationProperty msgProperty = new ValidationProperty();

							msgProperty.setEquivValue(objProperty
									.getEquivValue());

							msgProperty.setColor(objProperty.getColour());
							msgProperty.setName(objProperty
									.getValidationProperty());

							// Set weight score if it is not null
							if (objProperty.getWeightScore() != null)
								msgProperty.setWeightScore(objProperty
										.getWeightScore());

							lstMsgProperties.add(msgProperty);
						}

						// Add back into response.
						retValidationType
								.setValidationProperties(lstMsgProperties);

						lstMsgTypes.add(retValidationType);
					}
				}
			}

			// Have set of validation types to return.
			// if device has sent a checksum, then we need to check if details
			// are to be returned or not. if checksums match then no, dont
			// return

			String checkSum = getChecksumFromXML(validationTypeRequestMessage);
			if (checkSum != null) {

				StringBuilder strBuilder = new StringBuilder();

				if (validationTypeResponse.getValidations() != null) {

					for (Validation wrkValidationType : validationTypeResponse
							.getValidations()) {

						StringWriter ioStream = new StringWriter();

						try {

							restApiMarshaller.marshal(wrkValidationType,
									new StreamResult(ioStream));

							strBuilder.append(ioStream.getBuffer().toString());

						} catch (XmlMappingException e) {

							log.error("XmlMappingException in /validationType/list "
									+ e.getMessage());

						} catch (IOException e) {

							log.error("IOException in /validationType/list "
									+ e.getMessage());
						}
					}
				}

				// Bug 2261 TM 04/09/2013

				byte[] valBytes;

				try {

					valBytes = strBuilder.toString().getBytes(Common.UTF8_ENCODING);

				} catch (UnsupportedEncodingException e) {

					valBytes = strBuilder.toString().getBytes();
				}

				// Calculate and compare checksums
				String md5Checksum = Common.generateMD5Checksum(valBytes);

				// End Bug 2261

				if (checkSum.equals(md5Checksum)) {

					// Checksums match, remove all validation types from
					// response
					if (log.isDebugEnabled())
						log.debug("Checksum match - remove list");

					validationTypeResponse.setValidations(null);
					validationTypeResponse.setChecksumMatch(1);

				} else {

					if (log.isDebugEnabled())
						log.debug("Checksum mismatch");
				}
			}

			validationTypeResponse.setSuccess(true);

		} catch (BadRequestException e) {

			// In case of BadRequestException failure.. rethrow for handler to
			// set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (XmlMappingException e) {

			log.error("XmlMappingException in /validationType/list "
					+ e.getMessage());

			// if we are unable to parse the XML it is invalid and a 400
			// BadRequest
			// needs to be returned.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception ex) {

			log.error("Exception in /validationType/list " + ex.getMessage()
					+ Utils.getStackTrace(ex));

			validationTypeResponse.getError().setErrorCode(
					Utils.GENERAL_EXCEPTION);
			validationTypeResponse.getError().setErrorDescription(
					ex.getMessage());
		}
		finally {
			
			if (errorMessage.getErrorCode() != null) {

				validationTypeResponse.setError(errorMessage);
				validationTypeResponse.setSuccess(false);
			}
			
		}
	
		if (log.isDebugEnabled())
			log.debug("<<< getValidationTypeList response=" + validationTypeResponse);

		return ResponseEntity.ok(validationTypeResponse);
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Parse request XML and return list of VALIDATIONTYPE elements
	 * 
	 * @param requestXML
	 *            - VALIDATIONTYPEREQUEST xml
	 * @return list of VALIDATIONTYPE elements defined in XML If none specified,
	 *         then null returned.
	 */
	private List<String> parseRequestXML(String requestXML) {

		if (log.isDebugEnabled())
			log.debug(">>> parseRequestXML requestXML=" + Common.CRLFEscapeString(requestXML));

		List<String> result = new ArrayList<String>();

		try {

			// FDE019 05/09/2012 TM
			// Use XML utility class
			XMLUtils parser = new XMLUtils();
			Document doc = parser.parseXML(requestXML);
			// End FDE019
			NodeList nodes = doc.getElementsByTagName("VALIDATIONTYPE");

			// Parse all validationtype elements
			for (int i = 0; i < nodes.getLength(); i++) {

				Element typeElement = (Element) nodes.item(i);
				String validationType = typeElement.getTextContent();
				result.add(validationType);

				if (log.isDebugEnabled())
					log.debug(Common.CRLFEscapeString(validationType));
			}

		} catch (Exception e) {

			log.error("Error parsing XML: " + e.toString());
		}

		if (result.size() == 0)
			result = null;

		if (log.isDebugEnabled())
			log.debug("<<< parseRequestXML");

		return result;
	}

	/**
	 * Retrieve CHECKSUM element text from request XML
	 * 
	 * @param requestXML
	 *            - VALIDATIONTYPEREQUEST XML
	 * @return null if CHECKSUM element not specified, else test from CHECKSUM
	 *         element
	 */
	private String getChecksumFromXML(String requestXML) {

		if (log.isDebugEnabled())
			log.debug(">>> getChecksumFromXML requestXML=" + Common.CRLFEscapeString(requestXML));

		String result = null;

		try {

			// FDE019 05/09/2012 TM
			// Use XMl utility class
			XMLUtils parser = new XMLUtils();
			Document doc = parser.parseXML(requestXML);
			// End FDE019
			NodeList nodes = doc.getElementsByTagName("CHECKSUM");
			// There should one be one checksum element
			Element pwElement = (Element) nodes.item(0);
			result = pwElement.getTextContent();

			if (log.isDebugEnabled())
				log.debug(Common.CRLFEscapeString(result));

		} catch (Exception e) {

			log.error("Error parsing XML: " + e.toString());
		}

		if (log.isDebugEnabled())
			log.debug("<<< getChecksumFromXML");

		return result;
	}

}
