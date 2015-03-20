package jp.ramen.exceptions;

public class DbAlreadyExists extends RAMENException {
	private static final String text = "Database already exists!";
	public DbAlreadyExists() {
		super(text);
	}
}
