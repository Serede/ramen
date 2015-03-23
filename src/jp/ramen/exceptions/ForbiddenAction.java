package jp.ramen.exceptions;

public class ForbiddenAction extends RAMENException {
	private static final long serialVersionUID = 3433634798868336264L;
	private static final String text="Forbidden action:";
	public ForbiddenAction(String fault) {
		super(text + "\n\t" + fault);
	}
}
