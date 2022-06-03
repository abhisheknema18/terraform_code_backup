package com.amtsybex.fieldreach.fdm.search;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Ignore("Refer https://dev.azure.com/amtsybex1/Mobile/_workitems/edit/102309")
public class SearchResultsTest {

	@Test
	public void testNavigationOneResult(){
		
		SearchResults searchResults = new SearchResults();
		
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		for (int i = 0; i < 1; i++){
			SearchResult returned = new SearchResult();
			returned.setId(i);
			results.add(returned);
		}
		
		searchResults.setResults(results);
		searchResults.setSelectedResult(results.get(0));
		searchResults.updateNavigation();
		
		// we shouldn't be able to navigate to a previous result if we are at the first result
		assertFalse(searchResults.isPrevious());
		
		// we should be able to navigate to the next result if we are at the first result
		assertFalse(searchResults.isNext());
		
		// assert that we are at the first result
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 0);
		
		searchResults.nextResult();
		
		// assert that we are still at the first result after navigating
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 0);
		
		// we shouldn't be able to navigate to a previous result if we are at the first and only result
		assertFalse(searchResults.isPrevious());
		
		// we shouldn't be able to navigate to the next result if we are at the first and only result
		assertFalse(searchResults.isNext());
		
		searchResults.previousResult();
		
		// assert that we are at the first result again
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 0);
		
		// try to navigate before the first result
		searchResults.previousResult();
		
		// assert that we are still at the first result
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 0);
	}

	@Test
	public void testNavigationFiveResults(){
		
		SearchResults searchResults = new SearchResults();
		
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		for (int i = 0; i < 5; i++){
			SearchResult returned = new SearchResult();
			returned.setId(i);
			results.add(returned);
		}
		
		searchResults.setResults(results);
		searchResults.setSelectedResult(results.get(0));
		searchResults.updateNavigation();
		
		// we shouldn't be able to navigate to a previous result if we are at the first result
		assertFalse(searchResults.isPrevious());
		
		// we should be able to navigate to the next result if we are at the first result
		assertTrue(searchResults.isNext());
		
		// assert that we are at the first result
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 0);
		
		searchResults.nextResult();
		
		// assert that we are at the second result after navigating
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 1);
		
		// we should be able to navigate to a previous result if we are at the second result
		assertTrue(searchResults.isPrevious());
		
		// we should be able to navigate to the next result if we are at the second result
		assertTrue(searchResults.isNext());
		
		searchResults.previousResult();
		
		// assert that we are at the first result again
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 0);
		
		// try to navigate before the first result
		searchResults.previousResult();
		
		// assert that we are still at the first result
		assertEquals(searchResults.getResults().indexOf(searchResults.getSelectedResult()), 0);
	}
}
