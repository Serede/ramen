/**
 * 
 */
package jp.ramen;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import jp.ramen.exceptions.*;


public class RAMEN {
	private static RAMEN app=null;
	private static DAO ddb=null;
	private User currentUser = null;
	
	private RAMEN() {
		ddb = DAO.getInstance();
	}
	
	public static RAMEN getInstance() {
		if(app==null)
			app = new RAMEN();
		return app;
	}
	
	public DAO getDAO() {
		return ddb;
	}

	public void install(String db, String st, String pr) throws Exception {
		ddb.create(db, st, pr);
	}
	
	public void init() throws Exception {
		ddb.init();
	}

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
	
	public boolean readMessage(LocalMessage lm) throws SQLException {
		MessageDAO mdb = ddb.getMdb();
		
		mdb.readLocalMessage(currentUser, lm.getReference());
		return lm.read();
	}
	
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
	
	public boolean sendAnswer(Entity to, String subject, String text, Question q) throws SQLException, InvalidMessage, ForbiddenAction {
		Message msg = new Answer(subject, text, currentUser, to, q);
		if (currentUser.canAnswer()
				&& ((Group) to).getMembers().contains(currentUser)) {
			MessageDAO mdb = ddb.getMdb();
			return mdb.addMessage(msg);
		}
		throw new ForbiddenAction();
	}
	
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
	
	public boolean block(Entity e) throws SQLException {
		UserDAO udb = ddb.getUdb();
		return udb.addBlock(currentUser, e);
	}
	
	public boolean unblock(Entity e) throws SQLException {
		UserDAO udb = ddb.getUdb();
		return udb.delBlock(currentUser, e);
	}
	public User getCurrentUser() {
		return currentUser;
	}
	
	public Collection<User> listUsers() {
		return ddb.getUdb().listUsers();
	}
	
	public Collection<Group> listGroups() {
		return ddb.getGdb().listGroups();
	}
	
	public List<LocalMessage> getInbox() {
		return currentUser.getInbox();
	}
}
