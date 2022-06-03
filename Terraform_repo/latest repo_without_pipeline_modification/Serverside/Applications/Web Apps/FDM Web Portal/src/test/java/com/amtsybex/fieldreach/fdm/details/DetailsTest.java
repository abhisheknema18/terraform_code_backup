package com.amtsybex.fieldreach.fdm.details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptResults;
import com.amtsybex.fieldreach.backend.model.ScriptResultsTxt;
import com.amtsybex.fieldreach.backend.model.pk.ItemId;
import com.amtsybex.fieldreach.backend.model.pk.ScriptResultId;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore("Refer https://dev.azure.com/amtsybex1/Mobile/_workitems/edit/102309")
public class DetailsTest {
	
	private static final String TITLE = "TITLE";
	private static final String FREE_TEXT = "FREE TEXT";
	private static final String RESPONSE = "RESPONSE";

	@Test
	public void testBuildResponses() throws FRInstanceException{
		Details details = new Details();
		
		Map<Integer, Item> scriptItems = new HashMap<Integer, Item>();
		
		Integer[] sortedOrder = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		
		// build a map of script items, these will be sorted when we build the responses
		for (int i = 10; i > 0; i--){
			Item item = new Item();
			ItemId id = new ItemId();
			id.setScriptId(i);
			id.setSequenceNumber(i);
			item.setId(id);
			item.setItemText(TITLE+i);
			item.setItemType((i == 1 ? "HEADING" : "")); // make what will be the first item (sorted later), a heading
			scriptItems.put(i, item);
		}

		List<ScriptResults> scriptResults = new ArrayList<ScriptResults>();
		
		// build up a number of responses, less than the number of items
		for (int i = 5; i > 0; i--){
			ScriptResults result = new ScriptResults();
			ScriptResultId id = new ScriptResultId();
			ScriptResultsTxt resultsText = new ScriptResultsTxt();
			resultsText.setFreeText(FREE_TEXT+i);
			result.setFreeText(resultsText);
			result.setResponse(RESPONSE+i);
			id.setSequenceNo(i);
			id.setResOrderNo(i);
			result.setId(id);
			scriptResults.add(result);
		}
		
		List<ResultSet> responses = details.buildResponses(scriptItems, scriptResults);
		
		int answeredCount = 0;
		int unansweredCount = 0;
		int headingCount = 0;
		int responseCount = 0;
		
		for (int i = 0; i < responses.size(); i++){
			ResultSet x = responses.get(i);
			responseCount++;
			
			if (x.isAnswered()){
				answeredCount++;
			}else{
				unansweredCount++;
			}
			
			if (x.isHeading())
				headingCount++;
			
			if (x.getResOrderNo()!=null)
				assertEquals(sortedOrder[i], x.getResOrderNo());
		}
		
		// we should get back 10 responses
		assertEquals(responseCount, 10);
		
		// we should get back 4 answered responses
		assertEquals(answeredCount, 4);
		
		// we should get back 6 unanswered responses (headings count as unanswered, as they have no response)
		assertEquals(unansweredCount, 6);
		
		// we should get back 1 heading
		assertEquals(headingCount, 1);
		
		// assert that the 4th response corresponds with the expected response, given the input items and script results
		assertEquals("RESPONSE4", responses.get(3).getResponse());
		
		// check that the first item in the list is a heading
		// this item was actually added last, but our method sorts items before pairing them with responses
		assertTrue(responses.get(0).isHeading());
	}

	@Test
	public void testGetSortedScriptItemsMap(){
		Details details = new Details();
		
		// specify the order in which the items will be added to the map
		int[] intialOrder = {5, 4, 3, 2, 1};
		
		// specify the order in which the items will be sorted after we call our sort method
		int[] sortedOrder = {1, 2, 3, 4, 5};
		
		Map<Integer, Item> scriptItemsMap = new LinkedHashMap<Integer, Item>();
		
		for (int i = 5; i > 0; i--){
			Item item = new Item();
			ItemId id = new ItemId();
			id.setScriptId(i);
			item.setId(id);
			scriptItemsMap.put(i, item);
		}
		
		// determine how the items are ordered after adding them to the map
		int[] actualOrder = determineOrder(scriptItemsMap);
		
		// check that the items are ordered how we expected them to be
		for (int i = 0; i < 5; i++){
			assertEquals(intialOrder[i], actualOrder[i]);
		}
		
		// sort the map
		scriptItemsMap = details.getSortedScriptItemsMap(scriptItemsMap);
		
		// determine how the items are ordered after sorting them
		actualOrder = determineOrder(scriptItemsMap);
		
		// check that the items are ordered how we expected them to be after sorting
		for (int i = 0; i < 5; i++){
			assertEquals(sortedOrder[i], actualOrder[i]);
		}
		
	}
	
	private int[] determineOrder(Map<Integer, Item> scriptItemsMap){
		int actualOrder[] = new int[5];
		Iterator<Integer> scriptItemsIterator = scriptItemsMap.keySet().iterator();
		int cur = 0;
		while (scriptItemsIterator.hasNext()){
			int scriptId = scriptItemsIterator.next();
			actualOrder[cur++] = scriptId;
		}
		
		return actualOrder;
	}

}
