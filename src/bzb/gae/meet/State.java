package bzb.gae.meet;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bzb.gae.Utility;
import bzb.gae.meet.exceptions.NoStateException;

public class State {

	private static final Logger log = Logger.getLogger(State.class
			.getName());
	
	private String jsonResponse;
	private JSONObject json;
	private JSONArray drivers;
	private JSONArray passengers;
	private JSONArray journeys;

	public static final String DRIVERS_KEY = "drivers";
	public static final String DRIVER_KEY = "driver";
	public static final String PASSENGERS_KEY = "passengers";
	public static final String NAME_KEY = "name";
	public static final String LASTMATCH_KEY = "lastJourneyMatching";
	public static final String MATCHES_KEY = "journeyMatches";
	public static final String JOURNEYS_KEY = "journeys";
	public static final String STARTPOINT_KEY = "startPoint";
	public static final String LAT_KEY = "lat";
	public static final String LON_KEY = "lon";
	public static final String DESTINATION_KEY = "destination";
	public static final String DISTANCE_KEY = "distance";

	public State() throws NoStateException {
		setJsonResponse(Utility
				.sendGetRequest("http://carsharing-gae.appspot.com/carsharing/network/checkstate"));
		
		if (getJsonResponse() != null) {
			try {
				json = new JSONObject(getJsonResponse());
				setDrivers((JSONArray) json.get(DRIVERS_KEY));
				setPassengers((JSONArray) json.get(PASSENGERS_KEY));
				if (!json.get(MATCHES_KEY).equals(null)) {
					setJourneys((JSONArray) ((JSONObject) json.get(MATCHES_KEY))
							.get(JOURNEYS_KEY));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				throw new NoStateException();
			}
		} else {
			throw new NoStateException();
		}
		
	}

	public boolean userExists (String user) {
		for (int i = 0; i < drivers.length(); i++) {
			try {
				if (((JSONObject) drivers.get(i)).get(NAME_KEY).equals(user)) {
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < passengers.length(); i++) {
			try {
				if (((JSONObject) passengers.get(i)).get(NAME_KEY).equals(user)) {
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean hasMatches() {
		try {
			if (json.get(LASTMATCH_KEY).equals(null)) {
				return false;
			} else {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isDriver(String user) {
		for (int i = 0; i < drivers.length(); i++) {
			try {
				if (((JSONObject) drivers.get(i)).get(NAME_KEY).equals(user)) {
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
/*	http://maps.google.com/maps/api/staticmap?size=512x512&maptype=roadmap
		&markers=color:blue|label:S|40.702147,-74.015794&markers=color:green|label:G|40.711614,-74.012318
		&markers=color:red|color:red|label:C|40.718217,-73.998284&sensor=false
*/
	public float getJourneyDistance (JSONObject journey) {
		try {
			return journey.getLong(DISTANCE_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public String buildMapURL (String user) {
		//String URL = "http://maps.google.com/maps/api/staticmap?size=320x480&mobile=true&maptype=roadmap";
		String URL = "http://maps.google.com/?t=m";
		JSONObject thisJourney = getUserJourney(user);
		if (thisJourney != null) {
			try {
				boolean finished = false;
				
				JSONObject startPoint = (JSONObject) thisJourney.get(STARTPOINT_KEY);
				URL += "&saddr=" + startPoint.getString(LAT_KEY) + "," + startPoint.getString(LON_KEY);
				
				//"+to:"
				JSONArray passengers = thisJourney.getJSONArray(PASSENGERS_KEY);
				for(int i = 0; i < passengers.length() && !finished; i++) {
					JSONObject midPoint = (JSONObject) ((JSONObject) passengers.get(i)).get(DESTINATION_KEY);
					
					if (((JSONObject)passengers.get(i)).get(NAME_KEY).equals(user)){
						finished = true;
						URL += "&daddr=";
					} else {
						URL += "+to:";
					}
					
					URL += midPoint.getString(LAT_KEY) + "," + midPoint.getString(LON_KEY);
				}
				
				if (!finished) {
					JSONObject endPoint = (JSONObject) ((JSONObject) thisJourney.get(DRIVER_KEY)).get(DESTINATION_KEY);
					URL += "&daddr=" + endPoint.getString(LAT_KEY) + "," + endPoint.getString(LON_KEY);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		log.warning(URL);
		return URL;
	}
	
	public JSONObject getUserJourney (String user) {
		if (journeys != null) {
			if (isDriver(user)) {
				for (int i = 0; i < journeys.length(); i++) {
					try {
						JSONObject thisJourney = (JSONObject) journeys.get(i);
						if (((JSONObject) (thisJourney).get(DRIVER_KEY)).get(
								NAME_KEY).equals(user)) {
							return thisJourney;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				for (int i = 0; i < journeys.length(); i++) {
					try {
						JSONObject thisJourney = (JSONObject) journeys.get(i);
						JSONArray passengers = (JSONArray) thisJourney.get(PASSENGERS_KEY);
						for (int j = 0; j < passengers.length(); j++) {
							String passenger = ((JSONObject) passengers.get(j)).getString(NAME_KEY);
							if (passenger.equals(user)) {
								return thisJourney;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public String[] getCompanions(String user) {
		String[] companions = null;
		if (journeys != null) {
			if (isDriver(user)) {
				for (int i = 0; i < journeys.length(); i++) {
					try {
						JSONObject thisJourney = (JSONObject) journeys.get(i);
						if (((JSONObject) (thisJourney).get(DRIVER_KEY)).get(
								NAME_KEY).equals(user)) {
							JSONArray passengers = (JSONArray) thisJourney
									.get(PASSENGERS_KEY);
							companions = new String[passengers.length()];
							for (int j = 0; j < passengers.length(); j++) {
								companions[j] = ((JSONObject) passengers.get(j))
										.getString(NAME_KEY);
							}
							return companions;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				boolean found = false;
				for (int i = 0; i < journeys.length(); i++) {
					try {
						JSONObject thisJourney = (JSONObject) journeys.get(i);
						JSONArray passengers = (JSONArray) thisJourney.get(PASSENGERS_KEY);
						companions = new String[passengers.length()];
						for (int j = 0; j < passengers.length(); j++) {
							String passenger = ((JSONObject) passengers.get(j)).getString(NAME_KEY);
							if (!passenger.equals(user)) {
								companions[j] = passenger; 
							} else {
								found = true;
								companions[j] = ((JSONObject) thisJourney.get(DRIVER_KEY)).getString(NAME_KEY);
							}
						}
						if (found) {
							return companions;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				companions = null;
			}
		}
		return companions;
	}

	public String[] getUsers() {
		String[] users = new String[drivers.length() + passengers.length()];

		for (int i = 0; i < users.length; i++) {
			if (i < drivers.length()) {
				try {
					users[i] = ((JSONObject) drivers.get(i))
							.getString(NAME_KEY);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				try {
					users[i] = ((JSONObject) passengers.get(i
							- drivers.length())).getString(NAME_KEY);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return users;
	}

	/**
	 * @param jsonResponse
	 *            the jsonResponse to set
	 */
	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	/**
	 * @return the jsonResponse
	 */
	public String getJsonResponse() {
		return jsonResponse;
	}

	/**
	 * @param jsonArray
	 *            the drivers to set
	 */
	public void setDrivers(JSONArray jsonArray) {
		this.drivers = jsonArray;
	}

	/**
	 * @return the drivers
	 */
	public JSONArray getDrivers() {
		return drivers;
	}

	/**
	 * @param jsonArray
	 *            the passengers to set
	 */
	public void setPassengers(JSONArray jsonArray) {
		this.passengers = jsonArray;
	}

	/**
	 * @return the passengers
	 */
	public JSONArray getPassengers() {
		return passengers;
	}

	/**
	 * @param journeys
	 *            the journeys to set
	 */
	public void setJourneys(JSONArray journeys) {
		this.journeys = journeys;
	}

	/**
	 * @return the journeys
	 */
	public JSONArray getJourneys() {
		return journeys;
	}

}
