/**
 * 
 */
package bzb.gae.ws;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import bzb.gae.PMF;
import bzb.gae.Utility;
import bzb.gae.jdo.App;

/**
 * @author psxbdb
 *
 */
public class Twitter {

	/*private static final String TWITTER_URL = "http://search.twitter.com/search.json";
	
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
	}*/
	
	public static JSONObject getSearchResultsByProxy (String query, String appName) throws JSONException {
		String queryString = query;
		try {
			queryString = URLEncoder.encode(queryString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSONObject result;
		if (appName != null) {
			result = new JSONObject(Utility.makeGetRequest("http://www.growlingfish.com/dtcsummer_twitter.php?q=" + queryString + "&since=" + getLastCheckedIDForApp(appName))); 
		} else {
			result = new JSONObject(Utility.makeGetRequest("http://www.growlingfish.com/dtcsummer_twitter.php?q=" + queryString));
		}
		return result;
	}
	
	public static long getLastCheckedIDForApp (String appName) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			App app = pm.getObjectById(App.class, appName);
			return app.getLastCheckedTweetID();
		} catch (JDOObjectNotFoundException je) {
			App app = new App(appName);
			pm.makePersistent(app);
			return app.getLastCheckedTweetID();
		} finally {
			pm.close();
		}
	}
	
	public static void setLastCheckedIDForApp (String appName, long lastCheckedID) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			App app = pm.getObjectById(App.class, appName);
			app.setLastCheckedTweetID(lastCheckedID);
		} catch (JDOObjectNotFoundException je) {
			App app = new App(appName);
			app.setLastCheckedTweetID(lastCheckedID);
			pm.makePersistent(app);
		} finally {
			pm.close();
		}
	}
	
	public static boolean sendDirectMessage (String target, String body) {
		if (Utility.isValidTwitter(target)) {
			target = target.substring(1); // remove the @
			twitter4j.Twitter twitter = new TwitterFactory().getInstance("horizoncar","horizon2010");
		    try {
				twitter.sendDirectMessage(target, body);
				return true;
			} catch (TwitterException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
}
