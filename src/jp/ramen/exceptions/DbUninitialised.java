package jp.ramen.exceptions;

public class DbUninitialised extends RAMENException {
	private static final long serialVersionUID = -3456619083719248962L;
	private static final String text = "The database is not initialised.";
	public DbUninitialised() {
		super(text);
	}
}
