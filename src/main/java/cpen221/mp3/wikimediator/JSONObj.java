package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;
import org.json.JSONObject;

/**
 * Abstraction Function:
 *    item is a JSONObject containing a String id representing the title of the Wikipedia page
 *    and a String containing all the text that is contained in the Wikipedia page with the
 *    title of 'id'
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
