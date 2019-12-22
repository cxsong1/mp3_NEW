package cpen221.mp3.wikimediator;

import java.util.*;
import java.util.stream.Collectors;

import cpen221.mp3.cache.NoSuchObjectException;
import com.google.gson.internal.LinkedTreeMap;
import cpen221.mp3.cache.Cache;
import fastily.jwiki.core.*;
import javafx.util.Pair;

/**
 * Represents a WikiMediator that uses an API to interact with Wikipedia
 *
 * Abstraction Function:
 *     'this' is a Wikimediator with a Wiki, called wiki, being the main entry point to
 *     the jWiki API. It contains three HashMaps:
 *        a timeMap that maps a query to the time it was last accessed
 *          (either by using simpleSearch/getPage or through the cache)
 *        a freqMap that maps the query to the number of times it has
 *          been accessed (either by using simpleSearch/getPage or through the cache)
*         a requestMap that maps the String of the type of request
 *          to the number of times the request was made.
 *
 *     'this' also contains a cache with a fixed capacity and timeout value which will
 *     save the title and page text searched by method getPage. Stale items will be removed.
 *
 * Representation Invariant:
 *    Domain of wiki is from wikipedia.org
 *    timeMap, wiki, freqMap, cache, and requestMap are non-null
 *    Cache capacity and timeout are greater than 0
 *    queries, frequencies, and dates accessed are non-null
 *    query is a non-empty String
 *    For each i in timeMap.keySet(), timeMap.get(i) > 0
 *    For each i in freqMap.keySet(), freqMap.get(i) >= 1
 *    Each i in requestMap.keySet() should correspond to the String
 *        name of a method in the WikiMediator class
 *
 */

public class WikiMediator {
	private Wiki wiki;
	private Map<String, Long> timeMap;
	private Map<String, Integer> freqMap;
	private Map<String, Long> requestMap;
	private Cache cache;

	//constructor
	public WikiMediator(){
		this.timeMap = new HashMap<String, Long>();
		this.wiki = new Wiki("en.wikipedia.org");
		this.freqMap = new HashMap<>();
		this.requestMap = new HashMap<>();
		this.cache = new Cache(256, 12*3600);
	}

	/**
	 * Searches for page titles in Wikipedia matching the query String
	 *
	 * @param query String to search for
	 * @param limit max number of elements (ie. search results) returned
	 * @return a List of Strings of the page titles that match the query string
	 */
	public List<String> simpleSearch(String query, int limit) {
		if(this.freqMap.containsKey(query)){
			this.freqMap.put(query, this.freqMap.get(query)+1);
		}else {
			this.freqMap.put(query, 1);
		}
		this.timeMap.put(query, System.currentTimeMillis());
		this.requestMap.put("simpleSearch", System.currentTimeMillis());
		if(limit == 0) {
			return new ArrayList<String>();
		}
		return wiki.allPages(query, false, false, limit, null);
	}

	/**
	 * Returns the text on a given Wikipedia page by either:
	 *    accessing it from the cache if it has recently been searched for OR
	 *    searching it up on Wikipedia then storing the title and text to the cache
	 *
	 * @param pageTitle string representing the Wikipedia page you want to find
	 * @return String with the text on the "pageTitle" Wikipedia page
	 */
	public String getPage(String pageTitle) throws NoSuchObjectException {
		String text = "";

		if(this.freqMap.containsKey(pageTitle)){
			this.freqMap.put(pageTitle, this.freqMap.get(pageTitle)+1);
		}else {
			this.freqMap.put(pageTitle, 1);
		}

		try {
			Object obj = cache.get(pageTitle);
			JSONObj jo = (JSONObj) obj;
			text = (jo.item.get("Page Text")).toString();
			return text;
		}
		catch (NoSuchObjectException e) {
			this.timeMap.put(pageTitle, System.currentTimeMillis());
			this.requestMap.put("getPage", System.currentTimeMillis());
			text = wiki.getPageText(pageTitle);

			cache.put(new JSONObj(pageTitle, text));
		}
		return text;
	}

	/**
	 * Finds all the possible pages that can be found by following up to a max number
	 *    of links from a specified starting point
	 *
	 * @param pageTitle String name of the starting Wikipedia page
	 * @param hops max number of links that can be followed
	 * @return a List of Strings that can be reached by following a maximum of hops links from pageTitle
	 */

	public List<String> getConnectedPages(String pageTitle, int hops){
		Set<String> visited = new HashSet<>();
		Queue<Pair<String, Integer>> queue = new LinkedList<>();
		queue.add(new Pair<>(pageTitle, 0));

		while (queue.size() > 0){
			Pair parent = queue.poll();
			if ( !visited.contains((String) parent.getKey())){
				String title = (String) parent.getKey();
				visited.add(title);
				if ((int)parent.getValue() < hops) {
					List<String> neighbours = wiki.getLinksOnPage(title);
					int level = (int) parent.getValue() + 1;
					for (String s : neighbours) {
						queue.add(new Pair<>(s, level));
					}
				}
			}
		}

		this.requestMap.put("getConnectedPages", System.currentTimeMillis());

		return new ArrayList<String>(visited);
	}

		/**
		 * Returns the most common page titles searched for (using simpleSearch or getPage) in non-increasing order.
		 * If multiple Strings were searched the same number of times,
		 * they can be organized in any arbitrary order
		 * TODO: fix this (ie. order by time searched)
		 *
		 * @param limit max number of titles returned
		 * @return a List of Strings containing the most commonly searched titles,
		 *         up to a max number, in non-increasing order
		 */
	public List<String> zeitgeist(int limit){
		Map<String, Integer> sortedFreqMap;
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
	 * Finds and sorts the most frequent search requests (using simple search or getPage) made in the last 30secs
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
	 * The current call to peakLoad30s will not be counted as a request.
	 *
	 * @return the max number of search requests seen in any 30-second window
	 */

	public int peakLoad30s(){
		Map<String, Long> sortedRequestMap = new TreeMap<>();
		int requests = 0;

		sortedRequestMap = this.requestMap.entrySet()
						.stream()
						.sorted((Map.Entry.<String, Long>comparingByValue()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedTreeMap::new));

		for (Map.Entry e: sortedRequestMap.entrySet()){
			Long time = (Long) e.getValue();
			Map<String, Long> sortedEMap = sortedRequestMap.entrySet().stream()
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
	 * @param stopPage String name of the Wikipedia page at which to end
	 * @return a List of Strings containing the links to follow to get from
	 *         startPage to endPage (including both the start and end pages)
	 *            if a path is not found within five minutes, an empty List
	 *            will be returned
	 */
	public List<String> getPath(String startPage, String stopPage){
		List<String> path = new ArrayList<>();
		Set<String> visited = new HashSet<>();

		List<Pair<String, String>> tracing = new ArrayList<>();
		List<String> tracingKeys = new ArrayList<>();
		tracing.add(new Pair<>(startPage, "0"));
		tracingKeys.add(startPage);

		Queue<Pair<String, String>> queue = new LinkedList<>();
		queue.add(new Pair<>(startPage, "0"));
		boolean found = false;

		if(startPage == stopPage){
			path.add(startPage);
			return path;
		}

		long startTime = System.currentTimeMillis();

		while (queue.size() > 0 && !found){ //starting size is 1
			if((System.currentTimeMillis() - startTime) > 300000){
				return new ArrayList<>();
			}
			String parent = queue.poll().getKey();
			if ( !visited.contains(parent)){
				visited.add(parent);
				List<String> neighbours = wiki.getLinksOnPage(parent);
				for (String s : neighbours) {
					if (s.equals(stopPage)){
						found = true;
						break;
					}
					queue.add(new Pair<>(s, parent));
					tracing.add(new Pair<>(s, parent));
					tracingKeys.add(s);
				}
			}
		}
		String currParent = tracing.get(tracing.size()-1).getValue();
		if (!currParent.equals("0")) {
			path.add(currParent);
		}

		while(!currParent.equals("0")){
			int i = tracingKeys.indexOf(currParent);
			currParent = tracing.get(i).getValue();
			if(currParent.equals("0"))
				break;
			path.add(currParent);
		}

		Collections.reverse(path);
		path.add(stopPage);
		return path;
	}

	/**
	 * Finds a list of pages that meet the structured query from the user
	 *
	 * @param query String representing the structured query
	 * @return a List of Strings containing the page titles meeting the
	 *         requirements from query
	 */
	public List<String> executeQuery(String query){
		return new ArrayList<String>();
	}

}
