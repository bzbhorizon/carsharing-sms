/**
 * 
 */
package bzb.gae.summer;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

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
	
	public User (String username, String phoneNumber, String arrivalTime) {
		setUsername(username);
		setPhoneNumber(phoneNumber);
		setArrivalTime(arrivalTime);
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
	
}
