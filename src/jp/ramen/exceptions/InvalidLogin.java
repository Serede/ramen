package jp.ramen.exceptions;

public class InvalidLogin extends RAMENException {
	private static final String text = "Invalid User!";
	public InvalidLogin() {
		super(text);
	}
}
