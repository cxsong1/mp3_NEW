package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;
import org.json.JSONObject;

/**
 * Abstraction Function:
 *    item is a JSONObject that implements the interface Cacheable,
 *    it takes a wikipedia page, and store the title of the wikipedia page as a String id,
 *    and store the full text of the wikipedia page in a String pageText
 *
 * Representation Invariant:
 *    item is not null
 *    Strings pageTitle and pageText are not null or empty
 *    pageTitle refers to the exact title of an existing Wikipedia page
 *
 */
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
