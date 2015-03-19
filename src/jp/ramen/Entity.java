/**
 * 
 */
package jp.ramen;

import java.util.*;

/**
 * @author e303132
 *
 */
public abstract class Entity {
	private String name;
	private List<Message> messages;
	
	/**
	 * @param name
	 */
	public Entity(String name) {
		this.name = name;
		this.messages = new ArrayList<Message>();
	}
	
	public boolean addToMessages(Message m) {
		if(this.messages.contains(m) == true) return false;
		return messages.add(m);
	}

	public String getName() {
		return name;
	}

	public boolean isModerated() {
		return false;
	}

	public abstract boolean addToInbox(Message m);
	
	
}
