package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;
import org.json.JSONObject;

/**
 * Abstraction Function:
 *    item is a JSONObject that implements the interface Cacheable.
 *    It takes a Wikipedia page, and stores the title of the page as a String id,
 *    and the full text of the wikipedia page in a String called pageText
 *
 * Representation Invariant:
 *    item is not null
 *    Strings pageTitle and pageText are not null nor empty
 *    pageTitle refers to the exact title of an existing Wikipedia page
 *
 */

//This is used for testing the cache
class JSONObj implements Cacheable {

	public JSONObject item;

	public JSONObj(String pageTitle, String pageText){
		this.item = new JSONObject();
		item.put("id", pageTitle);
		item.put("Page Text", pageText);
	}

	public String id(){
		return item.get("id").toString();
	}

}
