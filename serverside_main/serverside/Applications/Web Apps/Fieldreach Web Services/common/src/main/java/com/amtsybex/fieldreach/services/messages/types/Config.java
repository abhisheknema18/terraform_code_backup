
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Config")
public class Config implements Serializable {

	private static final long serialVersionUID = -7160234350505115742L;
	
	private EQConfig eqConfig;
    private WMConfig wmConfig;
    private ScriptConfig scriptConfig;

    /** 
     * Get the 'eqConfig' element value.
     * 
     * @return value
     */
    public EQConfig getEqConfig() {
        return eqConfig;
    }

    /** 
     * Set the 'eqConfig' element value.
     * 
     * @param eqConfig
     */
    public void setEqConfig(EQConfig eqConfig) {
        this.eqConfig = eqConfig;
    }

    /** 
     * Get the 'wmConfig' element value.
     * 
     * @return value
     */
    public WMConfig getWmConfig() {
        return wmConfig;
    }

    /** 
     * Set the 'wmConfig' element value.
     * 
     * @param wmConfig
     */
    public void setWmConfig(WMConfig wmConfig) {
        this.wmConfig = wmConfig;
    }

    /** 
     * Get the 'scriptConfig' element value.
     * 
     * @return value
     */
    public ScriptConfig getScriptConfig() {
        return scriptConfig;
    }

    /** 
     * Set the 'scriptConfig' element value.
     * 
     * @param scriptConfig
     */
    public void setScriptConfig(ScriptConfig scriptConfig) {
        this.scriptConfig = scriptConfig;
    }
}
