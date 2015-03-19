package jp.ramen.exceptions;

public abstract class RAMENException extends Exception {
	private static final long serialVersionUID = 1L;
	private String text;
	private static final String DEFAULT_TEXT = "Unknown error";
	
	public RAMENException(String text) {
		this.text = text;
	}
	
	public RAMENException() {
		this.text = DEFAULT_TEXT;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
