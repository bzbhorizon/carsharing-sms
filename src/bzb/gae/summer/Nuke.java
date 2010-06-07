/**
 * 
 */
package bzb.gae.summer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bzb.gae.PMF;
import bzb.gae.jdo.App;
import bzb.gae.summer.jdo.Group;
import bzb.gae.summer.jdo.User;

/**
 * @author psxbdb
 * 
 */
@SuppressWarnings("serial")
public class Nuke extends HttpServlet {

	private static final Logger log = Logger
			.getLogger(Nuke.class.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		log.warning("Deleting all data");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery("select from " + App.class.getName());
			List<App> apps = (List<App>) q.execute();
			pm.deletePersistentAll(apps);
			
			q = pm.newQuery("select from " + Group.class.getName());
			List<Group> groups = (List<Group>) q.execute();
			pm.deletePersistentAll(groups);
			
			q = pm.newQuery("select from " + User.class.getName());
			List<User> users = (List<User>) q.execute();
			pm.deletePersistentAll(users);
		} finally {
			pm.close();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
