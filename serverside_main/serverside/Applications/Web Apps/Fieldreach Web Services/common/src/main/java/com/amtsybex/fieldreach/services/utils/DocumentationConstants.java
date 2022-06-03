package com.amtsybex.fieldreach.services.utils;

public class DocumentationConstants {
    
    // Swagger 
    public static final String DB_CLASS_DESCRIPTION = "Asset database class value. Used to determine the sub directory of the asset database within a pre-configured root directory. It is also used to determine the name of the asset database";
    public static final String WB_CLASS_DESCRIPTION = "Workbank database class value. Used to determine the sub directory of the workbank database within a pre-configured root directory. It is also used to determine the name of the workbank database";
    public static final String DB_AREA_DESCRIPTION = "The business area that the asset database is associated with. This will also be used to determine the location the web service will look for an asset database";
    public static final String DOWNLOAD_FILE_NAME_DESCRIPTION = "The name of the file to be downloaded";
    public static final String DOWNLOAD_IDENTIFIER_DESCRIPTION = "The unique identifier returned by the initialise download request";
    public static final String DOWNLOAD_PART_DESCRIPTION = "The part no of the file to be downloaded";
    public static final String SCRIPT_ID_DESCRIPTION = "The ScriptId of the script file to be retrieved";
    public static final String WORKGROUP_CODE_DESCRIPTION = "The workgroup code";
    public static final String EQUIP_NO_DESCRIPTION = "Search for results associated with the equipNo specified";
    public static final String FROM_DATE_DESCRIPTION = "Search for results whose completed date is >= this value. Must be in yyyymmdd format";
    public static final String TO_DATE_DESCRIPTION = "Search for results whose completed date is <= this value.Must be in yyyymmdd format";
    public static final String SCRIPT_CODE_DESCRIPTION = "Search for results with a matching script code. Multiple values can be supplied as a comma delimited list";
    public static final String RESULT_STATUS_DESCRIPTION = "Find results that have a particular result status. Multiple values can be supplied as a comma delimited list";
    public static final String USER_CODES_DESCRIPTION = "Look for script results that have been completed by a particular user. Multiple values can be supplied as a comma delimited list.";
    public static final String WORKGROUP_CODES_DESCRIPTION = "Look for script results that are associated with a particular workgroup. Multiple values can be supplied as a comma delimited list.";
    public static final String ALT_EQUIP_REF_DESCRIPTION = "Search for script results with a matching alt equip ref";
    public static final String VIEW_ID_DESCRIPTION = "Perform a search for script results using the personal view with the id supplied";
    public static final String WORK_ORDER_NO_DESCRIPTION = "Work Order Number";
    public static final String RES_ASSOC_CODE_RETURN_ID_DESCRIPTION = "The ReturnId of the current result. This will be used by the web service to determine the ResAssocCode value of the result and then use it to search for all associated results that are linked to the current result";
    public static final String EXTRACT_BINARY_DESCRIPTION = "Set to true to download any binary response data. If it is omitted or set to false binary response data will not be downloaded";
    public static final String FULL_SCRIPT_DESCRIPTION = "Boolean value indicating if the full result should be downloaded or only questions with a response recorded. Default value is false";
    public static final String EXTRACT_EDIT_PERMISSION_DESCRIPTION = "Boolean value indicating if the web service response will include the element indicating if the result is editable. Default value is false";
    public static final String RES_ORDER_NO_DESCRIPTION = "Result Order Number";
    public static final String USER_CODE_DESCRIPTION = "User code of the user";
    public static final String DELTA_DATE_DESCRIPTION = "Search for results whose ExtractDate in the ResultsHistoryExtract table is >= this value. Must be in YYYYMMDD format";
    public static final String DELTA_TIME_DESCRIPTION = "Search for results whose ExtractTime in the ResultsHistoryExtract table is > this value. Must be in HHMMSS format";
    public static final String WORK_ALLOC_DESCRIPTION = "Parameter to indicate if the web service should return work issued to a user, a workgroup, or multiple workgroups";
    public static final String DISTRICT_CODE_DESCRIPTION = "District code of the work order. If this is not supplied a default value of NA will be used";
    
    // Negotiation
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
}
