package jp.ramen.exceptions;

public class NotEnoughPermissions extends RAMENException {
	private static final String text="You can not do this";
	public NotEnoughPermissions() {
		super(text);
	}
}
