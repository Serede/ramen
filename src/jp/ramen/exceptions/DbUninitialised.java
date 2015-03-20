package jp.ramen.exceptions;

public class DbUninitialised extends RAMENException {
	private static final String text = "Database is not initialised!";
	public DbUninitialised() {
		super(text);
	}
}
