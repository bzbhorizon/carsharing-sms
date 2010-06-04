/**
 * 
 */
package bzb.gae;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author psxbdb
 * 
 */
@SuppressWarnings("serial")
public class SendSMSServlet extends HttpServlet {

	/*private static final Logger log = Logger
			.getLogger(SendSMSServlet.class.getName());*/

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (request.getParameter("body") != null && request.getParameter("recipient") != null) {
			response.setContentType("text/plain");
			JSONObject body = null;
			if (Utility.sendSMS(request.getParameter("recipient"), request.getParameter("body")) == null) {
				try {
					body = new JSONObject().put("success", false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				try {
					body = new JSONObject().put("success", true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			response.getWriter().println(body.toString());	
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
