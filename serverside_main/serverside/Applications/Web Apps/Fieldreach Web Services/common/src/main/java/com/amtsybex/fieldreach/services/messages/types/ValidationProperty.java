
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

public class ValidationProperty implements Serializable {

	private static final long serialVersionUID = -7059955604038709624L;
	
	private String equivValue;
	
    private Integer weightScore;
    
    private String color;
    
    private String name;

    /** 
     * Get the 'equivValue' element value.
     * 
     * @return value
     */
    public String getEquivValue() {
        return equivValue;
    }

    /** 
     * Set the 'equivValue' element value.
     * 
     * @param equivValue
     */
    public void setEquivValue(String equivValue) {
        this.equivValue = equivValue;
    }

    /** 
     * Get the 'weightScore' element value.
     * 
     * @return value
     */
    public Integer getWeightScore() {
        return weightScore;
    }

    /** 
     * Set the 'weightScore' element value.
     * 
     * @param weightScore
     */
    public void setWeightScore(Integer weightScore) {
        this.weightScore = weightScore;
    }

    /** 
     * Get the 'color' element value.
     * 
     * @return value
     */
    public String getColor() {
        return color;
    }

    /** 
     * Set the 'color' element value.
     * 
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /** 
     * Get the 'name' element value.
     * 
     * @return value
     */
    public String getName() {
        return name;
    }

    /** 
     * Set the 'name' element value.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
}
