/**
 * 
 */
package bzb.gae.summer.jdo;

import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import bzb.gae.PMF;

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
	
	public static User getUser (String phoneNumber) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery("select from " + User.class.getName());
		    List<User> users = (List<User>) q.execute();
		    Iterator<User> it = users.iterator();
		    while (it.hasNext()) {
				User thisUser = it.next();
				if (thisUser.getPhoneNumber().equals(phoneNumber)) {
					return thisUser;
				}
		    }
		} finally {
			pm.close();
		}
		return null;
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
