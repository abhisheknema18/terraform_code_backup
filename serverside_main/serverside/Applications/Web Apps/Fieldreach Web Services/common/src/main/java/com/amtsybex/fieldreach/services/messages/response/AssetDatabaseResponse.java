
package com.amtsybex.fieldreach.services.messages.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("AssetDatabaseResponse")
public class AssetDatabaseResponse extends DatabaseResponse {
    private static final long serialVersionUID = 1966337316726780744L;
    public static final String APPLICATION_VND_FIELDSMART_ASSET_DB_1_JSON = "application/vnd.fieldsmart.assetdatabase-1+json";
    public static final String APPLICATION_VND_FIELDSMART_ASSET_DB_1_XML = "application/vnd.fieldsmart.assetdatabase-1+xml";

}
