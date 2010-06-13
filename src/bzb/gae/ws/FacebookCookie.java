/**
 * 
 */
package bzb.gae.ws;

import javax.servlet.http.Cookie;

import org.json.JSONException;
import org.json.JSONObject;

import bzb.gae.Utility;

/**
 * @author bzb
 *
 */
public class FacebookCookie {
	
	private Cookie cookie;
	private String token;
	private String secret;
	private String session;
	private String sig;
	private String uid;
	private String[] name;
	private String profileURL;
	
	public FacebookCookie (Cookie cookie) {
		setCookie(cookie);
		String value = cookie.getValue();
		String[] bits = value.split("&");
		for (int i = 0; i < bits.length; i++) {
			String[] pair = bits[i].split("=");
			if (pair[0].equals("access_token")) {
				setToken(pair[1]);
			} else if (pair[0].equals("access_token")) {
				setSecret(pair[1]);
			} else if (pair[0].equals("session_key")) {
				setSession(pair[1]);
			} else if (pair[0].equals("sig")) {
				setSig(pair[1]);
			} else if (pair[0].equals("uid")) {
				setUid(pair[1]);
			}
		}
		try {
			JSONObject userJson = new JSONObject(Utility.makeGetRequest("https://graph.facebook.com/me?access_token=" + getToken()));
			setName(new String[]{userJson.getString("first_name"), userJson.getString("last_name")});
			setProfileURL(userJson.getString("link"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}
	public Cookie getCookie() {
		return cookie;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getToken() {
		return token;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getSecret() {
		return secret;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getSession() {
		return session;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	public String getSig() {
		return sig;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUid() {
		return uid;
	}
	public void setName(String[] name) {
		this.name = name;
	}
	public String[] getName() {
		return name;
	}
	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}
	public String getProfileURL() {
		return profileURL;
	}

}
