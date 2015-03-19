/**
 * 
 */
package jp.ramen;

import java.sql.SQLException;

import jp.ramen.exceptions.*;

/**
 * @author e303132
 *
 */
public class RAMEN {
	private static RAMEN app=null;
	private User currentUser = null;
	private RAMEN() {
		
	}
	public static RAMEN getInstance() {
		if(app==null) app = new RAMEN();
		return app;
	}

	public void init(String path) {
		//TODO: groups and messages
		
		
	}

	public boolean login(String username, String password) {
		UserDAO db = UserDAO.getInstance();
		//TODO: CATCH Excp
		User u = db.getUser(username);
		if(u.checkPassword(password)==true) {
			currentUser = u;
			return true;
		}
		else {
			return false;
		}
	}
	public boolean sendMessage(Entity to, String subject, String text, boolean question) throws SQLException {
		Message msg = new Message(subject, text, currentUser, to);
		MessageDAO mdb = MessageDAO.getInstance();
		return mdb.addMessage(msg);
	}
	
	
	public boolean createGroup(String name, String desc, Group parent, boolean isPrivate, boolean moderated, boolean social) {
	//	GroupDAO db = GroupDAO.getInstance();
		Group g;
		try {
			if(social&&parent==null || parent instanceof SocialGroup)
				g = new SocialGroup(name, desc, parent, currentUser, isPrivate, moderated);
			else
				g = new StudyGroup(name, desc, parent, currentUser);
		}
		catch(RAMENException e) {
			System.err.println(e);
		}
		//return db.addGroup(g);
		return false;
	}
	
	public boolean joinGroup(Group g) throws SQLException  {
		if(g.isPrivate()==false) {
			return MessageDAO.getInstance().addJoinRequest(currentUser, g);
		}
		else {
			if (g.addMember(currentUser)==false) return false;
			if (currentUser.subscribe(g)==false) return false;
		}
		return true;
	}
	
	public boolean sendAnswer(Entity to, String subject, String text, Question q) throws SQLException {
		Message msg = new Answer(subject, text, currentUser, to, q);
		MessageDAO mdb = MessageDAO.getInstance();
		return mdb.addMessage(msg);
	}
	
	public boolean handleRequest(Request r, boolean accepted) {
		r.setRequest(accepted); //TODO: void or boolean?
		return true;
	}
	
	public boolean block(Entity e) {
		return this.currentUser.block(e);
	}
	
	public boolean unblock(Entity e) {
		return this.currentUser.unblock(e);
	}
	public User getCurrentUser() {
		return currentUser;
	}

}
