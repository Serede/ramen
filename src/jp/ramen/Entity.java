/**
 * 
 */
package jp.ramen;

/**
 * @author e303132
 *
 */
public abstract class Entity {
	private String name;
	
	public Entity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract boolean addToInbox(Message m);
	
	
}
