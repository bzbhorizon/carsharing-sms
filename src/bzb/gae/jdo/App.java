/**
 * 
 */
package bzb.gae.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * @author bzb
 *
 */
@PersistenceCapable
public class App {

	@PrimaryKey
	@Persistent
    private String appName;
	
	@Persistent
	private long lastCheckedTweetID;
		
	public App (String appName) {
		setAppName(appName);
		setLastCheckedTweetID(0);
	}

	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param lastCheckedTweetID the lastCheckedTweetID to set
	 */
	public void setLastCheckedTweetID(long lastCheckedTweetID) {
		this.lastCheckedTweetID = lastCheckedTweetID;
	}

	/**
	 * @return the lastCheckedTweetID
	 */
	public long getLastCheckedTweetID() {
		return lastCheckedTweetID;
	}

}
