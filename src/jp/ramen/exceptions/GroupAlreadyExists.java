package jp.ramen.exceptions;

public class GroupAlreadyExists extends RAMENException {
	private static final long serialVersionUID = -44293881644133338L;
	private static final String text1="The group ";
	private static final String text2=" already exists!";
	public GroupAlreadyExists(String code) {
		super(text1 + code + text2);
	}
}
