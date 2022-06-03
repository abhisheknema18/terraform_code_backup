/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Author:  T Goodwin
 * Date:    10/07/2012
 * Project: FDE018
 * Description : Add in support for app identifiers
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import java.io.FileNotFoundException;
import java.util.List;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.services.exception.ResponseUpdateNotSupportedException;
import com.amtsybex.fieldreach.services.exception.ScriptItemNotFoundException;
import com.amtsybex.fieldreach.services.exception.ScriptResultNotFoundException;
import com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;

public interface ScriptController {

	/**
	 * Retrieve source for given script id.
	 * 
	 * @param scriptId
	 *            scriptid of the script to retrieve the source of.
	 * 
	 * @param applicationIdentifier
	 *            application identifier supplied with the original request.
	 * 
	 * @return Base64 encoded contents of the script file requested.
	 * 
	 * @throws ResourceNotFoundException
	 *             Script file could not be found.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance associated with application identifier is
	 *             not configured. An exception occurred accessing the
	 *             Fieldreach instance.
	 * @throws FileNotFoundException 
	 */
	public String getScriptSourceContents(Integer scriptId, String applicationIdentifier)
			throws ResourceNotFoundException, FRInstanceException, FileNotFoundException;

	//
	// FDE020 TM 23/01/2013

	/**
	 * Returns a Script object that corresponds to a ScriptVersion object by
	 * using the scriptID supplied.
	 * 
	 * @param applicationIdentifier
	 *            Application identifier supplied with original request.
	 * 
	 * @param scriptId
	 *            scriptid to search by.
	 * 
	 * @return Script object associated with the ScriptVersion specified by the
	 *         scriptId supplied.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance associated with application identifier is
	 *             not configured. An exception occurred accessing the
	 *             Fieldreach instance.
	 */
	public Script getScriptByScriptId(String applicationIdentifier, Integer scriptId) throws FRInstanceException;

	// FDP998 TM 20/09/2013
	// Modify method signature to include extra parameters

	// FDP1009 TM 17/12/2013
	// Modified method signature.

	// FDE034 TM 18/08/2015
	// Modified method signature.
	
	// FDP1152 TM 06/11/2015
	// Modified method signature - Added systemUserCode parameter.
	/**
	 * Method to return a list of entries from the returned scripts table that
	 * match the criteria passed in. Any parameter passed can be null, and null
	 * parameters will be ignored. If all parameters are null, then null will be
	 * returned.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param systemUserCode
	 *            System User Code to use to apply FDM system access group filters to. If
	 *            null then no filtering is applied.
	 *            
	 * @param equipNo
	 *            The equipno of the asset the script result was completed
	 *            against.
	 * 
	 * @param fromDate
	 *            Return script results where the completed date is >= supplied
	 *            value.
	 * 
	 * @param toDate
	 *            Return script results where the completed date is <= supplied
	 *            value.
	 * 
	 * @param scriptCodes
	 *            Return script results where the script code matches any of
	 *            those supplied.
	 * 
	 * @param resultStatusList
	 *            Return script results where the result status matches any of
	 *            those supplied.
	 * 
	 * @param userCodes
	 *            Return script results where the complete user matches any of
	 *            those supplied.
	 * 
	 * @param workgroupCodes
	 *            Return script results where the workgroup matches any of those
	 *            supplied.
	 * 
	 * @param altEquipRef
	 *            Return script results where the altEquipRef matches that
	 *            supplied.
	 * 
	 * @param workOrderNo
	 *            Return script results where the workorderno matches that
	 *            supplied. Must appear along with no other parameters.
	 * 
	 * @param resAssocReturnId
	 *            Get the resassoccode of the returnid supplied in this
	 *            parameter. Then use the reassoccode to return script results
	 *            where the resassoccode matches that resolved. Must appear
	 *            along with no other parameters.
	 * 
	 * @param viewId
	 *            Id of personal view to use to perform a search. Must appear
	 *            along with no other parameters.
	 * 
	 * @return List of ReturnedScripts that match the criteria supplied. Returns
	 *         null of no records are found.
	 * 
	 * @throws MaxResultsExceededException
	 *             The search returns more than the maximum number of result
	 *             sets allowed.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public List<ReturnedScripts> getScriptResults(String frInstance, String systemUserCode, String equipno, Integer fromDate, Integer toDate,
			List<String> scriptCodes, List<String> resultStatusList, List<String> userCodes,
			List<String> workgroupCodes, String altEquipRef, String workOrderNo, Integer resAssocReturnId,
			Integer viewId) throws MaxResultsExceededException, FRInstanceException;

	// End FDE020

	// FDP987 TM 13/09/2013

	/**
	 * Accepts a list of script codes that should be omitted from Asset History
	 * searches.
	 * 
	 * @param scriptCodeList
	 *            Comma delimited list of script codes that are to be omitted
	 *            from any Asset History searches.
	 */
	public void setOmitFromAHSearch(String scriptCodeList);

	/**
	 * Gets the comma delimited list of script codes to be omitted from Asset
	 * History searches.
	 * 
	 * @return Comma delimited list of script codes that will be omitted from
	 *         Asset History searches.
	 */
	public String getOmitFromAHSearch();

	/**
	 * Gets the list of script codes to be omitted from Asset History searches.
	 * 
	 * @return List of script codes that will be omitted from Asset History
	 *         searches.
	 */
	public List<String> getOmitFromAHSearchList();

	// End FDP987

	// FDE034 TM 08/09/2015

	/**
	 * Update or add a response to a script result.
	 * 
	 * @param frInstance
	 * Application identifier supplied with original request.
	 * 
	 * @param returnId
	 * returnId of the result to update.
	 * 
	 * @param resOrderNo
	 * resOrderNo of the question to add or update a response to. If adding, the resorderno must be determined
	 * elsewhere.
	 * 
	 * @param usr
	 * UpdateScriptResult object containing information to be used to update the script result
	 * 
	 * @throws FRInstanceException
	 * 
	 * @throws ScriptResultNotFoundException
	 * Script result could not be found in the ReturnedScripts table.
	 * 
	 * @throws ResponseUpdateNotSupportedException
	 * The script question does not support response updates.
	 * 
	 * @throws ScriptItemNotFoundException
	 * The Item relating to the script result being upated could not be found in the
	 * Item table of the database.
	 */
	public void updateResultResponse(String frInstance, int returnId, int resOrderNo, UpdateScriptResult usr)
			throws FRInstanceException, ScriptResultNotFoundException, ResponseUpdateNotSupportedException,
			ScriptItemNotFoundException;

	// End FDE034
}
