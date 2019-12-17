package cpen221.mp3.server;

import cpen221.mp3.wikimediator.WikiMediator;
import org.json.JSONObject;

import java.util.List;

public class testJSONObject {
    public JSONObject process(JSONObject n) {
        JSONObject result = new JSONObject(n);
        String type = n.getString("type").replaceAll(",", "");
        if (type.equals("simpleSearch")) {
            String query = n.getString("query").replaceAll(",", "");
            int limit = n.optInt("limit");
            WikiMediator process = new WikiMediator();
            List<String> response = process.simpleSearch(query, limit);
            result.put("response", response);
        }
        return result;
    }

    public static void main(String[] args) {
            JSONObject x = new JSONObject();
            x.put("id", 1);
            x.put("type", "simpleSearch");
            x.put("query", "Disney");
            x.put("limit", 5);

            testJSONObject test1 = new testJSONObject();
            JSONObject result = test1.process(x);
            String type = x.getString("type").replaceAll(",", "");
            System.out.println(type);
            System.out.println(result.get("response"));
    }
}
