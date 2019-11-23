package cpen221.mp3.wikimediator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fastily.jwiki.core.*;
import fastily.jwiki.dwrap.*;

public class WikiMediator {
	//map that will be used in the zeitgeist, trending and peakLoad30s
	private Map<String, Long> timeMap;
	private Wiki wiki;
	private Map<String, Integer> freqMap;

	//constructor
	public WikiMediator(){
		this.timeMap = new HashMap<String, Long>();
		this.wiki = new Wiki("en.wikipedia.org");
	}

	/**
	 * Searches for page titles in Wikipedia matching the query String
	 *
	 * @param query String to search for
	 * @param limit max number of elements (ie. search results) returned
	 * @return a List of Strings containing the page titles that match the query string
	 */
	public List<String> simpleSearch(String query, int limit) {
		return wiki.allPages(query, false, false, limit, null);
	}

	/**
	 * Returns the text on a given Wikipedia page
	 *
	 * @param pageTitle string representing the Wikipedia page you want to find
	 * @return String with the text on the "pageTitle" Wikipedia page
	 */
	public String getPage(String pageTitle){
		this.timeMap.put(pageTitle, System.currentTimeMillis());
		return wiki.getPageText(pageTitle);
	}

	/**
	 * Finds all the possible pages that can be found by following UP TO a max number
	 *    of links from a specified starting point
	 *
	 * @param pageTitle String name of the starting Wikipedia page
	 * @param hops max number of links that can be followed
	 * @return a List of Strings that can be reached by following a maximum of hops links from pageTitle
	 */

	//MAKE SURE ITS UP TO AND NOT NECESSARILY JUST "HOPS" NUMBER OF LINKS!!!!
	public List<String> getConnectedPages(String pageTitle, int hops){
		List<String> connected = new ArrayList<>();
		List<String> currLinks = new ArrayList<>();
		List<String> currTitles = new ArrayList<>();
		List<String> links = new ArrayList<>();

		for(int i = 0; i<hops; i++) {
			currLinks = wiki.getLinksOnPage(pageTitle, null);
			connected.addAll(currLinks);
			currTitles = linkToTitle(currLinks);
			links = getConnectedHelper(currTitles, links ,0);

		}
	}

	private List<String> getConnectedHelper(List<String> pageTitles, List<String> links, int start){
		if(start == pageTitles.size()){
			return links;
		}else{
			links.addAll(wiki.getLinksOnPage(pageTitles.get(start), null));
			return getConnectedHelper(pageTitles, links, start+1);
		}
	}

	private List<String> linkToTitle (List<String> pageLinks){
		List<String> pageTitles = new ArrayList<>();
		StringBuilder title;

		for(int i = 0; i<pageLinks.size(); i++) {
			StringBuilder link = new StringBuilder(pageLinks.get(i));
			title = link.delete(0, 21);
			while(title.toString().contains("_")){
				title.replace(title.indexOf("_"), title.indexOf("_"), " ");
			}
			pageTitles.add(title.toString());
		}
		return pageTitles;
	}

	/**
	 * Returns the most common page titles searched for in non-increasing order
	 *
	 * @param limit max number of requests returned
	 * @return a List of Strings containing the most common searched titles,
	 *         up to a max number, in non-increasing order
	 */
	public List<String> zeitgeist(int limit){
		Map<String, Integer> sortedFreqMap = new HashMap<>();
		int count = 0;
		List<String> mostCommon = new ArrayList<>();

		//sorts freqMap to be in non-increasing order
		sortedFreqMap = this.freqMap.entrySet()
																.stream()
																.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
																.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, HashMap::new));

		for(String s: sortedFreqMap.keySet()){
			if(count<limit) {
				mostCommon.add(s);
				count++;
			}else{
				return mostCommon;
			}
		}
		return mostCommon;
	}

	/**
	 * Finds and sorts the most search frequent requests made in the last 30secs
	 *
	 * @param limit max number of elements returned in the List
	 * @return a List of Strings containing the most common searched titles in the
	 *         last 30secs, up to a max number, in non-increasing order
	 */
	public List<String> trending(int limit){
		return null;
	}

	/**
	 * Finds the max number of requests seen in any 30-second window
	 *
	 * @return the max number of search requests seen in any 30-second window
	 */
	public int peakLoad30s(){
		return -1;
	}

	/**
	 * Finds path of links between two Wikipedia pages
	 *
	 * @param startPage String name of Wikipedia page at which to start
	 * @param stopPage String name of the Wikipedia page we want to end on
	 * @return a List of Strings containing the links to follow to get from
	 *         startPage to endPage
	 */
	List<String> getPath(String startPage, String stopPage){
		return null;
	}

	//TODO: need to modify the spec for the specific grammar of the query
	/**
	 * Finds a list of pages that meet the structured query from the user
	 *
	 * @param query String representing the structured query.....
	 * @return a List of Strings containing the page titles meeting the
	 *         requirements from query
	 */
	List<String> executeQuery(String query){
		return null;
	}
}
