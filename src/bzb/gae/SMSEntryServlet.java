package bzb.gae;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bzb.gae.exceptions.BadArrivalTimeException;
import bzb.gae.exceptions.TooFewArgumentsException;
import bzb.gae.exceptions.TooManyArgumentsException;
import bzb.gae.exceptions.UserAlreadyExistsException;
import bzb.gae.meet.MeetSMS;
import bzb.gae.summer.SummerSMS;

@SuppressWarnings("serial")
public class SMSEntryServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(SMSEntryServlet.class
			.getName());

	private static final String[][] apps = {
			{ "meet", "ben ng35bb yes\" to register ben as a driver",
					"ben ng3bb\" to register ben as a passenger" },
			{ "summer", "ben 1342\" to register ben on the 1:42PM train",
						"ben 942\" to register ben on the 9:42AM train" } };
	private static final int NAME = 0;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String html = "<h1>SMS Entry for Car Sharing apps</h1>";
		response.setContentType("text/html");

		if (!request.getParameterNames().hasMoreElements()) {
			html += getUsageInfo();
			log.warning("No parameters given; showing usage help");
		} else if (request.getParameter("body") != null) {
			String[] smsChunks = Pattern.compile("\\s").split(
					request.getParameter("body"));

			String originator = request.getParameter("originator");
			if (originator != null) {
				originator = originator.trim();
			} else {
				originator = "0";
			}

			String app = smsChunks[0].trim().toLowerCase();
			log.warning("SMS from " + originator + " intended for " + app
					+ " app; checking ...");

			if (app.equals(apps[0][NAME])) { // meet
				try {
					MeetSMS sms = new MeetSMS(smsChunks, originator);
					log.warning("Response: "
							+ bzb.gae.meet.Rest.registerGetRequest(sms
									.getNetwork(), sms.getName(), sms
									.getDestination(), sms.isDriver(), sms
									.getSender()));
					// no need to reply by text - if it works, it'll show up on the public display
				} catch (TooManyArgumentsException tme) {
					// send too many arguments message
					log.warning(Utility.sendSMS(originator, "There were too many words in your SMS; you could text something like " + getUsageInfoForSMS(0)));
					html += getUsageInfo(0);
					log.warning("tme");
				} catch (TooFewArgumentsException tfe) {
					// send too few arguments message
					log.warning(Utility.sendSMS(originator, "There were too few words in your SMS; you could text something like " + getUsageInfoForSMS(0)));
					html += getUsageInfo(0);
					log.warning("tfe");
				}
			} else if (app.equals(apps[1][NAME])) { // summer
				try {
					SummerSMS ss = new SummerSMS(originator, smsChunks);
					html += "<p>User " + ss.getUsername() + " registered</p>";
				} catch (UserAlreadyExistsException ue) {
					// send updated user message
					log.warning(Utility.sendSMS(originator, "You've already registered that user: we've updated the previous entry with your new details"));
					html += "<p>User already registered; user updated</p>";
					log.warning("ue");
				} catch (BadArrivalTimeException be) {
					// send request for correct arrival time message
					log.warning(Utility.sendSMS(originator, "We couldn't understand the arrival time you gave; you could text something like " + getUsageInfoForSMS(1)));
					html += getUsageInfo(1);
					log.warning("be");
				} catch (TooManyArgumentsException tme) {
					// send too many arguments message
					log.warning(Utility.sendSMS(originator, "There were too many words in your SMS; you could text something like " + getUsageInfoForSMS(1)));
					html += getUsageInfo(1);
					log.warning("tme");
				} catch (TooFewArgumentsException tfe) {
					// send too few arguments message
					log.warning(Utility.sendSMS(originator, "There were too few words in your SMS; you could text something like " + getUsageInfoForSMS(1)));
					html += getUsageInfo(1);
					log.warning("tfe");
				}
			} else {
				html += getUsageInfo();
				log.warning("No matching app found");
			}
		} else {
			html += getUsageInfo();
			log
					.warning("Some parameters but neither body nor originator - odd?");
		}

		response.getWriter().println(html);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

	public String getUsageInfo() {
		String html = "<p>SMSes sent should match one of the following:</p>"
				+ "<ul>";

		for (int i = 0; i < apps.length; i++) {
			html += "<li>Scenario " + (i + 1) + "</li>" + "<ul>";
			for (int j = 1; j < apps[i].length; j++) {
				html += "<li>\"" + apps[i][NAME] + " " + apps[i][j] + "</li>";
			}
			html += "</ul>";
		}

		html += "</ul>";

		return html;
	}

	public String getUsageInfo(int app) {
		String html = "<p>SMSes sent should match one of the following:</p>"
				+ "<ul>";
		for (int j = 1; j < apps[app].length; j++) {
			html += "<li>\"" + apps[app][NAME] + " " + apps[app][j] + "</li>";
		}
		html += "</ul>";

		return html;
	}
	
	public String getUsageInfoForSMS (int app) {
		String smsBody = "";
		//for (int i = 1; i < apps[app].length; i++) {
			smsBody += "\"" + apps[app][NAME] + " " + apps[app][1];
			//if (i < apps[app].length - 1) {
			//	smsBody += " or ";
			//}
		//}
		
		return smsBody;
	}
}
