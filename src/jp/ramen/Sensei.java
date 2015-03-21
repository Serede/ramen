package jp.ramen;

public class Sensei extends User {

	public Sensei(String name, String pass) {
		super(name, pass);
	}
	
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
