package bzb.gae;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public abstract class Utility {
	
	public static String sendGetRequest(String request) {
		String result = null;
		try {
			URL url = new URL(request);
			URLConnection conn = url.openConnection();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static final String DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";

	public static String headWithTitle(String title) {
		String html = DOCTYPE + "\n" + "<html>\n" + "<head><title>" + title + "</title>" +
				"<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\" />" +
				"</head>";
		
		return html;
	}
	
	public static String headWithRefreshAndTitle(String title) {
		String html = DOCTYPE + "\n" + "<html>\n" + "<head><title>" + title + "</title>" +
				"<meta http-equiv=\"refresh\" content=\"20\">" +
				"<link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\" />" +
				"</head>";
		
		return html;
	}
	
	public static String printHeaders (HttpServletRequest request) {
		String headers = "<ul>";
		Enumeration e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String headerName = (String)e.nextElement();
			headers += "<li>" + headerName + "=" + request.getHeader(headerName) + "</li>";
		}
		headers += "</ul>";
		return headers;
	}
	
}
