/**
 * 
 */
package jp.ramen;

/**
 * Abstract class entity for the shared functions between User and Group
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
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
