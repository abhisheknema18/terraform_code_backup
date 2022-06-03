package com.amtsybex.fieldreach.xmlloader.core.scriptmonitor;

import com.amtsybex.fieldreach.backend.model.ReturnedScripts;

public interface ScriptResultsMonitor {

	public void performScriptMonitor(String instance, ReturnedScripts workingScriptResult);
}
