package jp.ramen.exceptions;

public class InvalidFormat extends RAMENException {
	private static final long serialVersionUID = 7381373495648756330L;
	private static final String text = "The file format was incorrect.";
	public InvalidFormat() {
		super(text);
	}
}
