package jp.ramen.exceptions;

public class InvalidMessage extends RAMENException {
	private static final String text = "The message is not valid.";
	public InvalidMessage() {
		super(text);
	}
}
