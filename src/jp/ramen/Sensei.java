package jp.ramen;

/**
 * The Sensei class
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class Sensei extends User {

	/**
	 * Constructor
	 * @param name
	 * @param pass
	 */
	public Sensei(String name, String pass) {
		super(name, pass);
	}
	
	/**
	 * Constructor with sha-1 switch option
	 * @param name
	 * @param pass
	 * @param sha
	 */
	public Sensei(String name, String pass, boolean sha) {
		super(name,pass,sha);
	}

	@Override
	public boolean block(Entity e) {
		return this.addToBlocked(e);
	}

	@Override
	public boolean canAnswer() {
		return false;
	}

	@Override
	public String toString() {
		return "Sensei " + super.toString();
	}
}
