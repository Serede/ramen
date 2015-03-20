package jp.ramen.exceptions;

public class ForbiddenAction extends RAMENException {
	private static final String text="You are not allowed to do that.";
	public ForbiddenAction() {
		super(text);
	}
}
