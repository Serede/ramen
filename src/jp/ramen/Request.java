package jp.ramen;

public abstract class Request extends Message {

	public Request(String subject, String text, User author, Entity to) {
		super(subject, text, author, to);
		// TODO Auto-generated constructor stub
	}
	
	public abstract void setRequest(boolean accepted);

}
