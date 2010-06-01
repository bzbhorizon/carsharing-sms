/**
 * 
 */
package bzb.gae.meet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import bzb.gae.Utility;
import bzb.gae.meet.exceptions.NoStateException;

/**
 * @author psxbdb
 * 
 */
@SuppressWarnings("serial")
public class MobileServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(MobileServlet.class
			.getName());
	
	private static final String PARAM_USER = "user";
	private static final String PARAM_REGISTER = "register";
	private static final String ARG_FORM = "form";
	private static final String ARG_DETAILS = "details";
	private static final String PARAM_NAME = "name";
	private static final String PARAM_DEST = "dest";
	private static final String PARAM_DRIVER = "driver";
	private static final String PARAM_MATCHES = "matches";

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		response.setContentType("text/html");
		
		String body = null;
		
		State state;
		try {
			state = new State();
				
			if (request.getParameterMap().containsKey(PARAM_USER)) {
				String user = request.getParameter(PARAM_USER);
				
				if (state.userExists(user)) {
					body = "<p>" + user + ", you are a <span class=\"bold\">";
					if (state.isDriver(user)) {
						body += "driver";
					} else {
						body += "passenger";
					}
					body += "</span>. ";
					
					if (state.hasMatches()) {
						String[] companions = state.getCompanions(user);
						if (companions != null && companions.length > 0) {
							body += "You are travelling with:</p><ul>";
							for (int i = 0; i < companions.length; i++) {
								if (state.isDriver(companions[i])) {
									body += "<li>" + companions[i] + " (driver)</li>";
								} else {
									body += "<li>" + companions[i] + "</li>";
								}
							}
							body += "</ul>";
						} else {
							if (state.isDriver(user)) {
								body += "You have no passengers. Lucky you!</p>";;
							} else {
								if (state.getDrivers().length() > 0) {
									body += "You have no driver; maybe the group generated matches before you joined?"; //<a href=\"?" + PARAM_MATCHES + "=true\">Generate matches</a></p>"
								} else {
									body += "There are no drivers currently registered. You'll have to wait ...</p>";
								}
							}
						}
						
						if (companions != null || state.isDriver(user)) {
							body += "<p class=\"box\"><a class=\"box\" href=\"" + state.buildMapURL(user) + "\">Show route on map</a></p>";
						}
					} else {
						body += "No journeys have been planned yet.</p>";
					}
				} else {
					body = "<p>" + user + ", you haven't registered yourself yet!</p>";
				}
				
				body += "<p><a href=\"mobile\"><< Back to user list</a></p>";
			} else if (request.getParameterMap().containsKey(PARAM_REGISTER)) {
				String action = request.getParameter(PARAM_REGISTER);
				
				if (action.equals(ARG_FORM)) {
					body = "<form method=\"get\" action=\"mobile\"><input name=\"register\" value=\"details\" type=\"hidden\" />Name: <input name=\"name\" /><br />Destination post-code: <input name=\"dest\" /><br />"/*Your phone number (optional): <input name=\"phone\" /><br />*/ + "Driver? <input type=\"radio\" name=\"driver\" value=\"DRIVER\" CHECKED /> Yes <input type=\"radio\" name=\"driver\" value=\"PASSENGER\" /> No<br /><input type=\"submit\" /></form>";
					body += "<p><a href=\"mobile\"><< Cancel registration</a></p>";
				} else if (action.equals(ARG_DETAILS)) {
					if (request.getParameterMap().containsKey(PARAM_NAME) &&
							request.getParameterMap().containsKey(PARAM_DEST) &&
							request.getParameterMap().containsKey(PARAM_DRIVER)) {
						try {
							String strippedDest = request.getParameter(PARAM_DEST).trim().replaceAll(" ", "");
							String getResponseStr = Get.registerGetRequest(MeetSMS.DEFAULT_NETWORK, request.getParameter(PARAM_NAME), strippedDest, request.getParameter(PARAM_DRIVER), "0");
							if (getResponseStr != null) {
								JSONObject getResponse = new JSONObject(getResponseStr);
								if (getResponse.getBoolean("success") == false) {
									body = "<p>Registration failed (" + getResponse.get("cause") + ").</p><p><a href=\"mobile\"><< Back to user list</a></p>";
								} else {
									body = "<p>Sharer added.</p><p><a href=\"mobile\"><< Back to user list</a></p>";
								}
							} else {
								body = "<p>Registration failed.</p><p><a href=\"mobile\"><< Back to user list</a></p>";
							}
						} catch (JSONException e) {
							body = "<p>Sharer added.</p><p><a href=\"mobile\"><< Back to user list</a></p>";
						}
					} else {
						body = "<p>Registration failed.</p><p><a href=\"mobile\"><< Back to user list</a></p>";
					}
				}
			} else if (request.getParameterMap().containsKey(PARAM_MATCHES)) {
				String check = request.getParameter(PARAM_MATCHES);
				if (check.equals("true")) {
					log.warning(Utility.sendGetRequest("http://carsharing-gae.appspot.com/carsharing/network/create?mode=start"));
					String getResponseStr = Utility.sendGetRequest("http://carsharing-gae.appspot.com/carsharing/network/generatematches");
					log.warning(getResponseStr);
					if (getResponseStr != null) {
						JSONObject getResponse;
						try {
							getResponse = new JSONObject(getResponseStr);
							if (getResponse.has("success") && getResponse.getBoolean("success") == false) {
								body = "<p>No matches generated. No drivers?</p><p><a href=\"mobile\"><< Back to user list</a></p>";
							} else if (getResponse.has("noJourneyMatches")) {
								body = "<p>" + getResponse.getString("noJourneyMatches") + " matches generated.</p><p><a href=\"mobile\"><< Back to user list</a></p>";
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						body = "<p>Matches generated.</p><p><a href=\"mobile\"><< Back to user list</a></p>";
					}
				}
			} else {
				// shouldn't be able to generate matches via the mobile - should be a group thing
				/*if (!state.hasMatches()) {
					body = "<p>No matches made yet. ";
					if (state.getDrivers().length() > 0) {
						body += "</p><p class=\"box\"><a class=\"box\" href=\"?" + PARAM_MATCHES + "=true\">Generate matches</a></p>";
					} else {
						body += "There are no drivers so matches cannot be generated.</p>";
					}
				} else {
					body = "<p>Matches have previously been made.</p><p class=\"box\"><a class=\"box\" href=\"?" + PARAM_MATCHES + "=true\">Regenerate matches</a></p>";
				}*/
				
				if (state.getUsers().length > 0) {
					body = "<p>The sharers currently registered are:</p><ul>";
					for (String user : state.getUsers()) {
						body += "<li>";
						if (state.hasMatches()) {
							body += "<a href=\"?" + PARAM_USER + "=" + user + "\">" + user + "</a>";
						} else {
							body += user;
						}
						if (state.isDriver(user)){
							body += " (driver)";
						}
						body += "</li>";
					}
					body += "</ul>";
				} else {
					body = "<p>No sharers registered.</p>";
				}
				
				body += "<p>Note: this list refreshes automatically every 20 seconds.</p><p class=\"box\"><a class=\"box\" href=\"mobile\">Refresh list now</a> | <a class=\"box\" href=\"?" + PARAM_REGISTER + "=" + ARG_FORM + "\">Register new sharer</a></p>";
				
			}
		} catch (NoStateException e1) {
			e1.printStackTrace();
			body = "<p>Service currently experiencing hiccups</p><p class=\"box\"><a class=\"box\" href=\"mobile\">Refresh</a></p>";
		}

		String html = "<body><h1>Car Sharing</h1><p>" + body + "</p></body></html>";
		if (request.getParameterMap().isEmpty()) {
			response.getWriter().println(Utility.headWithRefreshAndTitle("Car Sharing") + html);
		} else {
			response.getWriter().println(Utility.headWithTitle("Car Sharing") + html);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

}
