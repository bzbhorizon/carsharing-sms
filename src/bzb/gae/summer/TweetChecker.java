/**
 * 
 */
package bzb.gae.summer;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

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
			tweets = Twitter.getSearchResultsByProxy("#dtcsummer");
			log.warning(tweets.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
