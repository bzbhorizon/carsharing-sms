/**
 * 
 */
package bzb.gae.summer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bzb.gae.PMF;
import bzb.gae.Utility;
import bzb.gae.summer.jdo.Group;
import bzb.gae.summer.jdo.User;
import bzb.gae.ws.Trains;

/**
 * @author psxbdb
 *
 */
@SuppressWarnings("serial")
public class StateServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		response.setContentType("text/plain");
		
		JSONObject body = null;
		JSONArray nonEmptyGroups = new JSONArray();
		JSONArray emptyGroups = new JSONArray();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery("select from " + Group.class.getName());
			q.setOrdering("arrivalTime asc");
		    List<Group> groups = (List<Group>) q.execute();
		    Iterator<Group> it = groups.iterator();
		    while (it.hasNext()) {
				Group thisGroup = it.next();
				List<User> users = thisGroup.getUsers(null);
				JSONObject groupJson;
				try {
					groupJson = new JSONObject().put("id", thisGroup.getKey().toString());
					groupJson.put("arrivalTime", thisGroup.getArrivalTime());
					if (Utility.getMinutesUntil(thisGroup.getArrivalTime()) > 0) {
						groupJson.put("arrived", false);
					} else {
						groupJson.put("arrived", true);
					}
					JSONArray trainsJson = Trains.getTrainsArrivingAt("not", thisGroup.getArrivalTime());
					groupJson.put("matches", trainsJson);
					if (users.size() > 0) {
						JSONArray usersJson = new JSONArray();
						Iterator<User> itu = users.iterator();
						while (itu.hasNext()) {
							User thisUser = itu.next();
							JSONObject userJson;
							try {
								userJson = new JSONObject().put("username", thisUser.getUsername());
								userJson.put("phoneNumber", thisUser.getContact());
								usersJson.put(userJson);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						groupJson.put("users", usersJson);
						nonEmptyGroups.put(groupJson);
					} else {
						emptyGroups.put(groupJson);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}			
		    }
		    body = new JSONObject().put("groups", nonEmptyGroups);
		    body.put("emptyGroups", emptyGroups);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		response.getWriter().println(body.toString());		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
