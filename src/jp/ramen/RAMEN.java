/**
 * 
 */
package jp.ramen;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import jp.ramen.exceptions.*;

/**
 * Main interface. This would be the API of the network
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class RAMEN {
	private static RAMEN app=null;
	private static DAO ddb=null;
	private User currentUser = null;
	
	/**
	 * Constructor (singleton)
	 */
	private RAMEN() {
		ddb = DAO.getInstance();
	}
	
	/**
	 * 
	 * @return the instance of the object
	 */
	public static RAMEN getInstance() {
		if(app==null)
			app = new RAMEN();
		return app;
	}
	
	/**
	 * 
	 * @return the DAO
	 */
	public DAO getDAO() {
		return ddb;
	}

	/**
	 * Installs the application
	 * @param db path to the db
	 * @param st path to the students file
	 * @param pr path to the professors file
	 * @throws Exception
	 */
	public void install(String db, String st, String pr) throws Exception {
		ddb.create(db, st, pr);
	}
	
	/**
	 * Loads the data
	 * @throws Exception
	 */
	public void init() throws Exception {
		ddb.init();
	}

	/**
	 * Login function
	 * @param username
	 * @param password
	 * @return true if it was possible, false otherwise
	 * @throws InvalidLogin
	 */
	public boolean login(String username, String password) throws InvalidLogin {
		UserDAO udb = ddb.getUdb();
		
		User u = udb.getUser(username);
		if(u==null) throw new InvalidLogin();
		if(u.checkPassword(password)==true) {
			currentUser = u;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Sends a message
	 * @param to
	 * @param subject
	 * @param text
	 * @param question true if it is a question, false otherwise
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 * @throws ForbiddenAction
	 * @throws InvalidMessage
	 */
	public boolean sendMessage(Entity to, String subject, String text, boolean question) throws SQLException, ForbiddenAction, InvalidMessage {
		Message msg = null;
		MessageDAO mdb = ddb.getMdb();
		Entity target;
		
		if(question)
			msg = new Question(subject, text, currentUser, to);
		else
			msg = new Message(subject, text, currentUser, to);
		target = msg.getTo();
		
		if (target instanceof Group && ((Group) target).getMembers().contains(currentUser)) {
			if (target instanceof SocialGroup && !question) { //if(target.isSocial())
				if (((Group) target).isModerated())
					return mdb.addMessageRequest(msg);
				else
					return mdb.addMessage(msg);
			} else if (target instanceof StudyGroup) { //else if(!target.isSocial())
				if (currentUser instanceof Sensei) {   //currentUser.canSendMessagesStudyGroup(study=true)
					if (!question)
						return mdb.addMessage(msg);
					else if (question && ((Group) target).getOwner().equals(currentUser)) 
						return mdb.addMessage(msg);
					else
						throw new ForbiddenAction();
				} else
					throw new ForbiddenAction();
			} else
				throw new ForbiddenAction();
		} else if (target instanceof User) { //????
			return mdb.addMessage(msg);
		} else
			throw new ForbiddenAction();
	}
	
	/**
	 * Reads a localMessage
	 * @param lm
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean readMessage(LocalMessage lm) throws SQLException {
		MessageDAO mdb = ddb.getMdb();
		
		mdb.readLocalMessage(currentUser, lm.getReference());
		return lm.read();
	}
	
	/**
	 * Creates a group
	 * @param name
	 * @param desc
	 * @param supg
	 * @param social
	 * @param priv
	 * @param mod
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 * @throws ForbiddenAction
	 */
	public boolean createGroup(String name, String desc, Group supg, boolean social, boolean priv, boolean mod) throws SQLException, ForbiddenAction {
		Group g = null;
		GroupDAO gdb = ddb.getGdb();
		
		if(social && supg==null || supg instanceof SocialGroup) 
			//check if the owner is the same in the supg
			g = new SocialGroup(name, desc, supg, currentUser, priv, mod);
		else
			g = new StudyGroup(name, desc, supg, currentUser);
		
		gdb.addGroup(g);
		
		return true;
	}
	
	/**
	 * Joins a group
	 * @param g
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	public boolean joinGroup(Group g) throws SQLException, InvalidMessage  {
		GroupDAO gdb = ddb.getGdb();
		MessageDAO mdb = ddb.getMdb();
		
		if(g.isPrivate()) {
			return mdb.addJoinRequest(currentUser, g);
		}
		else {
			return gdb.addMember(g, currentUser);
		}
	}
	
	/**
	 * Sends a answer to the question
	 * @param to
	 * @param subject
	 * @param text
	 * @param q
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 * @throws InvalidMessage
	 * @throws ForbiddenAction
	 */
	public boolean sendAnswer(Entity to, String subject, String text, Question q) throws SQLException, InvalidMessage, ForbiddenAction {
		Message msg = new Answer(subject, text, currentUser, to, q);
		if (currentUser.canAnswer()
				&& ((Group) to).getMembers().contains(currentUser)) {
			MessageDAO mdb = ddb.getMdb();
			return mdb.addMessage(msg);
		}
		throw new ForbiddenAction();
	}
	
	/**
	 * Handles the request
	 * @param r
	 * @param accepted
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean handleRequest(Request r, boolean accepted) throws SQLException {
		MessageDAO mdb = ddb.getMdb();
		GroupDAO gdb = ddb.getGdb();

		if(accepted) {
			if(r instanceof MessageRequest) { //r.requestingMessage()
				mdb.acceptMessage((MessageRequest) r);
				mdb.delLocalMessage(currentUser, r);
			} else if(r instanceof JoinRequest) { //r,requestingGroup()
				gdb.addMember(((JoinRequest) r).getRef(), r.getAuthor());
				r.setRequest(accepted);
				mdb.delLocalMessage(currentUser, r);
			}
		} else {
			mdb.delLocalMessage(currentUser, r);
			r.setRequest(accepted);
		}
		return true;
	}
	
	/**
	 * Blocks an entity
	 * @param e
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean block(Entity e) throws SQLException {
		UserDAO udb = ddb.getUdb();
		return udb.addBlock(currentUser, e);
	}
	
	/**
	 * Unblocks an entity
	 * @param e
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean unblock(Entity e) throws SQLException {
		UserDAO udb = ddb.getUdb();
		return udb.delBlock(currentUser, e);
	}
	
	/**
	 * 
	 * @return the user using the application
	 */
	public User getCurrentUser() {
		return currentUser;
	}
	
	/**
	 * 
	 * @return the list of users
	 */
	public Collection<User> listUsers() {
		return ddb.getUdb().listUsers();
	}
	
	/**
	 * 
	 * @return the list of groups
	 */
	public Collection<Group> listGroups() {
		return ddb.getGdb().listGroups();
	}
	
	/**
	 * 
	 * @return the inbox of the user
	 */
	public List<LocalMessage> getInbox() {
		return currentUser.getInbox();
	}
}
