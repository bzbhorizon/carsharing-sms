/**
 * 
 */
package bzb.gae.ws;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import bzb.gae.Utility;

/**
 * @author psxbdb
 *
 */
public class Twitter {

	private static final String TWITTER_URL = "http://search.twitter.com/search.json";
	
	public static JSONObject getSearchResults (String query) throws JSONException {
		String queryString = query;
		try {
			queryString = URLEncoder.encode(queryString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String requestURL = TWITTER_URL + "?q=" + queryString + "&result_type=recent&rpp=100";
		String response = Utility.makeGetRequest(requestURL);
		return new JSONObject(response);
	}
	
	public static JSONObject getSearchResultsByProxy (String query) throws JSONException {
		String queryString = query;
		try {
			queryString = URLEncoder.encode(queryString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new JSONObject(Utility.makeGetRequest("http://www.growlingfish.com/dtcsummer_twitter.php?q=" + queryString));
	}
}
