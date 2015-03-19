package jp.ramen.exceptions;

public class InvalidFormat extends RAMENException {
	private static final String text = "The format of the file is not the expected one";
	public InvalidFormat() {
		super(text);
	}
}
