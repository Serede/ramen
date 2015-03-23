package jp.ramen.exceptions;

public class DbNotReachable extends RAMENException {
	private static final long serialVersionUID = -6919194641688475956L;
	private static final String text = "The database is not reachable.";
	public DbNotReachable() {
		super(text);
	}
}
