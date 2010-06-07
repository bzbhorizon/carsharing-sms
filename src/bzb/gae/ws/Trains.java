/**
 * 
 */
package bzb.gae.ws;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bzb.gae.Utility;
import bzb.gae.summer.TweetChecker;

/**
 * @author psxbdb
 *
 */
public abstract class Trains {

	private static final Logger log = Logger
	.getLogger(Trains.class.getName());
	
	private static final String LDB_URL = "http://train-info.georgehindle.com/";
	
	public static JSONArray getTrainsArrivingAt (String stationCode) throws JSONException {
		String requestURL = LDB_URL + "arrivals/?station=" + stationCode + "&limit=30&format=json";
		String response = Utility.makeGetRequest(requestURL);
		log.warning(response);
		return new JSONArray(response);
	}
	
	public static JSONArray getTrainsArrivingAt (String stationCode, String arrivalTime) throws JSONException {
		JSONArray trains = getTrainsArrivingAt(stationCode);
		JSONArray trainsJson = new JSONArray();
		for (int i = 0; i < trains.length(); i++) {
			JSONObject train = (JSONObject) trains.get(i);
			String[] scheduled = ((String)train.get("scheduled")).split(":");
			if ((scheduled[0] + scheduled[1]).equals(arrivalTime)) {
				trainsJson.put(train);
			}
		}
		return trainsJson;
	}
	
	public static JSONArray getTrainsDepartingFrom (String stationCode) throws JSONException {
		String requestURL = LDB_URL + "departures/?station=" + stationCode + "&limit=30&format=json";
		String response = Utility.makeGetRequest(requestURL);
		log.warning(response);
		return new JSONArray(response);
	}
}
