package jp.ramen;

public class Student extends User {

	public Student(String name, String pass) {
		super(name, pass);
	}

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
