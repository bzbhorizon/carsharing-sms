/**
 * 
 */
package bzb.gae.summer;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bzb.gae.exceptions.BadArrivalTimeException;
import bzb.gae.exceptions.TooFewArgumentsException;
import bzb.gae.exceptions.TooManyArgumentsException;
import bzb.gae.exceptions.UserAlreadyExistsException;
import bzb.gae.exceptions.UserNotFoundException;
import bzb.gae.ws.Twitter;

/**
 * @author psxbdb
 * 
 */
@SuppressWarnings("serial")
public class TweetChecker extends HttpServlet {
	
	private static final Logger log = Logger
			.getLogger(TweetChecker.class.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		log.warning("Checking for new tweets");
		JSONObject tweets = new JSONObject();
		try {
			tweets = Twitter.getSearchResultsByProxy("#dtcsummer", "summer");
			log.warning(tweets.toString());
			
			long latestID = 0;
			JSONArray results = (JSONArray) tweets.get("results");
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = (JSONObject) results.get(i);
				if (((Long)result.get("id")).longValue() > Twitter.getLastCheckedIDForApp("summer")) {
					log.warning(result.toString());
					String[] tweetChunks = Pattern.compile("\\s").split((String) result.get("text"));
					try {
						SummerSMS tweet = new SummerSMS("@" + result.get("from_user"), tweetChunks);
					} catch (UserAlreadyExistsException e) {
						e.printStackTrace();
					} catch (BadArrivalTimeException e) {
						e.printStackTrace();
					} catch (TooManyArgumentsException e) {
						e.printStackTrace();
					} catch (TooFewArgumentsException e) {
						e.printStackTrace();
					} catch (UserNotFoundException e) {
						e.printStackTrace();
					}
				}
				if (((Long)result.get("id")).longValue() > latestID) {
					latestID = ((Long)result.get("id")).longValue();
				}
			}
			
			if (Twitter.getLastCheckedIDForApp("summer") < latestID) {
				Twitter.setLastCheckedIDForApp("summer", latestID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
