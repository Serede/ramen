package jp.ramen.exceptions;

public class DbNotConnected extends RAMENException {
	private static final String text = "The database is not reachable";
	public DbNotConnected() {
		super(text);
	}
}
