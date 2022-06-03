package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;

import com.amtsybex.fieldreach.backend.model.ScriptRefItem;

public class LevelResultSet extends ResultSet implements Serializable {

	private String deltaResponse;
	
	//38437 - KN - Edit Level Response
	private String rightLabel;
	private String leftLabel;
	private String points;
	private String startValue;
	private String increment;
	private int maxValSlidder;
	
	private static final long serialVersionUID = -1765265511567934954L;

	public LevelResultSet(Integer id, String script, String response, String freeText, Integer resOrderNo) {
		super(id, script, response, freeText, resOrderNo);
		//this.deltaResponse = response;
		this.rightLabel = "Full";
		this.leftLabel = "Empty";
		this.points = "7";
		this.startValue = "0";
		this.increment = "1";
	}

	public LevelResultSet(int id, Integer scriptId, String script, int resOrderNo, Integer sequenceNumber,
			String itemType, String inputType) {
		super(id, scriptId, script, resOrderNo, sequenceNumber, itemType, inputType);
		// TODO Auto-generated constructor stub
		this.rightLabel = "Full";
		this.leftLabel = "Empty";
		this.points = "7";
		this.startValue = "0";
		this.increment = "1";
	}

	@Override
	public void setEditPanelResponse() {
		if (this.isMandatorymodifierCheckBox()) {
			this.deltaResponse = "";
		} else {
			// added for Level Question to be used in slidder
			
			if(this.getResponse() != null && !this.getResponse().equalsIgnoreCase("N/A") && !this.getResponse().equalsIgnoreCase("O/R")) {
				this.deltaResponse = this.getResponse();
				
			}else {
			//if it is unanswered or previously NA response set to 0
				this.deltaResponse = "0";
			}
			
			//this.deltaResponse = this.getResponse();
			if (this.getScriptRefItemList()!=null && !this.getScriptRefItemList().isEmpty()) {
				for (ScriptRefItem addref : this.getScriptRefItemList()) {
					if (addref.getRefItem().getRefDesc().equalsIgnoreCase("LEFT LABEL")) {
						this.leftLabel = addref.getRefItem().getOtherRef();
					} else if (addref.getRefItem().getRefDesc().equalsIgnoreCase("RIGHT LABEL")) {
						this.rightLabel = addref.getRefItem().getOtherRef();
					} else if (addref.getRefItem().getRefDesc().equalsIgnoreCase("POINTS")) {
						this.points = addref.getRefItem().getOtherRef();
					} else if (addref.getRefItem().getRefDesc().equalsIgnoreCase("START VALUE")) {
						this.startValue = addref.getRefItem().getOtherRef();
					} else if (addref.getRefItem().getRefDesc().equalsIgnoreCase("INCREMENT")) {
						this.increment = addref.getRefItem().getOtherRef();
					}
				}

			}
			maxValSlidder = Integer.parseInt(points) * Integer.parseInt(increment);
		
		}
		
	}

	@Override
	public void setCurrentResponse() {
		this.setResponse(this.deltaResponse);
		
	}
	
	@Override
	public String generateSaveResponse() {
		return this.deltaResponse;
	}
	
	@Override
	public boolean isResponseValid() {
		return true;
	}

	public String getDeltaResponse() {
		return deltaResponse;
	}

	public void setDeltaResponse(String deltaResponse) {
		this.deltaResponse = deltaResponse;
	}

	public String getRightLabel() {
		return rightLabel;
	}

	public void setRightLabel(String rightLabel) {
		this.rightLabel = rightLabel;
	}

	public String getLeftLabel() {
		return leftLabel;
	}

	public void setLeftLabel(String leftLabel) {
		this.leftLabel = leftLabel;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getStartValue() {
		return startValue;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	public String getIncrement() {
		return increment;
	}

	public void setIncrement(String increment) {
		this.increment = increment;
	}

	public int getMaxValSlidder() {
		return maxValSlidder;
	}

	public void setMaxValSlidder(int maxValSlidder) {
		this.maxValSlidder = maxValSlidder;
	}

}

