/**
 * 
 */
package bzb.gae.summer;

import java.io.IOException;

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
public class TweetServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/plain");
		JSONObject tweets = new JSONObject();
		try {
			tweets = Twitter.getSearchResultsByProxy("#dtcsummer");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getWriter().println(tweets.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
