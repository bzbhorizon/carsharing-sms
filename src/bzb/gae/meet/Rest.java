package bzb.gae.meet;

import bzb.gae.Utility;

public abstract class Rest {
	
	public static final String registerURL = "http://carsharing-gae.appspot.com/carsharing/destination/set";

	public static String registerGetRequest (String network, String name, String destination, String isDriver, String sender) {
		return Utility.makeGetRequest(registerURL + "?network=" + network + "&name=" + name + "&postcode=" + destination + "&role=" + isDriver + "&number=" + sender);
	}
	
}
