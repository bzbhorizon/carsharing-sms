package bzb.gae;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bzb.gae.meet.MeetSMS;
import bzb.gae.meet.Get;
import bzb.gae.summer.SummerSMS;
import bzb.gae.summer.exceptions.BadArrivalTimeException;
import bzb.gae.summer.exceptions.TooFewArgumentsException;
import bzb.gae.summer.exceptions.TooManyArgumentsException;
import bzb.gae.summer.exceptions.UserAlreadyExistsException;

@SuppressWarnings("serial")
public class SMSEntryServlet extends HttpServlet {

	private static final Logger log = Logger
			.getLogger(SMSEntryServlet.class.getName());
	
	private static final String[][] apps = {
			{"meet",
				"ben ng35bb yes\" to register ben as a driver",
				"ben ng3bb\" to register ben as a passenger"},
			{"summer",
				"ben 942\" to register ben on the 9:42AM train",
				"ben 1342\" to register ben on the 1:42PM train"}
	};
	private static final int NAME = 0;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String html = "<h1>SMS Entry for Car Sharing apps</h1>";
		response.setContentType("text/html");
		
		if (!request.getParameterNames().hasMoreElements()) {
			html += getUsageInfo();
			log.warning("No parameters given; showing usage help");
		} else if (request.getParameter("body") != null) {
			String[] smsChunks = 
			      Pattern.compile("\\s").split(request.getParameter("body"));
			
			String originator = request.getParameter("originator");
			if (originator != null) {
				originator = originator.trim();
			} else {
				originator = "0";
			}
			
			String app = smsChunks[0].trim().toLowerCase();
			log.warning("SMS from " + originator + " intended for " + app + " app; checking ...");
			
			if (app.equals(apps[0][NAME])) { // meet
				MeetSMS sms = new MeetSMS(smsChunks, originator);

				log.warning("Response: " + Get.registerGetRequest(sms.getNetwork(), sms.getName(), sms.getDestination(), sms.isDriver(), sms.getSender()));
			} else if (app.equals(apps[1][NAME])) { // summer
				try {
					SummerSMS ss = new SummerSMS(originator, smsChunks);
					html += "<p>User " + ss.getUsername() + " registered</p>";
				} catch (UserAlreadyExistsException ue) { // Tried to register with an existing username
					// send updated user message
					html += "<p>User already registered; user updated</p>";
					log.warning("ue");
				} catch (BadArrivalTimeException be) {
					// send request for correct arrival time message
					html += getUsageInfo(1);
					log.warning("be");
				} catch (TooManyArgumentsException tme) {
					// send too many arguments message
					html += getUsageInfo(1);
					log.warning("tme");
				} catch (TooFewArgumentsException tfe) {
					// send too few arguments message
					html += getUsageInfo(1);
					log.warning("tfe");
				}
			} else {
				html += getUsageInfo();
				log.warning("No matching app found");
			}
		} else {
			html += getUsageInfo();
			log.warning("Some parameters but neither body nor originator - odd?");
		}
		
		response.getWriter().println(html);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
	
	public String getUsageInfo () {
		String html = "<p>SMSes sent should match one of the following:</p>" +
		"<ul>";
		
		for (int i = 0; i < apps.length; i++) {
			html += "<li>Scenario " + (i + 1) + "</li>" +
					"<ul>";
			for (int j = 1; j < apps[i].length; j++) {
				html += "<li>\"" + apps[i][0] + " " + apps[i][j] + "</li>";
			}
			html += "</ul>";
		}
		
		html += "</ul>";
		
		return html;
	}
	
	public String getUsageInfo (int app) {
		String html = "<p>SMSes sent should match one of the following:</p>"
			+ "<ul>";
		for (int j = 1; j < apps[app].length; j++) {
			html += "<li>\"" + apps[app][0] + " " + apps[app][j] + "</li>";
		}
		html += "</ul>";
		
		return html;
	}
}
