package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;
import org.json.JSONObject;

class JSONObj implements Cacheable {

	private JSONObject item;
	public String id;
	public String text;

	public JSONObj(String pageTitle, String pageText){
		this.item = new JSONObject();
		item.put("id", pageTitle);
		item.put("Page Text", pageText);
		this.id = pageTitle;
		this.text = pageText;
	}

	public String id(){
		return item.get("id").toString();
	}

}
