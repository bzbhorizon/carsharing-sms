package bzb.gae.meet;

import java.util.logging.Logger;

import bzb.gae.exceptions.TooFewArgumentsException;
import bzb.gae.exceptions.TooManyArgumentsException;

/**
 * @author psxbdb
 *
 */
public class MeetSMS {

	private String network = DEFAULT_NETWORK;
	private String name;
	private String destination;
	private String sender;
	private boolean isDriver = false;
	
	public static String DEFAULT_NETWORK = "UON";
	public static String DRIVER = "DRIVER";
	public static String PASSENGER = "PASSENGER";
	
	private final static int EXPECTED_PARAMETERS = 4;
	
	private static final Logger log = Logger.getLogger(MeetSMS.class.getName());
	
	public MeetSMS (String[] smsChunks, String originator) throws TooFewArgumentsException, TooManyArgumentsException {
		if (smsChunks.length == EXPECTED_PARAMETERS - 1) {
			log.warning("No third argument found: set to driver");
		} else if (smsChunks.length == EXPECTED_PARAMETERS) {
			setDriver(true);
			log.warning("Third argument found in SMS (" + smsChunks[3] + "): set to driver");
		} else if (smsChunks.length < EXPECTED_PARAMETERS - 1) {
			throw new TooFewArgumentsException();
		} else if (smsChunks.length > EXPECTED_PARAMETERS) {
			throw new TooManyArgumentsException();
		}
		setName(smsChunks[1]);
		setDestination(smsChunks[2]);
		setSender(originator);
		log.info("SMS split: " + getSummary());
	}
	
	public String getSummary () {
		return "Network=" + getNetwork() + " Name=" + getName() + " Destination=" + getDestination() + " Number=" + getSender() + " Driver=" + isDriver();
	}
	
	/**
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		this.network = network;
	}

	/**
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param sender the phoneNumber to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param isDriver the isDriver to set
	 */
	public void setDriver(boolean isDriver) {
		this.isDriver = isDriver;
	}

	/**
	 * @return the isDriver
	 */
	public String isDriver() {
		if (isDriver) {
			return DRIVER;
		} else {
			return PASSENGER;
		}
	}
	
}
