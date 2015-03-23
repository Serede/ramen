package jp.ramen.exceptions;

public class DbAlreadyExists extends RAMENException {
	private static final long serialVersionUID = 8027534420189555805L;
	private static final String text = "The database already exists.";
	public DbAlreadyExists() {
		super(text);
	}
}
