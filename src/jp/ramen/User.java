/**
 * 
 */
package jp.ramen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract class of an user of the system
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public abstract class User extends Entity {
	private String pass;
	private List<Entity> blocked;
	private List<Group> subscriptions;
	private List<LocalMessage> inbox;
	private List<Message> sent;
	
	/**
	 * DB constructor
	 * @param name
	 * @param pass
	 */
	public User(String name, String pass) {
		this(name,pass,false);
	}
	
	/**
	 * Constructor
	 * @param name
	 * @param pass
	 * @param sha
	 */
	public User(String name, String pass, boolean sha) {
		super(name);
		this.pass = sha? UserDAO.generateSHA(pass):pass;
		this.inbox = new ArrayList<LocalMessage>();
		this.subscriptions = new ArrayList<Group>();
		this.blocked = new ArrayList<Entity>();
		this.sent = new ArrayList<>();
	}
	
	/**
	 * Subscribes to a group
	 * @param g
	 * @return true if it was possible, false otherwise
	 */
	public boolean subscribe(Group g) {
		if(subscriptions.contains(g)==true) return false;
		return subscriptions.add(g);
	}

	/**
	 * Quits froma group 
	 * @param g
	 * @return true if it was possible, false otherwise
	 */
	public boolean unsubscribe(Group g) {
		if(subscriptions.contains(g)==false) return false;
		return subscriptions.remove(g);
	}
	
	/**
	 * Adds to blocked
	 * @param e
	 * @return true if it was possible, false otherwise
	 */
	protected boolean addToBlocked(Entity e) {
		if(blocked.contains(e)==true) return false;
		return blocked.add(e);
	}

	/**
	 * Blocks the entity
	 * @param e
	 * @return true if it was possible, false otherwise
	 */
	public abstract boolean block(Entity e);

	/**
	 * Unblocks the entity
	 * @param e
	 * @return
	 */
	public boolean unblock(Entity e) {
		if(blocked.contains(e)==false) return false;
		return blocked.remove(e);
	}
	
	//TODO: USED??
	/**
	 * Adds to sent the message
	 * @param m
	 * @return true if it was possible, false otherwise
	 */ 
	public boolean addSent(Message m) {
		if(sent.contains(m)) return false;
		return sent.add(m);
	}
	
	@Override 
	public boolean addToInbox(Message m){
		return addToInbox(m,false);
	}
	
	/**
	 * Adds to inbox the message with read state
	 * @param m
	 * @param read
	 * @return true if it was possible, false otherwise
	 */
	public boolean addToInbox(Message m, boolean read) {
		/* blocked ? */
		if(blocked.contains(m.getAuthor())) return true;
		if(blocked.contains(m.getTo())) return true; //TODO: Review
		
		/* Check whether is already there*/
		for(LocalMessage local: inbox) {
			if (local.getReference()==m) return false;
		}
		
		LocalMessage lm = new LocalMessage(m);
		if(read) lm.read();
		
		this.inbox.add(lm);
		return true;
	}
	
	/**
	 * Deletes from inbox given the message
	 * @param lm
	 * @return true if it was possible, false otherwise
	 */
	public boolean delFromInbox(LocalMessage lm) {
		if(!inbox.contains(lm)) return false;

		inbox.remove(lm);
		return true;
	}
	
	/**
	 * 
	 * @return if the user can answer a question
	 */
	public abstract boolean canAnswer();
	
	/**
	 * 
	 * @return the inbox of a user
	 */
	public List<LocalMessage> getInbox() {
		return Collections.unmodifiableList(inbox);
	}

	/**
	 * Checks the user password
	 * @param pass
	 * @return
	 */
	public boolean checkPassword(String pass) {
		return this.pass.equals(UserDAO.generateSHA(pass));
	}
	
	public List<Entity> getBlocked() {
		return Collections.unmodifiableList(blocked);
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
}
