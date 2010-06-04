/**
 * 
 */
package bzb.gae.ws;

import org.json.JSONArray;
import org.json.JSONException;

import bzb.gae.Utility;

/**
 * @author psxbdb
 *
 */
public abstract class Trains {

	private static final String LDB_URL = "http://train-info.georgehindle.com/";
	
	public static JSONArray getTrainsArrivingAt (String stationCode) throws JSONException {
		String requestURL = LDB_URL + "arrivals/?station=" + stationCode + "&limit=30&format=json";
		String response = Utility.makeGetRequest(requestURL);
		return new JSONArray(response);
	}
	
	public static JSONArray getTrainsDepartingFrom (String stationCode) throws JSONException {
		String requestURL = LDB_URL + "departures/?station=" + stationCode + "&limit=30&format=json";
		String response = Utility.makeGetRequest(requestURL);
		return new JSONArray(response);
	}
}
