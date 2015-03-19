/**
 * 
 */
package jp.ramen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author e303132
 *
 */
public abstract class User extends Entity {
	private String pass;
	private List<Entity> blocked;
	private List<Group> subscriptions;
	private List<LocalMessage> inbox;
	
	/* DB */
	public User(String name, String pass) {
		this(name,pass,false);
	}
	
	public User(String name, String pass, boolean sha) {
		super(name);
		this.pass = sha? UserDAO.generateSHA(pass):pass;
		this.inbox = new ArrayList<LocalMessage>();
		this.subscriptions = new ArrayList<Group>();
		this.blocked = new ArrayList<Entity>();
	}
	
	public boolean subscribe(Group g) {
		if(subscriptions.contains(g)==true) return false;
		return subscriptions.add(g);
	}

	public boolean unsubscribe(Group g) {
		if(subscriptions.contains(g)==false) return false;
		return subscriptions.remove(g);
	}

	protected boolean addToBlocked(Entity e) {
		if(blocked.contains(e)==true) return false;
		return blocked.add(e);
	}

	public abstract boolean block(Entity e);

	public boolean unblock(Entity e) {
		if(blocked.contains(e)==false) return false;
		return blocked.remove(e);
	}
	
	@Override 
	public boolean addToInbox(Message m) {
		/* blocked ? */
		if(blocked.contains(m.getAuthor())) return true;
		if(blocked.contains(m.getTo())) return true; //TODO: Review
		/* Check whether is already there*/
		for(LocalMessage local: inbox) {
			if (local.getReference()==m) return false;
		}
		this.inbox.add(new LocalMessage(m));
		return true;
	}
	
	public abstract boolean canAnswer();
	
	public List<LocalMessage> getInbox() {
		return Collections.unmodifiableList(inbox);
	}

	public boolean checkPassword(String pass) {
		return this.pass.equals(UserDAO.generateSHA(pass));
	}
	
	
}
