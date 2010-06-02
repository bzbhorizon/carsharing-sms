/**
 * 
 */
package bzb.gae.summer.jdo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import bzb.gae.PMF;
import bzb.gae.Utility;

import com.google.appengine.api.datastore.Key;

/**
 * @author bzb
 *
 */
@PersistenceCapable
public class Group {

	private static final int TOTAL_CAPACITY = 4;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String arrivalTime;
	
	public Group (String arrivalTime) {
		setArrivalTime(arrivalTime);
	}

	public Key getKey() {
        return key;
    }
	
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}
	
	public int size() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		int count = 0;
		try {
			Query q = pm.newQuery("select from " + User.class.getName());
			List<User> users = (List<User>) q.execute();
			Iterator<User> i = users.iterator();
			while (i.hasNext()) {
				if (i.next().getGroupKey().equals(getKey())) {
					count++;
				}
			}
			return count;
		} finally {
			pm.close();
		}
	}
	
	public boolean moreRoom () {
		if (size() < TOTAL_CAPACITY) {
			return true;
		} else {
			return false;
		}
	}
	
	public List<User> getUsers (String exceptUsername) {
		List<User> myUsers = new ArrayList<User>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery("select from " + User.class.getName());
			List<User> users = (List<User>) q.execute();
			Iterator<User> i = users.iterator();
			while (i.hasNext()) {
				User user = i.next();
				if (user.getGroupKey().equals(getKey()) && (exceptUsername == null || (exceptUsername != null && !user.getUsername().equals(exceptUsername)))) {
					myUsers.add(user);
				}
			}
		} finally {
			pm.close();
		}
		return myUsers;
	}
	
	public String toString (String username) {
		String message = "Your group contains: ";
		List<User> users = getUsers(username);
		Iterator<User> i = users.iterator();
		while (i.hasNext()) {
			User user = i.next();
			message += user.getUsername() + " " + user.getPhoneNumber() + ", ";
		}
		if (users.size() > 1) {
			message = message.substring(0, message.length() - 2);
		}
		return message;
	}
	
	public void mailGroup () {
		List<User> users = getUsers(null);
		if (users.size() == 1) {
			User user = users.get(0);
			Utility.sendSMS(user.getPhoneNumber(), "Unfortunately there were no other travellers in your group");
		} else if (users.size() > 1) {
			Iterator<User> i = users.iterator();
			while (i.hasNext()) {
				User user = i.next();
				Utility.sendSMS(user.getPhoneNumber(), toString(user.getUsername()));
			}
		}
	}
	
}
