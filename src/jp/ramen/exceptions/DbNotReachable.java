package jp.ramen.exceptions;

public class DbNotReachable extends RAMENException {
	private static final String text = "Database is not reachable!";
	public DbNotReachable() {
		super(text);
	}
}
