package com.amtsybex.fieldreach.fdm.analytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.RepeatGroups;
import com.amtsybex.fieldreach.backend.model.ScriptItemRules;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * FDE060 - script analytics/metrics
 * @author CroninM
 *
 */
public class AnalysisResultSet {


	private Long resultCountOverVersions;
	private Date resultFromDateOverVersions;
	private Date resultToDateOverVersions;
	
	private Long overAllResultCount;
	private ScriptVersions workingVersion;
	
	private Long averageDuration;
	//the current list of questions for script metrics, changes on tab change
	private Map<Integer, Item> latestScriptItems;
	List<RepeatGroups> repeatGroups;
	List<ScriptItemRules> scriptItemRules;

	//only should be set for the ALL tab
	private Map<Integer, Long> resultCounts;
	private Map<Integer, Long> dateCounts;

	private  Map<String, Map<String,Object>> wgCatCounts;
	private List<ScriptVersions> versions;
	private Map<Integer, Map<Integer, Item>> allScripts;


	private Map<Integer, ScriptVersions> versionsMap;

	private List<AnalysisResultSetResults> questionResults;

	Map<Integer, Map<String, Long>> versionAnalytics;
	Boolean showNAColumn;
	Boolean showORColumn;


	public AnalysisResultSet() {
		this.averageDuration = null;
	}
	
	public AnalysisResultSet(Long overAllResultCount, Map<Integer, Long> resultCounts, List<ScriptVersions> versions,
			ScriptVersions workingVersion, Map<Integer, Item> latestScriptItems) {
		super();
		this.overAllResultCount = overAllResultCount == null ? Long.valueOf(0) : overAllResultCount;
		this.resultCounts = resultCounts;
		this.workingVersion = workingVersion;
		this.latestScriptItems = latestScriptItems;

		this.setVersions(versions);
	}

	public String getTitle() {
		return resultCounts != null ? "Data Set Analytics (" + overAllResultCount + ")" : "Version " + workingVersion.getVersionNumber();
	}
	
	public String getQuestionRepeatGroupRuleGroupIcon(Item question) {
		
		if(question == null) {
			return null;
		}
		if(repeatGroups != null) {
			for(RepeatGroups rg : repeatGroups) {
				if(rg.getId().getFromSeqNo() == question.getId().getSequenceNumber()) {
					
					return "rgstart";

				}
				if(rg.getToSeqNo() == question.getId().getSequenceNumber()) {
					
					return "rgend";

				}
			}
		}

				
		if(scriptItemRules != null) {
			for(ScriptItemRules ir : scriptItemRules) {
				
				if(ir.getId().getFromSeqNo() == question.getId().getSequenceNumber()) {
					
					if(ir.getToSeqNo() == null || ir.getToSeqNo() == question.getId().getSequenceNumber()) {
						
						return "rgpsingle";

					}
					
					return "rgpstart";
				}
				
				if(ir.getToSeqNo() == question.getId().getSequenceNumber()) {
					
					return "rgpend";

				}
			}
		}


		return null;
	}
	

	
	public Boolean isNAPresent(){

		if(this.latestScriptItems != null) {
			if(showNAColumn == null) {
				for(Item item : this.latestScriptItems.values()) {
					if(item.getInputType() != null && item.getItemType() != null && !item.getItemType().equals(Common.QUESTION_TYPE_HEADING) && (item.getInputType().equals("O") || item.getInputType().equals("MN"))) {
						showNAColumn = Boolean.valueOf(true);
						return showNAColumn;
					}
					
				}
				showNAColumn = Boolean.valueOf(false);
			}
		}

		return showNAColumn;

	}
	
	public Boolean isORPresent(){

		if(this.latestScriptItems != null) {
			if(showORColumn == null) {
				for(Item item : this.latestScriptItems.values()) {
					if(item.getInputType() != null && item.getItemType() != null && !item.getItemType().equals(Common.QUESTION_TYPE_HEADING) && item.getInputType().equals("MO")) {
						showORColumn = Boolean.valueOf(true);
						return showORColumn;
					}
				}
				showORColumn = Boolean.valueOf(false);
			}
		}

		return showORColumn;

	}

	public Long getNaForSeqNo(Integer sequenceNumber) {
		if(versionAnalytics != null) {
			Map<String, Long> items = versionAnalytics.get(sequenceNumber);
			if(items != null && items.containsKey("N/A")) {
				return items.get("N/A");
			}
		}
		return null;
	}

	public Long getOrForSeqNo(Integer sequenceNumber) {
		if(versionAnalytics != null) {
			Map<String, Long> items = versionAnalytics.get(sequenceNumber);
			if(items != null && items.containsKey("O/R")) {
				return items.get("O/R");
			}
		}

		return null;
	}
	
	public Long getOkForSeqNo(Integer sequenceNumber) {
		if(versionAnalytics != null) {
			Map<String, Long> items = versionAnalytics.get(sequenceNumber);
			if(items != null && items.containsKey("OK")) {
				return items.get("OK");
			}
		}

		return null;
	}
	

	public String getOkPercentageForSeqNo(Integer sequenceNumber) {
		if(versionAnalytics != null) {
			Map<String, Long> items = versionAnalytics.get(sequenceNumber);
			if(items != null && items.containsKey("OK")) {
				Long okCount = items.get("OK");
				if(okCount == null) {
					return null;
				}
				return String.format("%.01f", ((Float.valueOf(okCount) / Float.valueOf(getRespondedForSeqNo(sequenceNumber)) * 100))) + "%";
			}
		}
		return null;
	}
	
	
	public String getRespondedForSeqNo(Integer sequenceNumber) {
		
		if(versionAnalytics != null) {
			
			Long ok = this.getOkForSeqNo(sequenceNumber);
			Long na = this.getNaForSeqNo(sequenceNumber);
			Long or = this.getOrForSeqNo(sequenceNumber);
			
			ok = ok == null ? Long.valueOf(0) : ok;
			na = na == null ? Long.valueOf(0) : na;
			or = or == null ? Long.valueOf(0) : or;
			
			return String.valueOf(ok + na + or);
		}
		return null;
	}
	
	public String getRespondedPercentageForSeqNo(Integer sequenceNumber) {
		
		if(versionAnalytics != null) {
			
			Long ok = this.getOkForSeqNo(sequenceNumber);
			Long na = this.getNaForSeqNo(sequenceNumber);
			Long or = this.getOrForSeqNo(sequenceNumber);
			
			ok = ok == null ? Long.valueOf(0) : ok;
			na = na == null ? Long.valueOf(0) : na;
			or = or == null ? Long.valueOf(0) : or;
			
			Float totalResponses = Float.valueOf(ok + na + or);
			
			if((Float.valueOf(totalResponses) / Float.valueOf(this.overAllResultCount)) * 100 > 100) {
				return "100%";
			}
			
			return String.format("%.01f", ((Float.valueOf(totalResponses) / Float.valueOf(this.overAllResultCount)) * 100)) + "%";
		}
		return null;
	}

	public String getColourForSeqNo(Integer sequenceNumber) {
		
		Long ok = this.getOkForSeqNo(sequenceNumber);
		Long or = this.getOrForSeqNo(sequenceNumber);
		Long na = this.getNaForSeqNo(sequenceNumber);
		
		ok = ok == null ? Long.valueOf(0) : ok;
		na = na == null ? Long.valueOf(0) : na;
		or = or == null ? Long.valueOf(0) : or;
		
		if(( ok + na + or) == 0) {
			return "red";
		}
		if(this.overAllResultCount * 0.05 > ( ok + na + or)) {
			return "orange";
		}
		if(this.overAllResultCount * 0.05 > ok) {
			return "yellow";
		}

		return "none";
	}
	
	
	
	public String getColourForSeqNoNA(Integer sequenceNumber) {
		
		Long ok = this.getOkForSeqNo(sequenceNumber);
		Long or = this.getOrForSeqNo(sequenceNumber);
		Long na = this.getNaForSeqNo(sequenceNumber);
		
		ok = ok == null ? Long.valueOf(0) : ok;
		na = na == null ? Long.valueOf(0) : na;
		or = or == null ? Long.valueOf(0) : or;
		
		if(( ok + na + or) == 0) {
			return "red";
		}
		if(this.overAllResultCount * 0.05 > ( ok + na + or)) {
			return "orange";
		}
		
		if(ok == 0 || ok * 0.8 < na) {
			return "orange";
		}
		
		if(this.overAllResultCount * 0.05 > ok) {
			return "yellow";
		}

		return "none";
	}
	
	
	public String getColourForSeqNoOR(Integer sequenceNumber) {
		
		Long ok = this.getOkForSeqNo(sequenceNumber);
		Long or = this.getOrForSeqNo(sequenceNumber);
		Long na = this.getNaForSeqNo(sequenceNumber);
		
		ok = ok == null ? Long.valueOf(0) : ok;
		na = na == null ? Long.valueOf(0) : na;
		or = or == null ? Long.valueOf(0) : or;
		
		if(( ok + na + or) == 0) {
			return "red";
		}
		if(this.overAllResultCount * 0.05 > ( ok + na + or)) {
			return "orange";
		}
		
		if(ok == 0 || ok * 0.8 < or) {
			return "orange";
		}
		
		if(this.overAllResultCount * 0.05 > ok) {
			return "yellow";
		}

		return "none";
	}

	public String getVersionNumbersAsText() {

		if(versions != null) {
			return versions.stream()
					.map(n -> String.valueOf(n.getVersionNumber()))
					.collect(Collectors.joining(", "));
		}else {
			return String.valueOf(workingVersion.getVersionNumber());
		}

	}

	public Date getResultFromDateRange() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			return df.parse(String.valueOf((Integer)((TreeMap)dateCounts).firstKey()));
		} catch (Exception e) {
			return null;
		}
	}

	public Date getResultToDateRange() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			return df.parse(String.valueOf((Integer)((TreeMap)dateCounts).lastKey()));
		} catch (Exception e) {
			return null;
		}
	}

	public Map<Integer, List<Item>> getItemsForItemInVersions(AnalysisResultSetResults resultSetResult) {
		resultSetResult.setFoundInVersions(null);
		Map<Integer, List<Item>> items = new HashMap<Integer, List<Item>>();
		if(allScripts == null || allScripts.size() == 0) {
			List<Item> itemsList = new ArrayList<Item>();
			itemsList.add(resultSetResult.getQuestion());
			items.put(resultSetResult.getQuestion().getId().getSequenceNumber(), itemsList);
			resultSetResult.setFoundInVersions(this.getVersionNumbersAsText());
		}else {
			for(Map<Integer, Item> otherScriptItems : allScripts.values()) {

				//if seqNo, itemText and itemType equals
					//Then match
				//else
					//loop other version questin items (from the start)
						//if itemText and itemType equals
						//Then match and exit loop
				
				//if not then check other responses to see if we can match one up
				int found = 0;
				for(Item otherItem : otherScriptItems.values()) {
					if(otherItem.getItemText() != null && otherItem.getItemType() != null) {

						if(otherItem.getItemText().equalsIgnoreCase(resultSetResult.getQuestion().getItemText()) && otherItem.getItemType().equalsIgnoreCase(resultSetResult.getQuestion().getItemType())) {
	
							found++;
							
							if(items.containsKey(otherItem.getId().getSequenceNumber())) {
								items.get(otherItem.getId().getSequenceNumber()).add(otherItem);
							}else {
								List<Item> itemsList = new ArrayList<Item>();
								itemsList.add(otherItem);
								items.put(otherItem.getId().getSequenceNumber(), itemsList);
							}
	
							if(found == 1) {
								if(resultSetResult.getFoundInVersions() == null) {
									resultSetResult.setFoundInVersions("" + versionsMap.get(otherItem.getId().getScriptId()).getVersionNumber());
								}else {
									resultSetResult.setFoundInVersions(resultSetResult.getFoundInVersions() + " ," + versionsMap.get(otherItem.getId().getScriptId()).getVersionNumber());
								}
							}
	
							//break;
						}
					}
				}
				if(found > 1) {
					resultSetResult.setFoundMoreThanOnce(true);
					resultSetResult.setFoundInVersions(resultSetResult.getFoundInVersions() + "(" + found + ")");
				}else {
					resultSetResult.setFoundMoreThanOnce(false);
				}
			}
		}


		return items;
	}

	public boolean isQuestionInAllVersions(Item item) {

		if(allScripts == null || allScripts.size() == 0) {
			return true;
		}
		for(Map<Integer, Item> otherScriptItems : allScripts.values()) {

			Item matchingQ = otherScriptItems.get(item.getId().getSequenceNumber());

			//check coresponding seq no is the same first
			if(matchingQ == null || !matchingQ.getItemText().equalsIgnoreCase(item.getItemText()) || !matchingQ.getItemType().equalsIgnoreCase(item.getItemType())) {
				//if not then check other responses to see if we can match one up
				boolean found = false;
				for(Item otherItem : otherScriptItems.values()) {
					if(otherItem.getItemText().equalsIgnoreCase(item.getItemText()) && otherItem.getItemType().equalsIgnoreCase(item.getItemType())) {
						found = true;
						break;
					}
				}
				if(!found) {
					return false;
				}
			}
		}

		return true;
	}

	public Long getOverAllResultCount() {
		return overAllResultCount;
	}


	public void setOverAllResultCount(Long overAllResultCount) {
		this.overAllResultCount = overAllResultCount == null ? Long.valueOf(0) : overAllResultCount;
	}


	public Map<Integer, Long> getResultCounts() {
		return resultCounts;
	}


	public void setResultCounts(Map<Integer, Long> resultCounts) {
		this.resultCounts = resultCounts;
	}


	public List<ScriptVersions> getVersions() {
		return versions;
	}


	public void setVersions(List<ScriptVersions> versions) {
		this.versions = versions;
		if(versions != null) {
			this.versionsMap = new HashMap<Integer, ScriptVersions>();
			for(ScriptVersions version : versions) {
				versionsMap.put(version.getId(), version);
			}
		}
	}


	public ScriptVersions getWorkingVersion() {
		return workingVersion;
	}


	public void setWorkingVersion(ScriptVersions workingVersion) {
		this.workingVersion = workingVersion;
	}


	public Map<Integer, Item> getLatestScriptItems() {
		return latestScriptItems;
	}


	public void setLatestScriptItems(Map<Integer, Item> latestScriptItems) {
		this.latestScriptItems = latestScriptItems;
	}


	public Map<Integer, Map<Integer, Item>> getAllScripts() {
		return allScripts;
	}


	public void setAllScripts(Map<Integer, Map<Integer, Item>> allScripts) {
		this.allScripts = allScripts;
	}


	public Map<Integer, Long> getDateCounts() {
		return dateCounts;
	}


	public void setDateCounts(Map<Integer, Long> dateCounts) {
		this.dateCounts = dateCounts;
	}

	public Map<Integer, ScriptVersions> getVersionsMap() {
		return versionsMap;
	}


	public void setVersionsMap(Map<Integer, ScriptVersions> versionsMap) {
		this.versionsMap = versionsMap;
	}


	public List<AnalysisResultSetResults> getQuestionResults() {
		return questionResults;
	}


	public void setQuestionResults(List<AnalysisResultSetResults> questionResults) {
		this.questionResults = questionResults;
	}


	public Map<Integer, Map<String, Long>> getVersionAnalytics() {
		return versionAnalytics;
	}


	public void setVersionAnalytics(Map<Integer, Map<String, Long>> versionAnalytics) {
		this.versionAnalytics = versionAnalytics;
	}


	public Map<String, Map<String, Object>> getWgCatCounts() {
		return wgCatCounts;
	}


	public void setWgCatCounts(Map<String, Map<String, Object>> wgCatCounts) {
		this.wgCatCounts = wgCatCounts;
	}


	public List<RepeatGroups> getRepeatGroups() {
		return repeatGroups;
	}


	public void setRepeatGroups(List<RepeatGroups> repeatGroups) {
		this.repeatGroups = repeatGroups;
	}


	public List<ScriptItemRules> getScriptItemRules() {
		return scriptItemRules;
	}


	public void setScriptItemRules(List<ScriptItemRules> scriptItemRules) {
		this.scriptItemRules = scriptItemRules;
	}

	public Long getResultCountOverVersions() {
		return resultCountOverVersions;
	}

	public void setResultCountOverVersions(Long resultCountOverVersions) {
		this.resultCountOverVersions = resultCountOverVersions;
	}

	public Date getResultFromDateOverVersions() {
		return resultFromDateOverVersions;
	}

	public void setResultFromDateOverVersions(Date resultFromDateOverVersions) {
		this.resultFromDateOverVersions = resultFromDateOverVersions;
	}

	public Date getResultToDateOverVersions() {
		return resultToDateOverVersions;
	}

	public void setResultToDateOverVersions(Date resultToDateOverVersions) {
		this.resultToDateOverVersions = resultToDateOverVersions;
	}

	public Long getAverageDuration() {
		return averageDuration;
	}

	public void setAverageDuration(Long averageDuration) {
		this.averageDuration = averageDuration;
	}
	
}
