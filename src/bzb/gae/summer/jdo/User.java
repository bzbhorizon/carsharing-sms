/**
 * 
 */
package bzb.gae.summer.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * @author bzb
 *
 */
@PersistenceCapable
public class User {

	@PrimaryKey
	@Persistent
    private String username;
	
	@Persistent
	private String phoneNumber;
	
	@Persistent
	private String arrivalTime;
	
	@Persistent
	private Key groupKey;
	
	public User (String username, String phoneNumber, String arrivalTime, Key groupKey) {
		setUsername(username);
		setPhoneNumber(phoneNumber);
		setArrivalTime(arrivalTime);
		setGroupKey(groupKey);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setGroupKey(Key groupKey) {
		this.groupKey = groupKey;
	}

	public Key getGroupKey() {
		return groupKey;
	}
	
}
