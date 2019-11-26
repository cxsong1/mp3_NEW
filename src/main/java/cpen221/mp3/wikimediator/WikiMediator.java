package cpen221.mp3.wikimediator;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.internal.LinkedTreeMap;
import cpen221.mp3.cache.Cache;
import fastily.jwiki.core.*;
import fastily.jwiki.dwrap.*;
import javafx.util.Pair;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Represents a WikiMediator that uses an API to interact with Wikipedia
 *
 * Abstraction Function:
 *    AF(r) = Wikimediator w such that
 *        timeMap maps the query to the time it was last accessed
 *            (either by using allPages or through the cache)
 *        freqMap maps the query to the number of times it has been accessed
 *            (either by using allPages or through the cache)
 *
 *
 * Representation Invariant:
 *    Domain of wiki is from wikipedia.org
 *    wiki is not null
 *    Cache capacity and timeout are greater than 0
 *    queries and their ids are non-null
 *
 */

public class WikiMediator {
	//map that will be used in the zeitgeist, trending and peakLoad30s
	private Map<String, Long> timeMap;
	private Wiki wiki;
	private Map<String, Integer> freqMap;
	private Cache cache= new Cache(256, 12*3600);
	private Map<String, Long> requestMap;

	//constructor
	public WikiMediator(){
		this.timeMap = new HashMap<String, Long>();
		this.wiki = new Wiki("en.wikipedia.org");
		this.freqMap = new HashMap<>();
		this.requestMap = new HashMap<>();
	}

	/**
	 * Searches for page titles in Wikipedia matching the query String
	 *
	 * @param query String to search for
	 * @param limit max number of elements (ie. search results) returned
	 * @return a List of Strings containing the page titles that match the query string
	 */
	public List<String> simpleSearch(String query, int limit) {
		if(this.freqMap.containsKey(query)){
			this.freqMap.put(query, this.freqMap.get(query)+1);
		}else {
			this.freqMap.put(query, 1);
		}
		this.timeMap.put(query, System.currentTimeMillis());
		this.requestMap.put("simpleSearch", System.currentTimeMillis());
		return wiki.allPages(query, false, false, limit, null);
	}

	/**
	 * Returns the text on a given Wikipedia page
	 *
	 * @param pageTitle string representing the Wikipedia page you want to find
	 * @return String with the text on the "pageTitle" Wikipedia page0o
	 */
	public String getPage(String pageTitle){
		this.timeMap.put(pageTitle, System.currentTimeMillis());
		this.requestMap.put("getPage", System.currentTimeMillis());
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
		List<String> visited = new ArrayList<>();
		Queue<Pair<String, Integer>> queue = new LinkedList<>();
		queue.add(new Pair<>(pageTitle, 0));

		while (queue.size() > 0){
			Pair parent = queue.poll();
			if ((int)parent.getValue() < hops && !visited.contains((String) parent.getKey())){
				String title = (String) parent.getKey();
				visited.add(title);
				List<String> neighbours = wiki.getLinksOnPage(title);
				int level = (int) parent.getValue() + 1;
				for (String s: neighbours){
					if (!connected.contains(s)){
						connected.add(s);
						queue.add(new Pair<>(s, level));
					}
				}
			}
		}

		this.requestMap.put("getConnectedPages", System.currentTimeMillis());

		return connected;
	}

		/**
		 * Returns the most common page titles searched for in non-increasing order. If multiple Strings were
		 * searched the same number of times, they can be organized in any arbitrary order
		 * TODO: fix this (ie. order by time searched)
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
		//Source : https://howtodoinjava.com/sort/java-sort-map-by-key/?fbclid=IwAR1KZHhiAwwJCmrJmxr6A8g3M_H9NGMRk12-_J88o4xlCr2DkD1EvzAhahk
		sortedFreqMap = this.freqMap.entrySet()
																.stream()
																.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
																.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));

		this.requestMap.put("zeitgeist", System.currentTimeMillis());

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
	 *         last 30secs (up to and including), up to a max number, in non-increasing order
	 */
	public List<String> trending(int limit){
		Long currTime = System.currentTimeMillis();
		List<String> trending = new ArrayList<>();
		Map<String, Integer> sortedFreqMap = new HashMap<>();

		sortedFreqMap = this.freqMap.entrySet()
						.stream()
						.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));

		for(String s: sortedFreqMap.keySet()){
			if(currTime - timeMap.get(s)<= 30000){
				if(trending.size()<limit) {
					trending.add(s);
				}else{
					break;
				}
			}
		}

		this.requestMap.put("Trending", System.currentTimeMillis());

		return trending;
	}


	/**
	 * Finds the max number of requests seen in any 30-second window
	 * When ths user makes this request, the current peakLoad 30s will not be counted as a request.
	 *
	 * @return the max number of search requests seen in any 30-second window
	 */

	public int peakLoad30s(){
		Map<String, Long> sortedRequstMap = new TreeMap<>();
		int requests = 0;

		sortedRequstMap = this.requestMap.entrySet()
						.stream()
						.sorted((Map.Entry.<String, Long>comparingByValue().reversed()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedTreeMap::new));

		for (Map.Entry e: sortedRequstMap.entrySet()){
			Long time = (Long) e.getValue();
			Map<String, Long> sortedEMap = sortedRequstMap.entrySet().stream()
											.filter(e1 -> e1.getValue() >= time)
											.filter(e1 -> e1.getValue() <= time + 30*1000)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedTreeMap::new));
			int size = sortedEMap.size();
			if (size > requests){
				requests = size;
			}
		}

		this.requestMap.put("peakLoad30s", System.currentTimeMillis());

		return requests;
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
