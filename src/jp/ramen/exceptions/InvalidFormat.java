package jp.ramen.exceptions;

public class InvalidFormat extends RAMENException {
	private static final String text = "The file format was incorrect.";
	public InvalidFormat() {
		super(text);
	}
}
