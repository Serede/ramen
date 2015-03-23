package jp.ramen.exceptions;

public class InvalidLogin extends RAMENException {
	private static final long serialVersionUID = -3077451874014657861L;
	private static final String text = "Invalid User!";
	public InvalidLogin() {
		super(text);
	}
}
