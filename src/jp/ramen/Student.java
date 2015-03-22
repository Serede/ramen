package jp.ramen;

/**
 * The class student
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class Student extends User {

	/**
	 * Constructor
	 * @param name
	 * @param pass
	 */
	public Student(String name, String pass) {
		super(name, pass);
	}

	/**
	 * Constructor with sha-1 switch option
	 * @param name
	 * @param pass
	 * @param sha
	 */
	public Student(String name, String pass, boolean sha) {
		super(name,pass,sha);
	}

	@Override
	public boolean block(Entity e) {
		if(e instanceof Sensei) return false;
		return this.addToBlocked(e);
	}

	@Override
	public boolean canAnswer() {
		return true;
	}

	@Override
	public String toString() {
		return "Student " + super.toString();
	}
}
