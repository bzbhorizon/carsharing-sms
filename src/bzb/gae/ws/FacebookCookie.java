/**
 * 
 */
package bzb.gae.ws;

import javax.servlet.http.Cookie;

/**
 * @author bzb
 *
 */
public class FacebookCookie {
		
	private Cookie cookie;
	private String token;
	private String firstName;
	private String lastName;
	private String profileURL;
	
	public FacebookCookie (Cookie cookie) {
		setCookie(cookie);
		String value = cookie.getValue();
		String[] bits = value.split("&");
		for (int i = 0; i < bits.length; i++) {
			String[] pair = bits[i].split("=");
			if (pair[0].equals("access_token")) {
				setToken(pair[1]);
			} else if (pair[0].equals("first_name")) {
				setFirstName(pair[1]);
			} else if (pair[0].equals("last_name")) {
				setLastName(pair[1]);
			} else if (pair[0].equals("profile_url")) {
				setProfileURL("http://www.facebook.com/profile.php?id=" + pair[1]);
			}
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
	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}
	public String getProfileURL() {
		return profileURL;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

}
