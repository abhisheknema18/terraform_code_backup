package com.amtsybex.fieldreach.fdm.analytics;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.amtsybex.fieldreach.backend.model.Item;

/**
 * FDE060 - script analytics/metrics
 * @author CroninM
 *
 */
public class AnalysisResultSetResults {

	private String heading;
	private Item question;
	private String chartType;
	//RESULTS
	private Map<String, Long> responseCounts;
	private Map<String, Double> numericResponsesByDate;
	private Map<String, Long> responseCountsByDate;
	private Map<String, Long> naCountsByDate;
	private Map<String, Long> orCountsByDate;
	
	private Long responseCount;
	private String foundInVersions;
	private boolean foundMoreThanOnce;
	private boolean showGraph;
	private boolean showNumericGraph;
	private double numericTotal;
	private double numericAverage;
	private double numericRangeLower;
	private double numericRangeUpper;
	
	private boolean canShowBar;
	private boolean showMetricSinglePickBar;
	

	public AnalysisResultSetResults() {
		super();
		this.chartType = "column";
	}
	
	public Date getResponseFromDateRange() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			return df.parse((String)((TreeMap)responseCountsByDate).firstKey());
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date getResponseToDateRange() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			return df.parse((String)((TreeMap)responseCountsByDate).lastKey());
		} catch (Exception e) {
			return null;
		}
	}
	
	public Long getAnsweredCount() {
		if(responseCount > 0 && responseCounts != null) {
			Long na = responseCounts.get("N/A");
			Long or = responseCounts.get("O/R");
			
			return responseCount - (na == null ? 0 : na) - (or == null ? 0 : or);
		}
		return Long.valueOf(0);
	}
	
	public Long getUnansweredCount() {
		if(responseCount > 0 && responseCounts != null) {
			Long na = responseCounts.get("N/A");
			Long or = responseCounts.get("O/R");
			
			return (na == null ? 0 : na) + (or == null ? 0 : or);
		}
		return Long.valueOf(0);
	}
	
	public void toggleChartType() {
		if(chartType.equals("column")) {
			chartType = "pie";
		}else {
			chartType = "column";
		}
	}
	
	public Map<String, Long> getResponseCounts() {
		return responseCounts;
	}
	public void setResponseCounts(Map<String, Long> responseCounts) {
		this.responseCounts = responseCounts;
	}
	public Map<String, Double> getNumericResponsesByDate() {
		return numericResponsesByDate;
	}
	public void setNumericResponsesByDate(Map<String, Double> numericResponsesByDate) {
		this.numericResponsesByDate = numericResponsesByDate;
	}
	public Map<String, Long> getResponseCountsByDate() {
		return responseCountsByDate;
	}
	public void setResponseCountsByDate(Map<String, Long> responseCountsByDate) {
		this.responseCountsByDate = responseCountsByDate;
	}
	public Map<String, Long> getNaCountsByDate() {
		return naCountsByDate;
	}
	public void setNaCountsByDate(Map<String, Long> naCountsByDate) {
		this.naCountsByDate = naCountsByDate;
	}
	public Map<String, Long> getOrCountsByDate() {
		return orCountsByDate;
	}
	public void setOrCountsByDate(Map<String, Long> orCountsByDate) {
		this.orCountsByDate = orCountsByDate;
	}
	public Long getResponseCount() {
		return responseCount;
	}
	public void setResponseCount(Long responseCount) {
		this.responseCount = responseCount;
	}
	public String getFoundInVersions() {
		return foundInVersions;
	}
	public void setFoundInVersions(String foundInVersions) {
		this.foundInVersions = foundInVersions;
	}
	public boolean isShowGraph() {
		return showGraph;
	}
	public void setShowGraph(boolean showGraph) {
		this.showGraph = showGraph;
	}
	public boolean isShowNumericGraph() {
		return showNumericGraph;
	}
	public void setShowNumericGraph(boolean showNumericGraph) {
		this.showNumericGraph = showNumericGraph;
	}
	public double getNumericTotal() {
		return numericTotal;
	}
	public void setNumericTotal(double numericTotal) {
		this.numericTotal = numericTotal;
	}
	public double getNumericAverage() {
		return numericAverage;
	}
	public void setNumericAverage(double numericAverage) {
		this.numericAverage = numericAverage;
	}
	public double getNumericRangeLower() {
		return numericRangeLower;
	}
	public void setNumericRangeLower(double numericRangeLower) {
		this.numericRangeLower = numericRangeLower;
	}
	public double getNumericRangeUpper() {
		return numericRangeUpper;
	}
	public void setNumericRangeUpper(double numericRangeUpper) {
		this.numericRangeUpper = numericRangeUpper;
	}

	public Item getQuestion() {
		return question;
	}

	public void setQuestion(Item question) {
		this.question = question;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public boolean isFoundMoreThanOnce() {
		return foundMoreThanOnce;
	}

	public void setFoundMoreThanOnce(boolean foundMoreThanOnce) {
		this.foundMoreThanOnce = foundMoreThanOnce;
	}

	public boolean isShowMetricSinglePickBar() {
		return showMetricSinglePickBar;
	}

	public void setShowMetricSinglePickBar(boolean showMetricSinglePickBar) {
		this.showMetricSinglePickBar = showMetricSinglePickBar;
	}

	public boolean isCanShowBar() {
		return canShowBar;
	}

	public void setCanShowBar(boolean canShowBar) {
		this.canShowBar = canShowBar;
	}
	
	
}

