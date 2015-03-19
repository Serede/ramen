package jp.ramen;

import java.sql.Date;

public abstract class Request extends Message {
	protected static String HEADER = null;

	/* DB */
	public Request(String subject, String text, boolean acpt, Date time) {
		super(subject, text, acpt, time);
	}
	
	public Request(String subject, String text, User author, Entity to) {
		super(subject, text, author, to);
	}


	public abstract void setRequest(boolean accepted);

}
